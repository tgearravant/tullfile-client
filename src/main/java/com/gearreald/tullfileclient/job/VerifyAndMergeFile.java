package com.gearreald.tullfileclient.job;

import java.io.File;
import java.io.IOException;

import com.gearreald.tullfileclient.models.TullFile;
import com.gearreald.tullfileclient.worker.HardStopException;
import com.gearreald.tullfileclient.worker.WorkerException;

public class VerifyAndMergeFile extends Job {

	private static final String JOB_NAME="Verify and Merge ";
	private TullFile file;
	private File destination;
	private boolean done;
	
	public VerifyAndMergeFile(TullFile f, File destination){
		this.file=f;
		this.done=false;
		this.destination=destination;
	}
	
	@Override
	public void work() throws WorkerException, HardStopException{
		this.failPermanently();
		if(!this.file.allPiecesValid()) {
			this.file.trashInvalidPieces();
		}else if(!this.file.allPiecesDownloaded()){
			return;
		}else{
			try{
				this.file.mergePieces(this.destination);
				this.done=true;
			}catch(IOException e){
				throw new WorkerException(e);
			}
		}
		
	}

	@Override
	public String getJobName() {
		return JOB_NAME+this.file.getName();
	}

	@Override
	public boolean completed() {
		return this.done;
	}
}
