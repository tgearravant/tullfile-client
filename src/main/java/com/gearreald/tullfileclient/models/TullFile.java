package com.gearreald.tullfileclient.models;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONObject;
public class TullFile {
	
	private String name;
	private TullFolder parent;
	private int pieces;
	
	public TullFile(JSONObject json,TullFolder parent){
		if(parent==null){
			ErrorDialogBox.dialogFor(new RuntimeException("A Tullfile must have a parent."));
		}
		this.name=json.getString("name");
		this.pieces = json.getInt("pieces");
		this.parent=parent;
	}
	public String getName(){
		return this.name;
	}
	public String getLocalPath(){
		return this.parent.getLocalPath();
	}
	public int getPieces(){
		return this.pieces;
	}
	public void downloadFile() throws IOException{
		File f=new File("C:/Users/tgearr34/TullFile/test.txt");
		String localPath = this.getLocalPath();
		String fileName = this.getName();
		FileOutputStream output = new FileOutputStream(f);
		for(int i=1;i<=this.getPieces();i++){
			byte[] pieceData = ServerConnection.downloadFilePiece(localPath,fileName,i);
			output.write(pieceData);
		}
		output.close();
	}
}
