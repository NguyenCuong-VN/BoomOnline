package com.api.RegisterAPI;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.DAO.UserDAO;
import com.Util.TagName;
import com.Util.Validate;

@Path("/users/register")
public class RegisterAPI {
	
	@POST
	@Produces("application/json; character=UTF-8")
	public Response register(String sReq){
		JSONObject json = new JSONObject();
		json.put("tag", TagName.getErrorRegister());

		try {
			JSONObject data = (JSONObject) new JSONParser().parse(sReq);
			String username = (String) data.get("username");
			String password = (String) data.get("password");
			String name = (String) data.get("name");
			
			if(Validate.checkUsernameAndPasswd(username) && Validate.checkUsernameAndPasswd(password) && Validate.checkUsernameAndPasswd(name)){
				UserDAO userDAO = new UserDAO();
				if(userDAO.createUser(name, username, password)){
					json.put("tag", TagName.getSuccessRegister());
				}
				else{
					json.put("tag", TagName.getCoincideUsername());
				}
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		return Response.ok(json.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}
}
