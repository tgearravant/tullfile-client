package com.gearreald.tullfileclient.job;

import java.io.IOException;

import org.json.JSONException;

import com.gearreald.tullfileclient.models.TullFolder;
import com.gearreald.tullfileclient.worker.WorkerException;

public class LoadTullFolderData extends Job {

	private TullFolder folder;
	private boolean force;
	public final static String JOB_NAME = "LoadData";
	
	public LoadTullFolderData(TullFolder f){
		this(f,false);
	}
	public LoadTullFolderData(TullFolder f, boolean force){
		super();
		this.folder=f;
		this.force=force;
	}
	public void theJob() throws WorkerException {
		try {
			this.folder.fetchFolderData(this.force);
			this.completeJob();
		} catch (IOException e) {
			throw new WorkerException("There was an error fetching the folder data.",e);
		} catch (JSONException e) {
			throw new WorkerException("The reponse from the server was malformed.",e);
		}
	}
	@Override
	public String getJobName() {
		return JOB_NAME;
	}
}
