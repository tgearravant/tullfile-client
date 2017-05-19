package com.gearreald.tullfileclient.models;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
public class TullFile {
	
	private String name;
	private TullFolder parent;
	
	public TullFile(String name,TullFolder parent){
		this.name=name;
		this.parent=parent;
	}
	public String getName(){
		return this.name;
	}
	public String getLocalPath(){
		if(this.parent==null){
			return "/";
		}
		return this.parent.getLocalPath();
	}
	public int getPieces(){
		return 13;
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
