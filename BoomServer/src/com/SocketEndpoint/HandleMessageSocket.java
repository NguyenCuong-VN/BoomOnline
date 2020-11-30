package com.SocketEndpoint;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

import javax.websocket.Session;

import org.json.simple.JSONObject;

import com.DAO.HistoryDAO;
import com.DAO.UserDAO;
import com.Model.User;
import com.Util.Cryptor;
import com.Util.DataGame;
import com.Util.KeyValue;
import com.Util.TagName;

public class HandleMessageSocket {
	
	//handle userA invite userB
	public static void handleRequestCompare(KeyValue<User, Socket> userAPair, KeyValue<User, Socket> userBPair){

		User userA = userAPair.getKey();
		Socket userASocket = userAPair.getValue();
		
		
		//userB not online
		if(userBPair == null){
			try {		
				JSONObject response = new JSONObject();
				response.put("tag", TagName.getDeniedCompare());
			
				//tao io stream
				BufferedWriter osA = new BufferedWriter(new OutputStreamWriter(userASocket.getOutputStream()));
				
				 osA.write(response.toString());
                 osA.newLine();
                 osA.flush();
				
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		
		
		//if userB online
		else{
			User userB = userBPair.getKey();
			Socket userBSocket = userBPair.getValue();
			try {		
				JSONObject response = new JSONObject();
				response.put("tag", TagName.getRequestInvite());
				response.put("inviter_name", userA.getName());
				response.put("inviter_id", userA.getId());
				
				//tao io stream
				BufferedWriter osB = new BufferedWriter(new OutputStreamWriter(userBSocket.getOutputStream()));
				
				osB.write(response.toString());
				osB.newLine();
				osB.flush();

			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}
	
	//handle user B accept invite
	public static void handleAcceptInvite(KeyValue<User, Socket> userAPair, KeyValue<User, Socket> userBPair){
		
		User userA = userAPair.getKey();
		Socket userASocket = userAPair.getValue();
		User userB = userBPair.getKey();
		Socket userBSocket = userBPair.getValue();
		
		String time = LocalDateTime.now().toString();
		
		userA.setStatus(time);
		userB.setStatus(time);
		
		//save history to database
		HistoryDAO history = new HistoryDAO();
		int idHis = history.createHistory(userA.getId(), userB.getId(), userA.getName(), userB.getName(), time);
		
		//create data game  [8x10, 10 booms]
		DataGame dataGame = new DataGame();
		String data = dataGame.getDataGame();
		
		//send
		try {		
			JSONObject responseToUserA = new JSONObject();
			JSONObject responseToUserB = new JSONObject();
			
			
			responseToUserA.put("tag", TagName.getGameData());		
			responseToUserA.put("beginTime", Cryptor.getAESEncrypt(time));
			responseToUserA.put("idCompetitor", Cryptor.getAESEncrypt(Integer.toString(userB.getId())));
			responseToUserA.put("idHistory", idHis);
			responseToUserA.put("nameCompetitor", userB.getName());
			
			responseToUserB.put("tag", TagName.getGameData());
			responseToUserB.put("beginTime", Cryptor.getAESEncrypt(time));
			responseToUserB.put("idCompetitor", Cryptor.getAESEncrypt(Integer.toString(userA.getId())));
			responseToUserB.put("idHistory", idHis);
			responseToUserB.put("nameCompetitor", userA.getName());
			
			//add data game
			responseToUserA.put("datagame", data);	
			responseToUserB.put("datagame", data);	
			
			//send to 2 users
			BufferedWriter osA = new BufferedWriter(new OutputStreamWriter(userASocket.getOutputStream()));
			osA.write(responseToUserA.toString());
            osA.newLine();
            osA.flush();
            
            BufferedWriter osB = new BufferedWriter(new OutputStreamWriter(userBSocket.getOutputStream()));
            osB.write(responseToUserB.toString());
            osB.newLine();
            osB.flush();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	//handle user B refuse invite
	public static void handleRefuseInvite(KeyValue<User, Socket> userAPair){
		try {		
			JSONObject response = new JSONObject();
			response.put("tag", TagName.getRefuseCompare());
			
			BufferedWriter osA = new BufferedWriter(new OutputStreamWriter(userAPair.getValue().getOutputStream()));
			osA.write(response.toString());
            osA.newLine();
            osA.flush();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	//if userA complete game
	public static boolean handleCompleteGame(KeyValue<User, Socket> userPair, KeyValue<User, Socket> competitorPair, String beginTimeEncrypted, int id){
		
		User user = userPair.getKey();
		Socket userSocket = userPair.getValue();
		User competitorUser = competitorPair.getKey();
		Socket competitorSocket = competitorPair.getValue();
		
		String beginTime = Cryptor.getAESDecrypt(beginTimeEncrypted);
		
		//if begin time is same
		if(competitorUser.getStatus().equals(beginTime)){
			//notice to competitor
			JSONObject response = new JSONObject();
			response.put("tag", TagName.getYouLose());
			try {
				BufferedWriter osCompetitor = new BufferedWriter(new OutputStreamWriter(competitorSocket.getOutputStream()));
				osCompetitor.write(response.toString());
				osCompetitor.newLine();
				osCompetitor.flush();
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
			LocalDateTime begin = LocalDateTime.parse(beginTime);
			long longBegin = begin.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
			long longEnd = System.currentTimeMillis();
			long end_time = longEnd - longBegin;
			int endTime = (int) end_time;
			
			HistoryDAO history = new HistoryDAO();
			history.updateHistory(id, Integer.toString(user.getId()), endTime);
			UserDAO userDAO = new UserDAO();
			userDAO.updatePoint(user.getId(), competitorUser.getId(), user.getPoint(), competitorUser.getPoint());
			
			return true;
		}
		
		//else
		else{
			JSONObject response = new JSONObject();
			response.put("tag", TagName.getFalseComplete());
			try {
				BufferedWriter osUser = new BufferedWriter(new OutputStreamWriter(userSocket.getOutputStream()));
				osUser.write(response.toString());
				osUser.newLine();
				osUser.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return false;
		}
	}
	
	//if userA defeat game
	public static boolean handleDefeatGame(KeyValue<User, Socket> userPair, KeyValue<User, Socket> competitorPair, String beginTimeEncrypted, int id){
		
		User user = userPair.getKey();
		Socket userSocket = userPair.getValue();
		User competitorUser = competitorPair.getKey();
		Socket competitorSocket = competitorPair.getValue();
		
		String beginTime = Cryptor.getAESDecrypt(beginTimeEncrypted);
		
		//if begin time is same
		if(competitorUser.getStatus().equals(beginTime)){
			//notice to competitor
			JSONObject response = new JSONObject();
			response.put("tag", TagName.getYouWin());
			try {
				BufferedWriter osCompetitor = new BufferedWriter(new OutputStreamWriter(competitorSocket.getOutputStream()));
				osCompetitor.write(response.toString());
				osCompetitor.newLine();
				osCompetitor.flush();
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
			LocalDateTime begin = LocalDateTime.parse(beginTime);
			long longBegin = begin.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
			long longEnd = System.currentTimeMillis();
			long end_time = longEnd - longBegin;
			int endTime = (int) end_time;
			
			HistoryDAO history = new HistoryDAO();
			history.updateHistory(id, Integer.toString(competitorUser.getId()), endTime);
			UserDAO userDAO = new UserDAO();
			userDAO.updatePoint(user.getId(), competitorUser.getId(), user.getPoint(), competitorUser.getPoint());
			
			return true;
		}
		
		//else
		else{
			JSONObject response = new JSONObject();
			response.put("tag", TagName.getFalseComplete());
			try {
				BufferedWriter osUser = new BufferedWriter(new OutputStreamWriter(userSocket.getOutputStream()));
				osUser.write(response.toString());
				osUser.newLine();
				osUser.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return false;
		}
	}
	
	//rematch
	public static void handlerRematchGame(KeyValue<User, Socket> inviterPair, KeyValue<User, Socket> competitorPair){
		//if competitor offline or ingame
		if(competitorPair == null){
			JSONObject response = new JSONObject();
			response.put("tag", TagName.getRematchRefuse());
			try {
				BufferedWriter osInviter = new BufferedWriter(new OutputStreamWriter(inviterPair.getValue().getOutputStream()));
				osInviter.write(response.toString());
				osInviter.newLine();
				osInviter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//else
		else{
			User inviter = (User) inviterPair.getKey();
			
			JSONObject response = new JSONObject();
			response.put("tag", TagName.getRequestContinue());
			response.put("idInviter", inviter.getId());
			response.put("nameInviter", inviter.getName());
			try {
				BufferedWriter osCompetitor = new BufferedWriter(new OutputStreamWriter(competitorPair.getValue().getOutputStream()));
				osCompetitor.write(response.toString());
				osCompetitor.newLine();
				osCompetitor.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}

