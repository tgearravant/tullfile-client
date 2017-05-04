package com.gearreald.tullfileclient.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gearreald.tullfileclient.Environment;
import com.gearreald.tullfileclient.worker.LoadTullFolderData;

public class TullFolder {
	
	public List<TullFolder> subfolders;
	public List<TullFile> files;
	
	public boolean fetched = false;
	
	private String name;
	
	private TullFolder parent;
	
	public TullFolder(String name){
		this(name,null);
	}
	public TullFolder(String name, TullFolder parent){
		this.name=name;
		this.parent=parent;
		this.subfolders = new ArrayList<TullFolder>();
		this.files = new ArrayList<TullFile>();
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
	public List<TullFolder> getSubfolders() {
		return this.subfolders;
	}
	public List<TullFile> getFiles(){
		return this.files;
	}
	public void fetchFolderData(boolean force) throws IOException{
		if(this.fetched&&!force)
			return;
		if(this.fetched){
			this.subfolders.clear();
			this.files.clear();
		}
		JSONObject folderJSON = ServerConnection.getFileListing(this);
		System.out.println(folderJSON);
		fromJSON(folderJSON.getJSONObject("response"));
		for(TullFolder folder: this.subfolders){
			Environment.getJobQueue().add(new LoadTullFolderData(folder));
		}
		this.fetched=true;
	}
	protected void fromJSON(JSONObject json){
		JSONArray fileArray = json.getJSONArray("files");
		JSONArray folderArray = json.getJSONArray("subfolders");
		for(int i=0;i<fileArray.length();i++){
			JSONObject fileJSON = fileArray.getJSONObject(i);
			this.files.add(new TullFile(fileJSON.getString("name"),this));
		}
		for(int i=0;i<folderArray.length();i++){
			String folderName = folderArray.getString(i);
			this.subfolders.add(new TullFolder(folderName,this));
		}
	}
}
