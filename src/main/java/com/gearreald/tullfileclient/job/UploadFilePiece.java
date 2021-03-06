package com.gearreald.tullfileclient.job;

import java.io.File;
import java.io.IOException;

import com.gearreald.tullfileclient.Environment;
import com.gearreald.tullfileclient.models.ServerConnection;
import com.gearreald.tullfileclient.worker.WorkerException;

import javafx.application.Platform;

public class UploadFilePiece extends Job {
	private static final String JOB_NAME="Upload File %s: Piece # %d";
	private File file;
	private String remotePath;
	private String fileName;
	private int pieceNumber;
	
	public UploadFilePiece(File f, String remotePath, String fileName, int pieceNumber){
		super();
		this.file=f;
		this.remotePath=remotePath;
		this.fileName=fileName;
		this.pieceNumber=pieceNumber;
	}
	public void theJob() throws WorkerException {
		try {
			ServerConnection.uploadFilePiece(file, remotePath, fileName, pieceNumber);
			this.completeJob();
		} catch (IOException e) {
			e.printStackTrace();
			throw new WorkerException(e);
		}
		if(this.completed()){
			Platform.runLater(() -> {
				Environment.getInterfaceController().refreshCurrentFolder(true);
			});
		}
	}
	@Override
	public String getJobName() {
		return String.format(JOB_NAME, this.fileName, this.pieceNumber);
	}
}
