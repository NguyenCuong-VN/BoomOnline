package src.socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;

import javax.swing.JOptionPane;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import src.util.DataGame;
import src.util.TagName;
import src.view.gameDoMin;


public class HandleServer extends Thread{
	private ClientSocket clientSocket = null;
    private String message = null;
    private BufferedReader is = null;
    
    public HandleServer(ClientSocket clientSocket) {
		this.is = clientSocket.getIs();
		this.clientSocket = clientSocket;
	}
    
    @Override
    public void run() {
    	try {
			while ((message = is.readLine()) != null) {
				try {
					JSONObject data = (JSONObject) new JSONParser().parse(message);
					if(data.get("tag").equals(TagName.getUpdateUsers())){
				    	//frame list user
						System.out.println("A user want rematch with you: " + message);
						
				    }
					else if(data.get("tag").equals(TagName.getRequestInvite())){
						//frame list user
						System.out.println("A user want rematch with you: " + message);
						
					}
					else if(data.get("tag").equals(TagName.getDeniedCompare())){
						//frame list user
						System.out.println("A user want rematch with you: " + message);
					}
					else if(data.get("tag").equals(TagName.getRefuseCompare())){
						//frame list user
						System.out.println("A user want rematch with you: " + message);
					}
					else if(data.get("tag").equals(TagName.getYouLose())){
						JOptionPane.showMessageDialog(this.clientSocket.getGameFrame(), "Chậm thế, người ta thắng rồi kìa !!");			
						this.clientSocket.getGameFrame().nextRound();
					}
					else if(data.get("tag").equals(TagName.getYouWin())){
						JOptionPane.showMessageDialog(this.clientSocket.getGameFrame(), "Thằng kia gà quá chết rồi. Game là dễ !!");			
						this.clientSocket.getGameFrame().nextRound();
					}
					else if(data.get("tag").equals(TagName.getFalseComplete())){
						JOptionPane.showMessageDialog(this.clientSocket.getGameFrame(), "Lừa à, server bị gì ý !!");
					}
					else if(data.get("tag").equals(TagName.getRematchRefuse())){
						//form  list user
					}
					else if(data.get("tag").equals(TagName.getGameData())){
						System.out.println(data.get("idCompetitor"));
						this.clientSocket.setGameFrame(new gameDoMin("Dò mìn online - "+ data.get("nameCompetitor"), 0, DataGame.convertToArray(data.get("datagame").toString()), this.clientSocket, data));
						Thread gameThread = new Thread(this.clientSocket.getGameFrame());
						gameThread.start();
					}
					else if(data.get("tag").equals(TagName.getRequestContinue())){
						//form list user
						System.out.println("A user want rematch with you: " + message);
					}
					else if(data.get("tag").equals(TagName.getLoginSuccess())){
						//form login, user list
						System.out.println("A user want rematch with you: " + message);
					}
					else if(data.get("tag").equals(TagName.getLoginFalse())){
						//form login
						System.out.println("A user want rematch with you: " + message);
					}
					else{
						System.out.println("Response from Server: " + message);
					}   
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
}
