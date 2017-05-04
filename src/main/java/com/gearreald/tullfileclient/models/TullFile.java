package com.gearreald.tullfileclient.models;

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
			return this.getName();
		}
		return this.parent.getLocalPath()+this.getName();
	}
	public int getPieces(){
		return 1;
	}
	public void downloadFile(){
		System.out.println(this.getName()+" isn't really downloading. No downloads are implemented yet.");
	}
}
