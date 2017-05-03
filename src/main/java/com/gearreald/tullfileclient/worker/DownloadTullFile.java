package com.gearreald.tullfileclient.worker;

import com.gearreald.tullfileclient.models.TullFile;

public class DownloadTullFile implements Workable {
	
	public TullFile file;
	
	public DownloadTullFile(TullFile f){
		this.file=f;
	}
	public void work() throws Exception{
		this.file.downloadFile();
	}
}
