package com.SocketEndpoint;

import java.time.LocalDateTime;

import javax.websocket.Session;

import org.json.simple.JSONObject;

import com.Model.User;
import com.Util.Cryptor;
import com.Util.TagName;

public class HandleMessage {
	
	//handle userA invite userB
	public static void handleRequestCompare(Session userASession, Session userBSession){
		//userB not online
		if(userBSession == null){
			try {		
				JSONObject response = new JSONObject();
				response.put("tag", TagName.getDeniedCompare());
				userASession.getBasicRemote().sendText(response.toString());
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		//if userB online
		else{
			try {		
				JSONObject response = new JSONObject();
				response.put("tag", TagName.getRequestInvite());
				User userA = (User) userASession.getUserProperties().get("user");
				response.put("inviter_name", userA.getName());
				response.put("inviter_id", userA.getId());
				userBSession.getBasicRemote().sendText(response.toString());
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}
	
	//handle user B accept invite
	public static void handleAcceptInvite(Session userASession, Session userBSession){
		User userA = (User) userASession.getUserProperties().get("user");
		User userB = (User) userBSession.getUserProperties().get("user");
		
		String time = LocalDateTime.now().toString();
		
		userA.setStatus(time);
		userB.setStatus(time);
		
		//create data game
		
		
		//send
		try {		
			JSONObject responseToUserA = new JSONObject();
			JSONObject responseToUserB = new JSONObject();
			
			
			responseToUserA.put("tag", TagName.getGameData());		
			responseToUserA.put("beginTime", Cryptor.getAESEncrypt(time));
			responseToUserA.put("idCompetior", Cryptor.getAESEncrypt(Integer.toString(userB.getId())));
			
			responseToUserB.put("tag", TagName.getGameData());
			responseToUserB.put("beginTime", Cryptor.getAESEncrypt(time));
			responseToUserB.put("idCompetior", Cryptor.getAESEncrypt(Integer.toString(userA.getId())));
			
			
			//add data game
			responseToUserA.put("data game to A", "test");	
			responseToUserB.put("data game to B", "test");	
			
			//send to 2 users
			userASession.getBasicRemote().sendText(responseToUserA.toString());
			userBSession.getBasicRemote().sendText(responseToUserB.toString());
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public static void handleRefuseInvite(Session userASession){
		try {		
			JSONObject response = new JSONObject();
			response.put("tag", TagName.getRefuseCompare());
			userASession.getBasicRemote().sendText(response.toString());
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public static void handleCompleteGame(){
			
		}
	
	public static void handleDefeatGame(){
		
	}
	
	public static void handlerRematchGame(){
		
	}
	
}
