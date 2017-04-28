package com.gearreald.tullfileclient;

import java.util.HashMap;

public class Environment {
	private static HashMap<String,Object> configuration;
	
	public static void setConfiguration(String key, Object value){
		configuration.put(key, value);
	}
	
	public static Object getConfiguration(String key){
		return configuration.get(key);
	}
}
