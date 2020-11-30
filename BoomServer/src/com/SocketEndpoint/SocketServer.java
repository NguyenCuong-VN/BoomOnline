package com.SocketEndpoint;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.Model.User;
import com.Util.KeyValue;
import com.mysql.cj.conf.ConnectionUrlParser.Pair;


public class SocketServer {
	 static Set<KeyValue<User, Socket>> users_online = Collections.synchronizedSet(new HashSet<KeyValue<User, Socket>>());
	 
	 public static void main(String args[]) {
		 	
	       ServerSocket listener = null;
	       Socket clientSocket = null;
	       
	       //listen
	       try {
	    	   listener = new ServerSocket(9999);
		   } catch (IOException e) {
		       System.out.println(e);
		       System.exit(1);
		   }
		 
		           System.out.println("Server is waiting to accept client...");
		          
		 
		           // Chấp nhận một yêu cầu kết nối từ phía Client.
		           while(true){
		        	   try {
							clientSocket = listener.accept();
							System.out.println("Accept a client!");
				           
				           //tao thread moi xu ly client
				           HandleClient hdlClient = new HandleClient(clientSocket);
				           hdlClient.start();						
				           
						} catch (IOException e) {
							e.printStackTrace();
						}
		           }
		               
	   }



}
