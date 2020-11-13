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
			//password = SHA1.getSHA1Hash(password);
			
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
	
}
