package com.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.Model.User;

public class HistoryDAO {
	Connection conn = Connector.getConnector();
	
	public int createHistory(int id_userA, int id_userB, String name_userA, String name_userB, String time){	
		int result = 0;
		try {
			PreparedStatement pre = conn.prepareStatement("INSERT INTO history (id_userA, id_userB, name_userA, name_userB, time) VALUES (?,?,?,?,?);");
			pre.setInt(1, id_userA);
			pre.setInt(2, id_userB);
			pre.setString(3, name_userA);
			pre.setString(4, name_userB);
			pre.setString(5, time);
			int rs = pre.executeUpdate();	
			
			pre = conn.prepareStatement("SELECT * FROM history WHERE id_userA=? and id_userB=? and time=?;");
			pre.setInt(1, id_userA);
			pre.setInt(2, id_userB);
			pre.setString(3, time);
			ResultSet rst = pre.executeQuery();
			if(rst.next()){
				result = rst.getInt("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return result;
	}	
	
	public void updateHistory(int id, String result, int end_time){
		try {
			PreparedStatement pre = conn.prepareStatement("UPDATE history SET result = ? , end_time = ? WHERE id = ?;");
			pre.setString(1, result);
			pre.setInt(2, end_time);
			pre.setInt(3, id);
			int rs = pre.executeUpdate();	
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	
}
