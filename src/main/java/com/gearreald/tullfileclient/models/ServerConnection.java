package com.gearreald.tullfileclient.models;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;

import com.gearreald.tullfileclient.Environment;

import net.tullco.tullutils.NetworkUtils;

public class ServerConnection {
	
	public static String LIST_URL = "list/";
	public static boolean checkKey() throws MalformedURLException, IOException{
		NetworkUtils.getDataFromURL(
				getURLFor("AUTH")
				,false
				,NetworkUtils.HEAD
				,Pair.of("Authorization", Environment.getConfiguration("API_KEY")));
		return true;
	}
	public static JSONObject getFileListing(TullFolder f) throws IOException{
		JSONObject request = new JSONObject();
		request.put("directory", f.getLocalPath());
		String response = NetworkUtils.sendDataToURL(
				getURLFor("LIST")
				,false
				,NetworkUtils.POST
				,request.toString()
				,Pair.of("Authorization", Environment.getConfiguration("API_KEY")));
		return new JSONObject(response);
	}
	public static File downloadFile(TullFile f){
		return null;
	}
	private static String getURLFor(String location){
		String baseURL = Environment.getConfiguration(("HOSTNAME"));
		if(!baseURL.endsWith("/"))
			baseURL+="/";
		switch(location){
			case "LIST":
				return baseURL + "list/";
			case "AUTH":
				return baseURL;
			default:
				return baseURL;
		}
	}
	private static void uploadFile(File file){
		
	}
}
