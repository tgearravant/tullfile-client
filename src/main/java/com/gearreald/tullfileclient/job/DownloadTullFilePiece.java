package com.gearreald.tullfileclient.job;

import java.io.IOException;

import com.gearreald.tullfileclient.models.TullFile;
import com.gearreald.tullfileclient.worker.HardStopException;
import com.gearreald.tullfileclient.worker.WorkerException;

public class DownloadTullFilePiece extends Job {
	
	private static final String JOB_NAME="Download %s%s piece #%d";
	private TullFile file;
	private int pieceNumber;
	
	public DownloadTullFilePiece(TullFile f, int pieceNumber){
		super();
		this.file=f;
		this.pieceNumber=pieceNumber;
	}
	public void work() throws WorkerException, HardStopException{
		this.failPermanently();
		try{
			this.file.downloadPiece(this.pieceNumber);
			this.completeJob();
		}catch(IOException e){
			throw new WorkerException("Download Failed",e);
		}
	}
	@Override
	public String getJobName() {
		return String.format(JOB_NAME, this.file.getLocalPath(),this.file.getName(),this.pieceNumber);
	}
}
