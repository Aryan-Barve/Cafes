package com.cafes.utils;

import java.util.Date;
import java.util.Map;
import java.io.File;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CafeUtils {
	
	private CafeUtils() {
	    
	}

	public static ResponseEntity<String> getResponseEntity(String message, HttpStatus http) {
	    return new ResponseEntity<String>(message, http);
	}

	public static String getUUID() {
		Date date = new Date();
		long time= date.getTime();
		return "Bill-"+time;
	}

	public static JSONArray getJsonArrayFromString(String data) throws JSONException {
		JSONArray jsonArray = new JSONArray(data);
		return jsonArray;
	}

	public static Map<String,Object> getMapFromJson(String data){
		 if(!Strings.isNullOrEmpty(data)) {
			 return new Gson().fromJson(data, new TypeToken<Map<String,Object>>(){
				 
			 }.getType());
		 }
		 return new HashMap<>();
	}
	
	public static Boolean isFileExists(String path) {
		try {
			File file = new File(path);
			return (file!=null && file.exists())?Boolean.TRUE:Boolean.FALSE;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
