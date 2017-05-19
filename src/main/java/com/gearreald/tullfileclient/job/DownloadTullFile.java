package com.gearreald.tullfileclient.job;

import java.io.IOException;

import com.gearreald.tullfileclient.models.TullFile;
import com.gearreald.tullfileclient.worker.WorkerException;

public class DownloadTullFile extends Job {
	
	private static final String JOB_NAME="Download";
	private boolean done;
	private TullFile file;
	
	public DownloadTullFile(TullFile f){
		this.file=f;
		this.done=false;
	}
	public void work() throws WorkerException{
		try{
			this.file.downloadFile();
			this.done=true;
		}catch(IOException e){
			throw new WorkerException("Download Failed",e);
		}
	}
	@Override
	public String getJobName() {
		return JOB_NAME+" "+this.file.getName();
	}
	@Override
	public boolean completed() {
		return this.done;
	}
}
