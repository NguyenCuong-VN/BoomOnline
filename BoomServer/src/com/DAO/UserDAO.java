package com.DAO;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.Model.User;
import com.Util.Cryptor;
import com.Util.TagName;
import com.Util.Validate;

public class UserDAO {
	Connection conn = Connector.getConnector();
	
	public User login(String username, String password){
		User user = null;
		
		//if username and passwd is validated
		if(Validate.checkUsernameAndPasswd(username) && Validate.checkUsernameAndPasswd(password)){
			//hash passwd
			password = Cryptor.getSHA1Hash(password);

			//connect db
			try {
				PreparedStatement pre = conn.prepareStatement("SELECT * FROM user WHERE username=? and password=?;");
				pre.setString(1, username);
				pre.setString(2, password);
				ResultSet rs = pre.executeQuery();	
				if(rs.next()){
					user = new User(rs.getInt("id"), rs.getString("name"), rs.getString("username"), rs.getString("password"), rs.getInt("point"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return user;
	}
	
	public void updatePoint(int idUserA, int idUserB, int pointA, int pointB){
		try {
			PreparedStatement pre = conn.prepareStatement("UPDATE user SET point = ? WHERE id = ?;");
			pre.setInt(1, pointA);
			pre.setInt(2, idUserA);
			int rs = pre.executeUpdate();	
			
			pre.setInt(1, pointB);
			pre.setInt(2, idUserB);
			rs = pre.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Map getRankPoint(){
		Map<Integer, String> data = new HashMap<Integer, String>();
		int rank=1;
		
		//connect db
		try {
			PreparedStatement pre = conn.prepareStatement("SELECT * FROM user ORDER BY user.point DESC;");
			ResultSet rs = pre.executeQuery();	
			while(rs.next()){
				User user = new User(rs.getInt("id"), rs.getString("name"), rs.getString("username"), rs.getString("password"), rs.getInt("point"));
				data.put(rank, user.getRankView().toString());
				rank++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return data;
	}	
	
	public Map getAverageRank(){
		Map<Integer, String> data = new HashMap<Integer, String>();
		int rank=1;
		
		//connect db
		try {
			CallableStatement stmt = conn.prepareCall("{CALL average_rank()}");
			ResultSet rs = stmt.executeQuery();	
			while(rs.next()){
				User user = new User(rs.getInt("id_user"), rs.getString("name"), rs.getString("username"), rs.getString("password"), rs.getInt("point"));
				JSONObject json = user.getRankView();
				json.put("average", rs.getFloat("trung binh"));
				data.put(rank, json.toString());
				rank++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return data;
	}
	
	public Map getAverageTime(){
		Map<Integer, String> data = new HashMap<Integer, String>();
		int rank=1;
		
		//connect db
		try {
			CallableStatement stmt = conn.prepareCall("{CALL average_time()}");
			ResultSet rs = stmt.executeQuery();	
			while(rs.next()){
				User user = new User(rs.getInt("id"), rs.getString("name"), rs.getString("username"), rs.getString("password"), rs.getInt("point"));
				JSONObject json = user.getRankView();
				json.put("average", rs.getFloat("trung binh"));
				data.put(rank, json.toString());
				rank++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return data;
	}
	
	private boolean checkUsername(String username){
		if(Validate.checkUsernameAndPasswd(username)){
			try {
				PreparedStatement pre = conn.prepareStatement("SELECT * FROM boomonline.user WHERE user.username = ?;");
				pre.setString(1, username);
				ResultSet rs = pre.executeQuery();	
				if(rs.next()){
					return false;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return true;
		}
		else return false;
	}
	
	public boolean createUser(String name, String username, String password){
		boolean user = this.checkUsername(username);
		if(user){
			//hash passwd
			password = Cryptor.getSHA1Hash(password);
		
			//connect db
			try {
				PreparedStatement pre = conn.prepareStatement("INSERT INTO boomonline.user (name, username, password) VALUES (?,?,?);");
				pre.setString(1, name);
				pre.setString(2, username);
				pre.setString(3, password);
				int rs = pre.executeUpdate();	
				if(rs != 0){
					return true;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
		
}
