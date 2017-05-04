package com.gearreald.tullfileclient.worker;

import java.io.File;

public class UploadFile implements Workable {
	
	public File file;
	
	public UploadFile(File f){
		this.file=f;
	}
	public void work() throws Exception{
		
	}	
}
