package src.socket;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;

import src.util.TagName;
import src.view.gameDoMin;

public class ClientSocket {
	private gameDoMin gameFrame = null;
	
	
	private  BufferedWriter os = null;
    private  BufferedReader is = null;
    
    public void client() throws Exception
    {
    	// Địa chỉ máy chủ.
        final String serverHost = "localhost";
        final int port = 9999;
        
        Socket socketOfClient = null;
  
        try {
            // Gửi yêu cầu kết nối tới Server đang lắng nghe
            // trên máy 'localhost' cổng 9999.
            socketOfClient = new Socket(serverHost, port);
  
            // Tạo luồng đầu ra tại client (Gửi dữ liệu tới server)
            os = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));
  
            // Luồng đầu vào tại Client (Nhận dữ liệu từ server).
            is = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));
  
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + serverHost);
            return;
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + serverHost);
            return;
        }
        
        //listening stream server to client
        Thread res = new HandleServer(this);
		res.start();	
		
		DangNhap("cuongnguyen", "cuong113");
//		DangNhap("cuon2", "cuong34");
		login();
    }  
    
    //test
    public void login(){	
        

        
    		 		//test compare
    		 		Scanner sc = new Scanner(System.in);
    				String name = sc.next();
    				System.out.println("input: " + name);
    				if(name.equals("ok")){
    					this.accept(1);
    				}
    				else if(name.equals("compare")){
    					this.compare(2);
    				}
    				else if(name.equals("no")){
    					this.refuse(1);
    				}
    				else if(name.equals("continue")){
    					this.rematchGame("{\"tag\":\"<Rematch game>\", \"idCompetitor\":\"7LjGmRxIyFtDfuLqt0e32w==\"}");
    				}
    }
    
    public void DangNhap(String username,String pwd) throws IOException, DeploymentException{
    	try {
			os.write("{\"tag\":\"" + TagName.getLogin() + "\", \"username\":\"" + username + "\", \"password\":\"" + pwd + "\"}");
			os.newLine(); 
	        os.flush();  
    	} catch (IOException e1) {
			e1.printStackTrace();
		}			
    }
    
    public void compare(int idUserB){
    	try {
			  os.write("{\"tag\":\"" + TagName.getRequestCompare() + "\", \"idUserB\":" + idUserB + "}");
	           os.newLine(); 
	           os.flush();  		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	  
	public void accept(int idUserA){
		  try {
			  os.write("{\"tag\":\"" + TagName.getAcceptInvite() + "\", \"idUserA\":"+idUserA+"}");
	           os.newLine(); 
	           os.flush();  
				
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	  
	  public void refuse(int idUserA){
		  try {
			  os.write("{\"tag\":\"" + TagName.getRefuseInvite()+ "\", \"idUserA\":"+idUserA+"}");
	           os.newLine(); 
	           os.flush();  

			} catch (IOException e) {
				e.printStackTrace();
			}
	  }
	  
	  public void complete_defeat(String datas){
		  try {
			  os.write(datas);
	           os.newLine(); 
	           os.flush();  

			} catch (IOException e) {
				e.printStackTrace();
			}
	  }	  
	  
	  public void rematchGame(String idHash){
		  try {
			  os.write("{\"tag\":\"" + TagName.getRematchGame() + "\", \"idCompetitor\":\"" + idHash + "\"}");
	           os.newLine(); 
	           os.flush();  
			  
			} catch (IOException e) {
				e.printStackTrace();
			}
	  }
	  
    public static void main(String[] args) {
		try {
			ClientSocket client = new ClientSocket();
			client.client();
			//test complete game
			Scanner sc = new Scanner(System.in);
			String input = sc.nextLine();
			
			client.accept(1);
			while(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    
    
    //getter setter area
    
	public gameDoMin getGameFrame() {
		return gameFrame;
	}

	public void setGameFrame(gameDoMin gameFrame) {
		this.gameFrame = gameFrame;
	}

	public BufferedWriter getOs() {
		return os;
	}

	public void setOs(BufferedWriter os) {
		this.os = os;
	}

	public BufferedReader getIs() {
		return is;
	}

	public void setIs(BufferedReader is) {
		this.is = is;
	}  
    
   
    
    
      
}
