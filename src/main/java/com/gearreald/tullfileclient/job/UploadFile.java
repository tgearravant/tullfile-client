package com.gearreald.tullfileclient.job;

import java.io.File;
import java.io.IOException;

import com.gearreald.tullfileclient.Environment;
import com.gearreald.tullfileclient.models.ServerConnection;
import com.gearreald.tullfileclient.worker.HardStopException;
import com.gearreald.tullfileclient.worker.WorkerException;

import javafx.application.Platform;

public class UploadFile extends Job {
	private static final String JOB_NAME="UploadFile";
	private File file;
	private String remotePath;
	private String fileName;
	private boolean done;
	
	public UploadFile(File f, String remotePath, String fileName){
		super();
		this.file=f;
		this.remotePath=remotePath;
		this.fileName=fileName;
		this.done=false;
	}
	public void work() throws WorkerException, HardStopException{
		this.failPermanently();
		try {
			ServerConnection.uploadFile(file, remotePath, fileName);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WorkerException(e);
		}
		Platform.runLater(() -> {
			Environment.getInterfaceController().refreshCurrentFolder();
		});
		this.done=true;
	}
	@Override
	public String getJobName() {
		return JOB_NAME+": "+this.file.getName();
	}
	@Override
	public boolean completed() {
		return this.done;
	}
}
