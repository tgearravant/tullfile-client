package com.gearreald.tullfileclient;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.gearreald.tullfileclient.worker.Workable;

public class Environment {
	private static HashMap<String,String> configuration = new HashMap<String,String>();
	
	private static ConcurrentLinkedQueue<Workable> jobQueue;
	
	public static void setConfiguration(String key, String value){
		configuration.put(key, value);
	}
	
	public static String getConfiguration(String key){
		return configuration.get(key);
	}
	public void addJobToQueue(Workable j){
		jobQueue.add(j);
	}
	public Workable getNextJobInQueue(){
		return jobQueue.poll();
	}
}
