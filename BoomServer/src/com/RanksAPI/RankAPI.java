package com.RanksAPI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;

@Path("/users/rank")
public class RankAPI {
	
	//tổng số điểm (giảm dần)
	@Path("/totalpoint")
	@GET
	public Response getTotalPoint(){
		JSONObject data = new JSONObject();
		
		return Response.ok(data.toString()).build();
	}
	
	// trung bình điểm của các đối thủ đã gặp (giảm dần)
	@Path("/averagepoint")
	@GET
	public Response getCompetitorPoint(){
		JSONObject data = new JSONObject();
		
		return Response.ok(data.toString()).build();
	}
	
	// trung bình thời gian kết thúc trong các trận thắng và hòa (tăng dần)
	@Path("/averagetime")
	@GET
	public Response getAverageTime(){
		JSONObject data = new JSONObject();
		
		return Response.ok(data.toString()).build();
	}
}
