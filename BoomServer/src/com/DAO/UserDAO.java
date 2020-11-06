package com.DAO;

import java.sql.Connection;

import com.Model.User;

public class UserDAO {
	Connection conn = Connector.getConnector();
	
	public User getUser(String username, String password){
		User user = null;
		
		return user;
	}
	
}
