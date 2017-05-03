package com.gearreald.tullfileclient.worker;

import com.gearreald.tullfileclient.models.TullFolder;

public class LoadTullFolderData implements Workable {

	public TullFolder folder;
	
	public LoadTullFolderData(TullFolder f){
		this.folder=f;
	}
	public void work() throws Exception {
		this.folder.fetchFolderData();
	}
}
