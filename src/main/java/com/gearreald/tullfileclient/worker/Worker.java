package com.gearreald.tullfileclient.worker;

import java.util.concurrent.atomic.AtomicBoolean;

public class Worker extends Thread {
	
	private final String queueName;
	private AtomicBoolean keepRunning;
	
	public Worker(String queueName){
		super();
		this.keepRunning.set(true);
		this.queueName=queueName;
		this.setDaemon(true);
	}
	@Override
	public void run() {
		while(keepRunning.get()){
			Workable job = WorkerQueues.getJobFromQueue(this.queueName);
			try{
				if(job!=null){
					job.work();
				}else{
					Thread.sleep(1000);
				}
			}catch (InterruptedException e) {
			}catch(Exception e){
				e.printStackTrace();
				WorkerQueues.addJobToQueue(this.queueName, job);
			}
		}
	}
	public void noMore() {
		this.keepRunning.lazySet(false);
	}
}
