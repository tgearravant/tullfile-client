package com.gearreald.tullfileclient.job;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import com.gearreald.tullfileclient.models.ServerConnection;
import com.gearreald.tullfileclient.worker.WorkerException;
import com.gearreald.tullfileclient.worker.WorkerQueues;

import net.tullco.tullutils.FileUtils;

public class QueueUpload extends Job {

	private static final String JOB_NAME="Queue Upload %s";
	private final File file;
	private final String localPath;
	private final String name;
	
	public QueueUpload(File f, String localPath, String name){
		super();
		this.file=f;
		this.localPath=localPath;
		this.name=name;
	}
	
	@Override
	public void theJob() throws WorkerException {
		String serverFileHash=null;
		String localFileHash=null;
		try{
			serverFileHash = ServerConnection.getFileHash(this.localPath, this.name);
		}catch(IOException e){}
		try {
			localFileHash = FileUtils.sha1Hash(file);
		} catch (NoSuchAlgorithmException|IOException e) {
			throw new WorkerException("Couldn't hash the file.", e);
		}
		if(serverFileHash!=null && localFileHash!=null && !serverFileHash.equals(localFileHash)){
			try {
				ServerConnection.deleteFile(this.localPath, this.name);
			} catch (IOException e) {
				throw new WorkerException("Couldn't delete the non-matching file on the server.",e);
			}
		}
		try {
			ServerConnection.setFileHash(this.localPath, this.name, localFileHash);
		} catch (IOException e) {
			throw new WorkerException("Couldn't set the hash on the server.",e);
		}
		int piecesInFile = ServerConnection.piecesInFile(this.file);
		for(int i = 1; i<=piecesInFile; i++){
			WorkerQueues.addJobToQueue("upload",new UploadFilePiece(this.file,localPath,name,i));
		}
		WorkerQueues.addJobToQueue("quick", new MonitorUpload(this.file,localPath,name));
		this.completeJob();
	}

	@Override
	public String getJobName() {
		return String.format(JOB_NAME,this.file.getName());
	}
}
