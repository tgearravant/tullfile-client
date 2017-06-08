package com.gearreald.tullfileclient.models;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gearreald.tullfileclient.job.LoadTullFolderData;
import com.gearreald.tullfileclient.worker.WorkerQueues;

public class TullFolder implements TullObject, Comparable<TullFolder> {
	
	private List<TullFolder> subfolders;
	private List<TullFile> files;
	
	private boolean fetched = false;
	
	private String name;
	
	private TullFolder parent;
	
	public TullFolder(String name){
		this(name,null);
	}
	public TullFolder(String name, TullFolder parent){
		this.name=name;
		this.parent=parent;
		this.subfolders = new CopyOnWriteArrayList<TullFolder>();
		this.files = new CopyOnWriteArrayList<TullFile>();
	}
	@Override
	public String getName(){
		return this.name;
	}
	@Override
	public String getLocalPath(){
		if(this.parent==null){
			return "/";
		}
		return this.parent.getLocalPath()+this.getName()+"/";
	}
	@Override
	public TullFolder getParent() {
		return this.parent;
	}
	public List<TullFolder> getSubfolders() {
		return this.subfolders;
	}
	public List<TullFile> getFiles(){
		return this.files;
	}
	public synchronized void fetchFolderData(boolean force) throws IOException{
		if(force==true)
			this.fetched=false;
		if(this.fetched)
			return;
		JSONObject folderJSON = ServerConnection.getFileListing(this.getLocalPath());
		TullFolder fetchedFolder = TullFolder.fromJSON(this.getParentFolder(), this.getName(), folderJSON.getJSONObject("response"));
		this.update(fetchedFolder);
		this.subfolders.sort(null);
		this.files.sort(null);
		this.fetched=true;
	}
	private void update(TullFolder newFolder){
		//first add new folders
		for(TullFolder newSubFolder: newFolder.getSubfolders()){
			if(!this.subfolders.contains(newSubFolder))
				this.subfolders.add(newSubFolder);
		}
		//then add new files
		for(TullFile newFile: newFolder.getFiles()){
			if(!this.getFiles().contains(newFile))
				this.getFiles().add(newFile);
		}
		//now delete old folders
		Iterator<TullFolder> folderIterator = this.getSubfolders().iterator();
		while(folderIterator.hasNext()){
			TullFolder currentSubFolder = folderIterator.next();
			if(!newFolder.getSubfolders().contains(currentSubFolder)){
				this.getSubfolders().remove(currentSubFolder);
			}
		}
		//now delete old files
		Iterator<TullFile> fileIterator = this.getFiles().iterator();
		while(fileIterator.hasNext()){
			TullFile oldFile = fileIterator.next();
			if(!newFolder.getFiles().contains(oldFile)){
				this.getFiles().remove(oldFile);
			}
		}
		//now update existing files
		for(TullFile file: this.getFiles()){
			for(TullFile fetchedFile: newFolder.getFiles()){
				if(fetchedFile.getName().equals(file.getName())){
					file.update(fetchedFile.toJSON());
				}
			}
		}
		//now queue all folders for fetching.
		for(TullFolder folder: this.getSubfolders()){
			WorkerQueues.addJobToQueue("quick", new LoadTullFolderData(folder));
		}
	}
	private static TullFolder fromJSON(TullFolder parent, String name, JSONObject json){
		JSONArray fileArray = json.getJSONArray("files");
		JSONArray folderArray = json.getJSONArray("subfolders");
		TullFolder fetchedFolder = new TullFolder(name, parent);
		for(int i=0;i<fileArray.length();i++){
			JSONObject fileJSON = fileArray.getJSONObject(i);
			fetchedFolder.files.add(new TullFile(fileJSON,fetchedFolder));
		}
		for(int i=0;i<folderArray.length();i++){
			String folderName = folderArray.getString(i);
			fetchedFolder.subfolders.add(new TullFolder(folderName,fetchedFolder));
		}
		return fetchedFolder;
	}
	public void removeFile(TullFile file){
		this.files.remove(file);
	}
	@Override
	public boolean delete(){
		try {
			ServerConnection.deleteFolder(this.getLocalPath());
			return true;
		} catch (IOException e) {
			ErrorDialogBox.dialogFor(e);
			return false;
		}
	}
	public TullFolder getParentFolder(){
		return this.parent;
	}
	public TullFolder getRootFolder(){
		TullFolder folder = this;
		while(folder.getParentFolder()!=null)
			folder=folder.getParentFolder();
		return folder;
	}
	public boolean isTullFolder(){
		return true;
	}
	public boolean isTullFile(){
		return false;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((this.getLocalPath() == null) ? 0 : this.getLocalPath().hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TullFolder other = (TullFolder) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (this.getLocalPath() == null) {
			if (other.getLocalPath() != null)
				return false;
		} else if (!this.getLocalPath().equals(other.getLocalPath()))
			return false;
		return true;
	}
	@Override
	public int compareTo(TullFolder o) {
		if(this.equals(o))
			return 0;
		return this.name.toLowerCase().compareTo(o.name.toLowerCase());
	}
}
