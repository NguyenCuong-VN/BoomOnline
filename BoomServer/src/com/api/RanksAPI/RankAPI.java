package com.api.RanksAPI;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;

import com.DAO.UserDAO;
import com.Util.TagName;

@Path("/users/rank")
public class RankAPI {
	
	//tổng số điểm (giảm dần)
	@Path("/totalpoint")
	@GET
	public Response getTotalPoint(){
		JSONObject data = new JSONObject();
		data.put("tag", TagName.getRankSuccess());
		
		UserDAO userDAO = new UserDAO();
		Map<Integer, String> dataRank = userDAO.getRankPoint();
		data.put("data", dataRank);
		
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}
	
	
	// trung bình điểm của các đối thủ đã gặp (giảm dần)
	@Path("/averagepoint")
	@GET
	public Response getCompetitorPoint(){
		JSONObject data = new JSONObject();
		data.put("tag", TagName.getRankSuccess());
		
		UserDAO userDAO = new UserDAO();
		Map<Integer, String> dataRank = userDAO.getAverageRank();
		data.put("data", dataRank);
		
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}
	
	
	// trung bình thời gian kết thúc trong các trận thắng và hòa (tăng dần)
	@Path("/averagetime")
	@GET
	public Response getAverageTime(){
		JSONObject data = new JSONObject();
		data.put("tag", TagName.getRankSuccess());
		
		UserDAO userDAO = new UserDAO();
		Map<Integer, String> dataRank = userDAO.getAverageTime();
		data.put("data", dataRank);
		
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}
}
