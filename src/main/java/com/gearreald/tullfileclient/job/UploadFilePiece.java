package com.gearreald.tullfileclient.job;

import java.io.File;
import java.io.IOException;

import com.gearreald.tullfileclient.models.ServerConnection;
import com.gearreald.tullfileclient.worker.HardStopException;
import com.gearreald.tullfileclient.worker.WorkerException;

public class UploadFilePiece extends Job {
	private static final String JOB_NAME="Upload File %s: Piece # %d";
	private File file;
	private String remotePath;
	private String fileName;
	private boolean done;
	private int pieceNumber;
	
	public UploadFilePiece(File f, String remotePath, String fileName, int pieceNumber){
		super();
		this.file=f;
		this.remotePath=remotePath;
		this.fileName=fileName;
		this.pieceNumber=pieceNumber;
		this.done=false;
	}
	public void work() throws WorkerException, HardStopException{
		this.failPermanently();
		try {
			ServerConnection.uploadFilePiece(file, remotePath, fileName, pieceNumber);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WorkerException(e);
		}
		this.done=true;
	}
	@Override
	public String getJobName() {
		return String.format(JOB_NAME, this.fileName, this.pieceNumber);
	}
	@Override
	public boolean completed() {
		return this.done;
	}
}
