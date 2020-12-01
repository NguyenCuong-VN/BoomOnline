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
import src.view.HomePage;
import src.view.LoginForm;
import src.view.RankPage;
import src.view.RegisterForm;
import src.view.gameDoMin;

public class ClientSocket {
	private gameDoMin gameFrame = null;
	private LoginForm loginFrame = null;
	private RegisterForm registerFrame = null;
	private HomePage homeFrame = null;
	private RankPage rankFrame = null;
	
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
	  
	  public void createLoginFrame(){
		  this.loginFrame = new LoginForm(this);
		  this.loginFrame.setVisible(true);
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

	public LoginForm getLoginFrame() {
		return loginFrame;
	}

	public void setLoginFrame(LoginForm loginFrame) {
		this.loginFrame = loginFrame;
	}

	public RegisterForm getRegisterForm() {
		return registerFrame;
	}

	public void setRegisterForm(RegisterForm registerFrame) {
		this.registerFrame = registerFrame;
	}

	public HomePage getHomeFrame() {
		return homeFrame;
	}

	public void setHomeFrame(HomePage homeFrame) {
		this.homeFrame = homeFrame;
	}

	public RankPage getRankFrame() {
		return rankFrame;
	}

	public void setRankFrame(RankPage rankFrame) {
		this.rankFrame = rankFrame;
	}  
    
	
   
    
    
      
}
