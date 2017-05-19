package com.gearreald.tullfileclient.models;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONObject;

import com.gearreald.tullfileclient.Environment;

import net.tullco.tullutils.NetworkUtils;
import net.tullco.tullutils.Pair;

public class ServerConnection {
	
	private static final String LIST_URL = "list/";
	private static final String UPLOAD_URL = "upload/";
	private static final String DOWNLOAD_URL = "download/";
	private static final String AUTH_URL = "";
	
	private static final int CHUNK_SIZE = 2097152;
	private static final int DOWNLOAD_RETRIES = 3;
	
	public static boolean checkKey() throws MalformedURLException, IOException{
		NetworkUtils.getDataFromURL(
				getURLFor("AUTH")
				,false
				,NetworkUtils.HEAD
				,Pair.<String,String>of("Authorization", Environment.getConfiguration("API_KEY")));
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
				,Pair.<String,String>of("Authorization", Environment.getConfiguration("API_KEY")));
		return new JSONObject(response);
	}
	
	public static byte[] downloadFilePiece(String localPath, String fileName, int piece) throws IOException {
		int attempts = 0;
		while(true){
			try{
				byte[] data = NetworkUtils.getBinaryDataFromURL(
						getURLFor("DOWNLOAD")
						,false
						,NetworkUtils.GET
						,Pair.<String,String>of("localPath",localPath)
						,Pair.<String,String>of("fileName",fileName)
						,Pair.<String,String>of("pieceNumber",Integer.toString(piece)));
				return data;
			}catch(IOException e){
				attempts++;
				if(attempts > DOWNLOAD_RETRIES)
					throw e;
			}
		}
		//TODO Need to download files at some point. (wasntme)
	}
	
	public static void uploadFile(File file, String filePath, String fileName) throws IOException{
		byte[] byteBuffer = new byte[CHUNK_SIZE];
		FileInputStream f = new FileInputStream(file);
		try{
			int bytesInBuffer=0;
			for(int i=1; (bytesInBuffer = f.read(byteBuffer))!=-1; i++){
				JSONObject json = sendByteStream(byteBuffer, bytesInBuffer, i, filePath, fileName);
				if(json.getString("status").equals("failure")){
					throw new IOException("Upload failed.");
				}
			}
		}finally{
			f.close();
		}
	}
	
	public static JSONObject sendByteStream(
			byte[] byteBuffer
			,int bytesInBuffer
			,int pieceNumber
			,String filePath
			,String fileName) throws MalformedURLException, IOException {
		String response = NetworkUtils.sendBinaryDataToURL(
				getURLFor("UPLOAD")
				,false
				,NetworkUtils.POST
				,byteBuffer
				,bytesInBuffer
				,Pair.<String,String>of("pieceNumber", Integer.toString(pieceNumber))
				,Pair.<String,String>of("localPath", filePath)
				,Pair.<String,String>of("fileName", fileName));
		return new JSONObject(response);
	}
	
	private static String getURLFor(String location){
		String baseURL = Environment.getConfiguration(("HOSTNAME"));
		if(!baseURL.endsWith("/"))
			baseURL+="/";
		switch(location){
			case "LIST":
				return baseURL + LIST_URL;
			case "AUTH":
				return baseURL + AUTH_URL;
			case "UPLOAD":
				return baseURL + UPLOAD_URL;
			case "DOWNLOAD":
				return baseURL + DOWNLOAD_URL;
			default:
				throw new RuntimeException("That URL doesn't exist...");
		}
	}
}
