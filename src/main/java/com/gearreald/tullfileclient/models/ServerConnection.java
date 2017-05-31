package com.gearreald.tullfileclient.models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;
import org.json.JSONObject;

import com.gearreald.tullfileclient.Environment;

import net.tullco.tullutils.NetworkUtils;
import net.tullco.tullutils.Pair;

public class ServerConnection {
	
	private static final String LIST_URL = "list/";
	private static final String UPLOAD_URL = "upload/";
	private static final String DOWNLOAD_URL = "download/";
	private static final String NEW_FOLDER_URL="new/";
	private final static String DELETE_FOLDER_URL="delete_folder/";
	private final static String DELETE_FILE_URL="delete_file/";
	private static final String AUTH_URL = "";
	private static final String VERIFY_PIECE_URL = "verify/";
	private static final String VERIFY_FILE_URL = "verify_file/";
	
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
	
	public static boolean createNewFolder(String localPath, String name) throws IOException{
		JSONObject request = new JSONObject();
		request.put("directory", localPath);
		request.put("name", name);
		String response = NetworkUtils.sendDataToURL(
				getURLFor("NEW_FOLDER")
				,false
				,NetworkUtils.POST
				,request.toString()
				,Pair.<String,String>of("Authorization", Environment.getConfiguration("API_KEY")));
		if((new JSONObject(response).get("message")).equals("success")){
			return true;
		}else{
			return false;
		}
	}
	public static boolean deleteFile(String localPath, String name) throws IOException{
		JSONObject request = new JSONObject();
		request.put("directory", localPath);
		request.put("name", name);
		String response = NetworkUtils.sendDataToURL(
				getURLFor("DELETE_FILE")
				,false
				,NetworkUtils.DELETE
				,request.toString()
				,Pair.<String,String>of("Authorization", Environment.getConfiguration("API_KEY")));
		if((new JSONObject(response).get("message")).equals("success")){
			return true;
		}else{
			return false;
		}
	}
	public static boolean deleteFolder(String localPath) throws IOException{
		JSONObject request = new JSONObject();
		request.put("directory", localPath);
		String response = NetworkUtils.sendDataToURL(
				getURLFor("DELETE_FOLDER")
				,false
				,NetworkUtils.DELETE
				,request.toString()
				,Pair.<String,String>of("Authorization", Environment.getConfiguration("API_KEY")));
		if((new JSONObject(response).get("message")).equals("success")){
			return true;
		}else{
			return false;
		}
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
						,Pair.<String,String>of("pieceNumber",Integer.toString(piece))
						,Pair.<String,String>of("Authorization", Environment.getConfiguration("API_KEY")));
				return data;
			}catch(IOException e){
				attempts++;
				if(attempts > DOWNLOAD_RETRIES)
					throw e;
			}
		}
	}
	
	public static JSONObject getFileListing(String localPath) throws IOException{
		JSONObject request = new JSONObject();
		request.put("directory", localPath);
		String response = NetworkUtils.sendDataToURL(
				getURLFor("LIST")
				,false
				,NetworkUtils.POST
				,request.toString()
				,Pair.<String,String>of("Authorization", Environment.getConfiguration("API_KEY")));
		return new JSONObject(response);
	}
	
	public static String getFilePieceHash(String localPath, String name, int piece) throws IOException {
		String response = NetworkUtils.getDataFromURL(
				getURLFor("VERIFY_PIECE")
				,false
				,NetworkUtils.GET
				,Pair.<String,String>of("localPath",localPath)
				,Pair.<String,String>of("fileName",name)
				,Pair.<String,String>of("pieceNumber",Integer.toString(piece))
				,Pair.<String,String>of("Authorization", Environment.getConfiguration("API_KEY")));
		JSONObject responseJSON = new JSONObject(response);
		try{
			return responseJSON.getString("piece_hash");
		}catch(JSONException e){
			throw new FileNotFoundException(responseJSON.getString("message"));
		}
	}
	
	public static String getFileHash(String localPath, String name) throws IOException {
		String response = NetworkUtils.getDataFromURL(
				getURLFor("VERIFY_FILE")
				,false
				,NetworkUtils.GET
				,Pair.<String,String>of("localPath",localPath)
				,Pair.<String,String>of("fileName",name)
				,Pair.<String,String>of("Authorization", Environment.getConfiguration("API_KEY")));
		JSONObject responseJSON = new JSONObject(response);
		try{
			return responseJSON.getString("file_hash");
		}catch(JSONException e){
			return null;
		}
	}
	
	public static void setFileHash(String localPath, String name, String hash) throws IOException {
		JSONObject requestJson = new JSONObject();
		requestJson.put("file_hash", hash);
		requestJson.put("localPath", localPath);
		requestJson.put("fileName", name);
		String response = NetworkUtils.sendDataToURL(
				getURLFor("VERIFY_FILE")
				,false
				,NetworkUtils.PUT
				,requestJson.toString()
				,Pair.<String,String>of("Authorization", Environment.getConfiguration("API_KEY")));
		JSONObject responseJSON = new JSONObject(response);
		if(responseJSON.has("error"))
			throw new IOException("The File Hash was not set.");
	}
	
	public static void uploadFile(File file, String filePath, String fileName) throws IOException{
		DigestInputStream dis=null;
		try{
			byte[] byteBuffer = new byte[CHUNK_SIZE];
			FileInputStream f = new FileInputStream(file);
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			dis = new DigestInputStream(f,md);
				int bytesInBuffer=0;
				for(int i=1; (bytesInBuffer = dis.read(byteBuffer))!=-1; i++){
					JSONObject json = sendByteStream(byteBuffer, bytesInBuffer, i, filePath, fileName);
					if(json.getString("status").equals("failure")){
						throw new IOException("Upload failed.");
					}
				}
		}catch(NoSuchAlgorithmException e){
			ErrorDialogBox.dialogFor(e);
		}finally{
			if(dis!=null)
				dis.close();
		}
	}
	
	private static JSONObject sendByteStream(
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
				,Pair.<String,String>of("fileName", fileName)
				,Pair.<String,String>of("Authorization", Environment.getConfiguration("API_KEY")));
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
			case "NEW_FOLDER":
				return baseURL + NEW_FOLDER_URL;
			case "DELETE_FOLDER":
				return baseURL + DELETE_FOLDER_URL;
			case "DELETE_FILE":
				return baseURL + DELETE_FILE_URL;
			case "VERIFY_PIECE":
				return baseURL + VERIFY_PIECE_URL;
			case "VERIFY_FILE":
				return baseURL + VERIFY_FILE_URL;
			default:
				throw new RuntimeException("That URL doesn't exist...");
		}
	}
}
