package com.SocketEndpoint;

import java.io.IOException;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.DAO.UserDAO;
import com.Model.User;
import com.Util.Cryptor;
import com.Util.TagName;

@ServerEndpoint(value = "/websocket/users/online/{username}/{password}")
public class SocketService {
	static Set<Session> users_online = Collections.synchronizedSet(new HashSet<Session>());
	
	@OnOpen
	public void handleOpen(Session userSession, @PathParam("username") String username, @PathParam("password") String password){
		
		UserDAO userDAO = new UserDAO();
		User user = userDAO.login(username, password);
		//if login false
		if(user == null){
			try {		
				JSONObject response = new JSONObject();
				response.put("tag", TagName.getLoginFalse());
				userSession.getBasicRemote().sendText(response.toString());
				userSession.close();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		//login success
		else{
			//add info user here
			user.setStatus("online");
			userSession.getUserProperties().put("user", user);
			
			//response
			try {
				//response to user
				JSONObject response = new JSONObject();
				response.put("tag", TagName.getLoginSuccess());
				userSession.getBasicRemote().sendText(response.toString());
				
				//add user
				addOrRemoveUser("add", userSession);
					
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@OnMessage
	public void handleMessage(String message, Session userSession) throws IOException{
		try {
			JSONObject data = null;
			Object data_tmp =  new JSONParser().parse(message);
			if(data_tmp instanceof JSONObject){
				data = (JSONObject) data_tmp;
			}
			
			//if userA invite userB
			if(data.get("tag").equals(TagName.getRequestCompare())){
				int idUserB = Integer.parseInt((data.get("idUserB").toString()));
				Session userBSession = getUserByID(idUserB);
				User tmp = (User) userBSession.getUserProperties().get("user");
				if(!tmp.getStatus().equals("online")){
					userBSession = null;
				}
				HandleMessage.handleRequestCompare(userSession, userBSession);			
			}
			
			//if userB accept invite from userA
			else if(data.get("tag").equals(TagName.getAcceptInvite())){
				int idUserA = Integer.parseInt((data.get("idUserA").toString()));
				Session userASession = getUserByID(idUserA);
				User tmp = (User) userASession.getUserProperties().get("user");
				if(!tmp.getStatus().equals("online")){
					userASession = null;
				}
				HandleMessage.handleAcceptInvite(userASession, userSession);
			}
			
			//if userB refuse invite from userA
			else if(data.get("tag").equals(TagName.getRefuseInvite())){
				int idUserA = Integer.parseInt((data.get("idUserA").toString()));
				Session userASession = getUserByID(idUserA);
				User tmp = (User) userASession.getUserProperties().get("user");
				if(!tmp.getStatus().equals("online")){
					userASession = null;
				}
				HandleMessage.handleRefuseInvite(userASession);
			}	
			
			//if userA complete game
			else if(data.get("tag").equals(TagName.getCompleteGame())){
				String idCompetitorEncrypted = data.get("idCompetitor").toString();
				String idCompetitor = Cryptor.getAESDecrypt(idCompetitorEncrypted);
				int id = Integer.parseInt(idCompetitor);
				Session competitorSession = getUserByID(id);	
				
				String beginTime = data.get("beginTime").toString();
				int idHistory = Integer.parseInt((data.get("idHistory").toString()));
				boolean check = HandleMessage.handleCompleteGame(userSession, competitorSession, beginTime, idHistory);	
				if(check){
					updateUsers();
				}
			}
			
			//if userA defeat game
			else if(data.get("tag").equals(TagName.getDefeatGame())){
				String idCompetitorEncrypted = data.get("idCompetitor").toString();
				String idCompetitor = Cryptor.getAESDecrypt(idCompetitorEncrypted);
				int id = Integer.parseInt(idCompetitor);
				Session competitorSession = getUserByID(id);
				
				if(competitorSession != null){
					String beginTime = data.get("beginTime").toString();
					int idHistory = Integer.parseInt((data.get("idHistory").toString()));
					boolean check = HandleMessage.handleDefeatGame(userSession, competitorSession, beginTime, idHistory);	
					if(check){
						updateUsers();
					}
				}	
			}
			
			//if userA want rematch game
			else if(data.get("tag").equals(TagName.getRematchGame())){
				String idCompetitorEncrypted = data.get("idCompetitor").toString();
				String idCompetitor = Cryptor.getAESDecrypt(idCompetitorEncrypted);
				int id = Integer.parseInt(idCompetitor);
				Session competitorSession = getUserByID(id);
				User tmp = (User) competitorSession.getUserProperties().get("user");
				if(!tmp.getStatus().equals("online")){
					competitorSession = null;
				}
				HandleMessage.handlerRematchGame(userSession, competitorSession);
				
			}
			
			else if(data.get("tag") == "<>"){
				
			}
			else if(data.get("tag") == "<>"){
				
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	@OnClose
	public void handleClose(Session userSession){
		addOrRemoveUser("remove", userSession);
	}
	
	@OnError
	public void handleError(Throwable t, Session userSession){
		t.printStackTrace();
	}
	
	
	
	//method handle area
	
	
	private void addOrRemoveUser(String code, Session userSession){
		if(code == "add"){
			users_online.add(userSession);
		}
		else{
			if(users_online.contains(userSession)) users_online.remove(userSession);
		}
		
		updateUsers();
	}
	
	//send update users list to users
	private void updateUsers(){
		JSONObject usersList = userListToJSON();
		usersList.put("tag", TagName.getUpdateUsers());
		for(Session session : users_online){
			try {
				session.getBasicRemote().sendText(usersList.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//change list users online to json
	private JSONObject userListToJSON(){
		List<User> users = new ArrayList<User>();
		for(Session session : users_online){
			users.add((User) session.getUserProperties().get("user"));
		}

		JSONObject json = new JSONObject();
		json.put("users", users);
			
		return json;	
	}
	
	//get user in list users online
	private Session getUserByID(int id){
		Session userB = null;
		for(Session session: users_online){
			User usr = (User) session.getUserProperties().get("user");
			if(usr.getId() == id){
				userB = session;
				break;	
			}
		}
		return userB;
	}
	
}
