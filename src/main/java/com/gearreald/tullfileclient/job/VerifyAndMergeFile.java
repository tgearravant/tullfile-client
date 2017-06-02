package com.gearreald.tullfileclient.job;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import com.gearreald.tullfileclient.Environment;
import com.gearreald.tullfileclient.models.TullFile;
import com.gearreald.tullfileclient.worker.WorkerException;

import javafx.application.Platform;
import net.tullco.tullutils.FileUtils;

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
	public void theJob() throws WorkerException{
		Platform.runLater( () -> { Environment.getInterfaceController().updateProgressOfTullFile(file);});
		if(!this.file.allPiecesValid()) {
			this.file.trashInvalidPieces();
		}else if(!this.file.allPiecesDownloaded()){
			return;
		}else{
			try{
				this.file.mergePieces(this.destination);
				String serverHash = this.file.getFileHash();
				String localHash = FileUtils.sha1Hash(this.destination);
				if(serverHash==null || serverHash.equals(localHash)){
					this.file.cleanUpAllPieces();
					this.done=true;
				}else{
					this.file.reverifyPieces();
				}
			}catch(IOException | NoSuchAlgorithmException e){
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
