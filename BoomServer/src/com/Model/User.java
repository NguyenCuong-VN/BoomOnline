package com.Model;

public class User {
	private int id;
	private String name;
	private String username;
	private String password;
	private int point;
	

	public User() {
		super();
	}

	public User(int id, String name, String username, String password, int point) {
		super();
		this.id = id;
		this.name = name;
		this.username = username;
		this.password = password;
		this.point = point;
	}

	@Override
	public String toString() {
		return "{\"id\":" + this.id + ", \"name\":\"" + this.name + "\", \"point\":" + this.point + "}";
	}
	
	
	
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getPoint() {
		return point;
	}
	public void setPoint(int point) {
		this.point = point;
	}
	
	
}
