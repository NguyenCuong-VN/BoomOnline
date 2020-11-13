package com.Util;

import java.util.regex.Pattern;

public class Validate {
	public static boolean checkUsernameAndPasswd(String input){
		String regex = "^[A-Za-z]\\w{4,29}$";
		
		return Pattern.matches(regex, input);
	}
	
}
