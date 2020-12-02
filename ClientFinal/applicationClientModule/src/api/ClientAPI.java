package src.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;










import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import src.socket.main;
import src.util.TagName;

public class ClientAPI {
	public static JSONObject getRanks(String loaiRank){
		JSONObject data = null;
		
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) new URL("http://localhost:8080/BoomServer/api/users/rank/" + loaiRank).openConnection();
			
			//Get Response  
			BufferedReader buff = new BufferedReader( new InputStreamReader(connection.getInputStream()));
			try {
				Object data_tmp =  new JSONParser().parse(buff.readLine());
				if(data_tmp instanceof JSONObject){
					data = (JSONObject) data_tmp;
					data.remove("tag");
				}
				buff.close();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return data;
	}
	
	public static String postRegister(String username, String password, String name){
		JSONObject data = new JSONObject();
		data.put("username", username);
		data.put("password", password);
		data.put("name", name);
		
		HttpURLConnection connection;

			try {
				connection = (HttpURLConnection) new URL("http://localhost:8080/BoomServer/api/users/register").openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setDoOutput(true);
				OutputStream os = connection.getOutputStream();
				os.write(data.toString().getBytes());
				os.flush();
				os.close();
								
				if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
					BufferedReader buff = new BufferedReader( new InputStreamReader(connection.getInputStream()));
					try {
						Object data_tmp =  new JSONParser().parse(buff.readLine());
						if(data_tmp instanceof JSONObject){
							data = (JSONObject) data_tmp;
							if(data.get("tag").equals(TagName.getErrorRegister())) return "Lỗi server rồi bạn ơi";
							else if(data.get("tag").equals(TagName.getCoincideUsername())) return "Trùng username rồi";
							else return "Đăng ký thành công";
						}
						buff.close();
					} catch (ParseException e) {					
						e.printStackTrace();
					}
				}
				else{
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "";
	}
}
