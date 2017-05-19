package com.gearreald.tullfileclient.job;

import java.util.UUID;

import com.gearreald.tullfileclient.worker.WorkerException;

public abstract class Job {
	private final UUID uuid;
	
	private static final int DEFAULT_RETRIES=5;
	
	public Job(){
		this.uuid=UUID.randomUUID();
	}
	public UUID getUUID(){
		return this.uuid;
	}
	public abstract void work() throws WorkerException;
	public abstract String getJobName();
	public int getRetries(){
		return Job.DEFAULT_RETRIES;
	}
	public String getFailureString(){
		return "The job "+getJobName()+" has failed too many times and is being abandoned.";
	}
	@Override
	public int hashCode() {
		return this.getUUID().hashCode();
	}
	@Override
	public boolean equals(Object o){
		if(o==this)
			return true;
		if(!(o instanceof Job))
			return false;
		Job j = (Job) o;
		return j.getUUID().equals(this.getUUID());
	}
	public abstract boolean completed();
}
