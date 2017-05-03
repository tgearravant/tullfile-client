package com.gearreald.tullfileclient.worker;

import com.gearreald.tullfileclient.Environment;

public class Worker extends Thread {
	@Override
	public void run() {
		while(true){
			Workable job = Environment.getJobQueue().poll();
			try{
				if(job!=null){
					job.work();
				}else{
					Thread.sleep(1000);
				}
			}catch (InterruptedException e) {
			}catch(Exception e){
				e.printStackTrace();
				Environment.getJobQueue().add(job);
			}
		}
	}
}
