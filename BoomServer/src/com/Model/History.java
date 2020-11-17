package com.Model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.websocket.Session;

import org.json.simple.JSONObject;

public class History {
	private int id;
	private String id_userA;
	private String id_userB;
	private String name_userA;
	private String name_userB;
	private String time;
	private String result;
	private int end_time;
	
	@Override
	public String toString() {		
		JSONObject data = new JSONObject();
		data.put("id", this.id);
		data.put("id_userA", this.id_userA);
		data.put("id_userB", this.id_userB);
		data.put("name_userA", this.name_userA);
		data.put("name_userB", this.name_userB);
		data.put("time", this.time);
		data.put("result", this.result);
		data.put("end_time", this.end_time);
		return data.toString();
	}
	
}
