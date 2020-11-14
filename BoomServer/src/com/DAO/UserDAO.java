package com.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.Model.User;
import com.Util.Cryptor;
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
	
}
