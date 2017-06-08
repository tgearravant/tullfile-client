package com.gearreald.tullfileclient.job;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import com.gearreald.tullfileclient.Environment;
import com.gearreald.tullfileclient.models.TullFile;
import com.gearreald.tullfileclient.worker.WorkerException;

import net.tullco.tullutils.FileUtils;

public class VerifyAndMergeFile extends Job {

	private static final String JOB_NAME="Verify and Merge %s";
	private TullFile file;
	private File destination;
	
	public VerifyAndMergeFile(TullFile f, File destination){
		super();
		this.file=f;
		this.destination=destination;
	}
	
	@Override
	public void theJob() throws WorkerException{
		Environment.getInterfaceController().updateProgressOfTullFile(file);
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
					this.completeJob();
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
		return String.format(JOB_NAME,this.file.getName());
	}
}
