package com.gearreald.tullfileclient.worker;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import com.gearreald.tullfileclient.job.Job;
import com.gearreald.tullfileclient.models.ErrorDialogBox;

public class Worker extends Thread {
	
	private final String queueName;
	private AtomicBoolean keepRunning;
	private final UUID uuid;
	
	public Worker(String threadName, String queueName){
		super(threadName);
		this.uuid = UUID.randomUUID();
		this.keepRunning=new AtomicBoolean(true);
		this.queueName=queueName;
		this.setDaemon(true);
	}
	@Override
	public void run() {
		while(keepRunning.get()){
			Job job = WorkerQueues.getJobFromQueue(this.queueName);
			try{
				if(job==null || WorkerQueues.attempted(job))
					Thread.sleep(1000);
				if(job!=null){
					WorkerQueues.attemptJob(job);
					job.work();
				}
			}catch (InterruptedException e) {
				this.noMore();
			}catch(WorkerException e){
				System.err.println("Worker on queue "+queueName+" has failed.\nMessage: "+e.getMessage());
				WorkerQueues.addJobToQueue(this.queueName, job);
			}catch (HardStopException e) {
				ErrorDialogBox.dialogFor(e);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	public void noMore() {
		this.keepRunning.lazySet(false);
		this.interrupt();
	}
	private UUID getUUID() {
		return this.uuid;
	}
	@Override
	public int hashCode() {
		return this.getUUID().hashCode();
	}
	@Override
	public boolean equals(Object o){
		if(o==this)
			return true;
		if(!(o instanceof Worker))
			return false;
		Worker w = (Worker) o;
		return w.getUUID().equals(this.getUUID());
	}
}
