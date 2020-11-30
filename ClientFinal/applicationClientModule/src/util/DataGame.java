package src.util;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DataGame {
	private  int NGANG = 10;
	private  int DOC = 8;
	private  int BOOMS = 10;
	private  int[][] values = new int[DOC][NGANG];
	private int dem = 0;
	
	public String getDataGame(){
		String result = "";
		float ratio = (float) 0.05;	
		int i, j;
		
		for(i = 0; i< DOC; i++){
			for(j = 0; j < NGANG; j++){
				values[i][j] = 0;
			}
		}
		while(dem < BOOMS){
			do{
				i = (int) ((DOC-1)*Math.random());
				j = (int) ((NGANG-1)*Math.random());
			} while(values[i][j] != 0);
			if(values[i][j] == 0){
				init(i,j,ratio);
			}
		}
		for(int x = 0; x< DOC; x++){
			result += Arrays.toString(values[x]);
		}
		
		return result;
	}
	
	private void init(int i, int j, float ratio){
		if(Math.random() < ratio){
			values[i][j] = -1;
			//update value cua 8 o xung quanh
			for (int k = i - 1; k <= i + 1; k++)
				for (int h = j - 1; h <= j + 1; h++)
					if (DOC > k && k >= 0 && NGANG > h && h >= 0 && values[k][h] != -1) {
						values[k][h]++;
					}
			dem++;
			
			//Gọi hàm tạo bom cho những ô xung quanh.
			for (int k = i - 1; k <= i + 1; k++)
				for (int h = j - 1; h <= j + 1; h++)
					if (DOC > k && k >= 0 && NGANG > h && h >= 0 && values[k][h] != -1 && dem < BOOMS)
						init(k, h, ratio);
		}
	}
	
	public static int[][] convertToArray(String arg) {
		int data[][] = new int[8][10];
		String[] list = arg.split("]");
		int index = 0;
		for(String j : list){		
			String[] items = j.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");					
			int[] results = new int[items.length];		
			for (int i = 0; i < items.length; i++) {
			    try {
			        results[i] = Integer.parseInt(items[i]);
			    } catch (NumberFormatException nfe) {
			    };
			}
			data[index++] = results;
		}
		return data;
	}

}
