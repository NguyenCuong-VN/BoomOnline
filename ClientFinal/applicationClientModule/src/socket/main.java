package src.socket;

public class main {
	public static void main(String[] args) {
		try {
			ClientSocket client = new ClientSocket();
			client.client();
			
			
			
			client.createLoginFrame();
			while(true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
