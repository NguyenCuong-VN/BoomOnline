package com.DAO;

import java.sql.Connection;
import java.sql.DriverManager;

public class Connector {
	private static String DB_URL = "jdbc:mysql://localhost:3306/boomonline";
	private static String USERNAME = "cuong";
	private static String PASSWD = "Cuong113";
	
	public static Connection getConnector(){
		Connection conn = null;
		
		try {
			conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWD);
		} catch (Exception  e) {
			e.printStackTrace();
		}
		
		return conn;
	}

}
