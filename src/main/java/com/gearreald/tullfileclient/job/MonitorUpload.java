package com.gearreald.tullfileclient.job;

import java.io.File;
import java.io.IOException;

import com.gearreald.tullfileclient.Environment;
import com.gearreald.tullfileclient.models.ServerConnection;
import com.gearreald.tullfileclient.worker.HardStopException;
import com.gearreald.tullfileclient.worker.WorkerException;

import javafx.application.Platform;

public class MonitorUpload extends Job {

	private static final String JOB_NAME="Monitor Upload %s";
	private final File file;
	private final String localPath;
	private final String name;
	
	public MonitorUpload(File f, String localPath, String name){
		super();
		this.file=f;
		this.localPath=localPath;
		this.name=name;
	}
	
	@Override
	public void work() throws WorkerException, HardStopException{
		this.failPermanently();
		try {
			int uploadedPieces = ServerConnection.uploadedPieces(localPath, name);
			if(uploadedPieces >= ServerConnection.piecesInFile(this.file))
				this.completeJob();
		} catch (IOException e) {
			e.printStackTrace();
			throw new WorkerException(e);
		}
		Platform.runLater(() -> {
			Environment.getInterfaceController().refreshCurrentFolder();
		});
	}

	@Override
	public String getJobName() {
		return String.format(JOB_NAME,this.file.getName());
	}
}
