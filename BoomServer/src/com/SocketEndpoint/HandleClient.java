package com.SocketEndpoint;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.Session;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.DAO.UserDAO;
import com.Model.User;
import com.Util.Cryptor;
import com.Util.KeyValue;
import com.Util.TagName;

public class HandleClient extends Thread{
	private Socket clientSocket = null;
	private String message;
	private BufferedReader is;
	private BufferedWriter os;
	private KeyValue<User, Socket> pair = null;
	
	public HandleClient(Socket socket) {
		this.clientSocket = socket;
	}
	
	@Override
	public void run() {
		// Mở luồng vào ra trên Socket tại Server.
        try {
			is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			os = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
 
        // Nhận được dữ liệu từ người dùng và gửi lại trả lời.
        try {
			while ((message = is.readLine()) != null) {
				System.out.println(message);
			    try {
					JSONObject data = null;
					Object data_tmp =  new JSONParser().parse(message);
					if(data_tmp instanceof JSONObject){
						data = (JSONObject) data_tmp;
						//if login
						if(data.get("tag").equals(TagName.getLogin())){
							UserDAO userDAO = new UserDAO();
							User user = userDAO.login(data.get("username").toString(), data.get("password").toString());
							//if login false
							if(user == null){
								try {		
									JSONObject response = new JSONObject();
									response.put("tag", TagName.getLoginFalse());
									os.write(response.toString());
									os.newLine();
									os.flush();
								} catch (Exception e) {
									System.out.println(e);
								}
							}
							//login success
							else{
								//add info user here
								user.setStatus("online");
					           	this.pair = new KeyValue<User, Socket>(user, this.clientSocket);
								
								//response
								try {
									//response to user
									JSONObject response = new JSONObject();
									response.put("tag", TagName.getLoginSuccess());
									os.write(response.toString());
									os.newLine();
									os.flush();
									
									//add user
									addOrRemoveUser("add", this.pair);
										
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
						
						//if userA invite userB
						if(data.get("tag").equals(TagName.getRequestCompare())){
							int idUserB = Integer.parseInt((data.get("idUserB").toString()));
							KeyValue<User, Socket> userBPair = getUserByID(idUserB);
							if(userBPair != null){
								User tmp = (User) userBPair.getKey();
								if(!tmp.getStatus().equals("online")){
									userBPair = null;
								}
							}						
							HandleMessageSocket.handleRequestCompare(this.pair, userBPair);			
						}
						
						//if userB accept invite from userA
						else if(data.get("tag").equals(TagName.getAcceptInvite())){
							int idUserA = Integer.parseInt((data.get("idUserA").toString()));
							KeyValue<User, Socket> userAPair = getUserByID(idUserA);
							if(userAPair != null){
								User tmp = (User) userAPair.getKey();
								if(!tmp.getStatus().equals("online")){
									userAPair = null;
								}
								HandleMessageSocket.handleAcceptInvite(userAPair, this.pair);
								updateUsers();
							}
							
						}
						
						//if userB refuse invite from userA
						else if(data.get("tag").equals(TagName.getRefuseInvite())){
							int idUserA = Integer.parseInt((data.get("idUserA").toString()));
							KeyValue<User, Socket> userAPair = getUserByID(idUserA);
							if(userAPair != null){
								User tmp = (User) userAPair.getKey();
								if(!tmp.getStatus().equals("online")){
									userAPair = null;
								}
							}
							HandleMessageSocket.handleRefuseInvite(userAPair);
						}	
						
						//if userA complete game
						else if(data.get("tag").equals(TagName.getCompleteGame())){
							String idCompetitorEncrypted = data.get("idCompetitor").toString();
							String idCompetitor = Cryptor.getAESDecrypt(idCompetitorEncrypted);
							int id = Integer.parseInt(idCompetitor);
							KeyValue<User, Socket> competitorPair = getUserByID(id);	
							
							String beginTime = data.get("beginTime").toString();
							int idHistory = Integer.parseInt((data.get("idHistory").toString()));
							boolean check = HandleMessageSocket.handleCompleteGame(this.pair, competitorPair, beginTime, idHistory);	
							if(check){
								updateUsers();
							}
						}
						
						//if userA defeat game
						else if(data.get("tag").equals(TagName.getDefeatGame())){
							String idCompetitorEncrypted = data.get("idCompetitor").toString();
							String idCompetitor = Cryptor.getAESDecrypt(idCompetitorEncrypted);
							int id = Integer.parseInt(idCompetitor);
							KeyValue<User, Socket> competitorPair = getUserByID(id);
							
							if(competitorPair != null){
								String beginTime = data.get("beginTime").toString();
								int idHistory = Integer.parseInt((data.get("idHistory").toString()));
								boolean check = HandleMessageSocket.handleDefeatGame(this.pair, competitorPair, beginTime, idHistory);	
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
							KeyValue<User, Socket> competitorPair = getUserByID(id);
							if(competitorPair != null){
								User tmp = (User) competitorPair.getKey();
								if(!tmp.getStatus().equals("online")){
									competitorPair = null;
								}
							}
							HandleMessageSocket.handlerRematchGame(this.pair, competitorPair);
							
						}
					}
 
				} catch (ParseException e) {
					e.printStackTrace();
				} 
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// remove user online
	    	addOrRemoveUser("remove", this.pair);
	    	System.out.println("Client shutdown");
		}
	}
	
	
	
	//method handle area
	
	
		private void addOrRemoveUser(String code, KeyValue<User, Socket> userPair){
			if(code.equals("add")){				
				SocketServer.users_online.add(userPair);
			}
			else{
				if(SocketServer.users_online.contains(userPair)) SocketServer.users_online.remove(userPair);
				updateUsers();
			}
			
			if(SocketServer.users_online.contains(userPair)) updateUsers();
		}
		
		//send update users list to users
		private void updateUsers(){
			JSONObject usersList = userListToJSON();
			usersList.put("tag", TagName.getUpdateUsers());
			for(KeyValue<User, Socket> pair : SocketServer.users_online){
				try {
					BufferedWriter os = new BufferedWriter(new OutputStreamWriter(pair.getValue().getOutputStream()));
					os.write(usersList.toString());
					os.newLine();
					os.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		//change list users online to json
		private JSONObject userListToJSON(){
			List<User> users = new ArrayList<User>();
			for(KeyValue<User, Socket> pair : SocketServer.users_online){
				users.add((User) pair.getKey());
			}

			JSONObject json = new JSONObject();
			json.put("users", users);
				
			return json;	
		}
		
		//get user in list users online
		private KeyValue<User, Socket> getUserByID(int id){
			KeyValue<User, Socket> userB = null;
			for(KeyValue<User, Socket> pair: SocketServer.users_online){
				User usr = (User) pair.getKey();
				if(usr.getId() == id){
					userB = pair;
					break;	
				}
			}
			return userB;
		}
}
