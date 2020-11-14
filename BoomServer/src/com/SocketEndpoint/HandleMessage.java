package com.SocketEndpoint;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.websocket.Session;

import org.json.simple.JSONObject;

import com.DAO.HistoryDAO;
import com.DAO.UserDAO;
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
		
		//save history to database
		HistoryDAO history = new HistoryDAO();
		int idHis = history.createHistory(userA.getId(), userB.getId(), userA.getName(), userB.getName(), time);
		
		//create data game

		
		//send
		try {		
			JSONObject responseToUserA = new JSONObject();
			JSONObject responseToUserB = new JSONObject();
			
			
			responseToUserA.put("tag", TagName.getGameData());		
			responseToUserA.put("beginTime", Cryptor.getAESEncrypt(time));
			responseToUserA.put("idCompetitor", Cryptor.getAESEncrypt(Integer.toString(userB.getId())));
			responseToUserA.put("idHistory", idHis);
			
			responseToUserB.put("tag", TagName.getGameData());
			responseToUserB.put("beginTime", Cryptor.getAESEncrypt(time));
			responseToUserB.put("idCompetitor", Cryptor.getAESEncrypt(Integer.toString(userA.getId())));
			responseToUserB.put("idHistory", idHis);
			
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
	
	//handle user B refuse invite
	public static void handleRefuseInvite(Session userASession){
		try {		
			JSONObject response = new JSONObject();
			response.put("tag", TagName.getRefuseCompare());
			userASession.getBasicRemote().sendText(response.toString());
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	//if userA complete game
	public static boolean handleCompleteGame(Session userSession, Session competitorSession, String beginTimeEncrypted, int id){
		String beginTime = Cryptor.getAESDecrypt(beginTimeEncrypted);
		User competitorUser = (User) competitorSession.getUserProperties().get("user");
		User user = (User) userSession.getUserProperties().get("user");
		
		//if begin time is same
		if(competitorUser.getStatus().equals(beginTime)){
			//notice to competitor
			JSONObject response = new JSONObject();
			response.put("tag", TagName.getYouLose());
			try {
				competitorSession.getBasicRemote().sendText(response.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//update point
			user.setPoint(user.getPoint()+1);
			if(competitorUser.getPoint() != 0){
				competitorUser.setPoint(competitorUser.getPoint()-1);	
			}
			
			//update status 2 users
			user.setStatus("online");
			competitorUser.setStatus("online");
			
			//save point and history to database
			HistoryDAO history = new HistoryDAO();
			history.updateHistory(id, Integer.toString(user.getId()));
			UserDAO userDAO = new UserDAO();
			userDAO.updatePoint(user.getId(), competitorUser.getId(), user.getPoint(), competitorUser.getPoint());
			
			return true;
		}
		
		//else
		else{
			JSONObject response = new JSONObject();
			response.put("tag", TagName.getFalseComplete());
			try {
				userSession.getBasicRemote().sendText(response.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return false;
		}
	}
	
	//if userA defeat game
	public static boolean handleDefeatGame(Session userSession, Session competitorSession, String beginTimeEncrypted, int id){
		String beginTime = Cryptor.getAESDecrypt(beginTimeEncrypted);
		User competitorUser = (User) competitorSession.getUserProperties().get("user");
		User user = (User) userSession.getUserProperties().get("user");
		
		//if begin time is same
		if(competitorUser.getStatus().equals(beginTime)){
			//notice to competitor
			JSONObject response = new JSONObject();
			response.put("tag", TagName.getYouWin());
			try {
				competitorSession.getBasicRemote().sendText(response.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//update point
			competitorUser.setPoint(competitorUser.getPoint()+1);
			if(user.getPoint() != 0){
				user.setPoint(user.getPoint()-1);	
			}
			
			//update status 2 users
			user.setStatus("online");
			competitorUser.setStatus("online");
			
			//save point and history to database
			HistoryDAO history = new HistoryDAO();
			history.updateHistory(id, Integer.toString(competitorUser.getId()));
			UserDAO userDAO = new UserDAO();
			userDAO.updatePoint(user.getId(), competitorUser.getId(), user.getPoint(), competitorUser.getPoint());
			
			return true;
		}
		
		//else
		else{
			JSONObject response = new JSONObject();
			response.put("tag", TagName.getFalseComplete());
			try {
				userSession.getBasicRemote().sendText(response.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return false;
		}
	}
	
	public static void handlerRematchGame(Session inviterSession, Session competitorSession){
		//if competitor offline or ingame
		if(competitorSession == null){
			JSONObject response = new JSONObject();
			response.put("tag", TagName.getRematchRefuse());
			try {
				inviterSession.getBasicRemote().sendText(response.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//else
		else{
			User inviter = (User) inviterSession.getUserProperties().get("user");
			
			JSONObject response = new JSONObject();
			response.put("tag", TagName.getRequestContinue());
			response.put("idInviter", inviter.getId());
			response.put("nameInviter", inviter.getName());
			try {
				competitorSession.getBasicRemote().sendText(response.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
