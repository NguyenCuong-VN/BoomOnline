package src.socket;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;

import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import src.util.DataGame;
import src.util.TagName;
import src.view.HomePage;
import src.view.RankPage;
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
						
						JSONArray js = (JSONArray) data.get("users");						
						
						if(this.clientSocket.getHomeFrame() != null){
							//create object[][] data user online
							Object[][] dataMo = new Object [js.size()][4];
					    	
					    	int index = 0;
					    	for(Object jsb : js){
								if(jsb instanceof JSONObject){
									JSONObject a = (JSONObject) jsb;
									if(a.get("username").equals(this.clientSocket.getHomeFrame().getTitle())) continue;
									Object[] tmp = new Object[4];
									tmp[0] = a.get("id") ;
									tmp[1] = a.get("name");
									tmp[2] = a.get("point");
									tmp[3] = a.get("status");
									dataMo[index++] = tmp; 
								}
							}
					    	
							this.clientSocket.getHomeFrame().setTable(dataMo);
						}				
				    }
					else if(data.get("tag").equals(TagName.getRequestInvite())){
						
						int result = JOptionPane.showConfirmDialog(this.clientSocket.getHomeFrame(), data.get("inviter_name") + " muốn thách đấu bạn");
						if(result == JOptionPane.YES_OPTION){
							int id = Math.toIntExact((Long) data.get("inviter_id"));
							this.clientSocket.accept(id);
						}
						else{
							int id = Math.toIntExact((Long) data.get("inviter_id"));
							this.clientSocket.refuse(id);
						}
						
					}
					else if(data.get("tag").equals(TagName.getDeniedCompare())){
						JOptionPane.showMessageDialog(this.clientSocket.getHomeFrame(), "Đối thủ của bạn không sẵn sàng");
					}
					else if(data.get("tag").equals(TagName.getRefuseCompare())){
						
						JOptionPane.showMessageDialog(this.clientSocket.getHomeFrame(), "Đối thủ không muốn solo với bạn");
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
						JOptionPane.showMessageDialog(this.clientSocket.getHomeFrame(), "Đối thủ không muốn chơi lại với bạn");
					}
					else if(data.get("tag").equals(TagName.getGameData())){
						try {
				            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				                if ("Nimbus".equals(info.getName())) {
				                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
				                    break;
				                }
				            }
				        } catch (ClassNotFoundException ex) {
				            java.util.logging.Logger.getLogger(RankPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
				        } catch (InstantiationException ex) {
				            java.util.logging.Logger.getLogger(RankPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
				        } catch (IllegalAccessException ex) {
				            java.util.logging.Logger.getLogger(RankPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
				        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
				            java.util.logging.Logger.getLogger(RankPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
				        }
						System.out.println(data.get("idCompetitor"));
						this.clientSocket.setGameFrame(new gameDoMin("Dò mìn online - "+ data.get("nameCompetitor"), 0, DataGame.convertToArray(data.get("datagame").toString()), this.clientSocket, data));
						Thread gameThread = new Thread(this.clientSocket.getGameFrame());
						gameThread.start();
					}
					else if(data.get("tag").equals(TagName.getRequestContinue())){
						int result = JOptionPane.showConfirmDialog(this.clientSocket.getHomeFrame(), data.get("nameInviter") + " muốn chơi lại với bạn");
						if(result == JOptionPane.YES_OPTION){
							int id = Math.toIntExact((Long) data.get("idInviter"));
							this.clientSocket.accept(id);
						}
						else{
							int id = Math.toIntExact((Long) data.get("idInviter"));
							this.clientSocket.refuse(id);
						}
					}
					else if(data.get("tag").equals(TagName.getLoginSuccess())){
						//form login, user list
						String name = this.clientSocket.getLoginFrame().getUsername();
						this.clientSocket.getLoginFrame().dispose();
						this.clientSocket.setHomeFrame(new HomePage(this.clientSocket));
						this.clientSocket.getHomeFrame().setVisible(true);
						this.clientSocket.getHomeFrame().setLocationRelativeTo(null);
						this.clientSocket.getHomeFrame().setTitle(name);
					}
					else if(data.get("tag").equals(TagName.getLoginFalse())){
						
						JOptionPane.showMessageDialog(this.clientSocket.getLoginFrame(), "Tài khoản hoặc mật khẩu không đúng");
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





