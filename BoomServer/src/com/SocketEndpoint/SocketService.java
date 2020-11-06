package com.SocketEndpoint;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.DAO.Connector;
import com.DAO.UserDAO;
import com.Model.User;
import com.Util.TagName;

@ServerEndpoint(value = "/websocket/users/online/{username}/{password}")
public class SocketService {
	static Set<Session> users_online = Collections.synchronizedSet(new HashSet<Session>());
	
	@OnOpen
	public void handleOpen(Session userSession, @PathParam("username") String username, @PathParam("password") String password){
		UserDAO userDAO = new UserDAO();
		User user = userDAO.getUser(username, password);
		if(user == null){
			try {		
				userSession.getBasicRemote().sendText("\"tag\":"+ TagName.getLoginFalse());
				userSession.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			//add info user here
			userSession.getUserProperties().put("user", user);
			
			//response
			try {
				//response to user
				userSession.getBasicRemote().sendText("\"tag\":"+ TagName.getLoginSuccess());
				
				//add user
				addOrRemoveUser("add", userSession);
					
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@OnMessage
	public void handleMessage(String message, Session userSession) throws IOException{
		
	}
	
	@OnClose
	public void handleClose(Session userSession){
		addOrRemoveUser("remove", userSession);
	}
	
	@OnError
	public void handleError(Throwable t, Session userSession){
		t.printStackTrace();
	}
	
	
	private void addOrRemoveUser(String code, Session userSession){
		if(code == "add"){
			users_online.add(userSession);
		}
		else{
			if(users_online.contains(userSession)) users_online.remove(userSession);
		}
		
		//send update users list to users
		JSONObject usersList = userListJSON();
		usersList.put("tag", TagName.getUpdateUsers());
		for(Session session : users_online){
			try {
				session.getBasicRemote().sendText(usersList.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private JSONObject userListJSON(){
		List<User> users = new ArrayList<User>();
		for(Session session : users_online){
			users.add((User) session.getUserProperties().get("user"));
		}

		JSONObject json = new JSONObject();
		json.put("users", users);
			
		return json;	
	}
	
}
