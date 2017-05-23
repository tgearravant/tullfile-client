package com.gearreald.tullfileclient.job;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.gearreald.tullfileclient.models.TullFile;
import com.gearreald.tullfileclient.worker.WorkerException;

public class DownloadTullFilePiece extends Job {
	
	private static final String JOB_NAME="Download";
	private boolean done;
	private TullFile file;
	private int pieceNumber;
	
	public DownloadTullFilePiece(TullFile f, int pieceNumber){
		this.file=f;
		this.done=false;
		this.pieceNumber=pieceNumber;
	}
	public void work() throws WorkerException{
		try{
			File f = File.createTempFile("TullFile"+StringUtils.leftPad(Integer.toString(this.pieceNumber), 8), ".part");
			this.file.downloadPiece(f, this.pieceNumber);
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
