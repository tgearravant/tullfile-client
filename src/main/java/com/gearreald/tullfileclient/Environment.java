package com.gearreald.tullfileclient;

import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.gearreald.tullfileclient.worker.Workable;
import com.gearreald.tullfileclient.worker.Worker;

import net.tullco.tullutils.NullUtils;

public class Environment {
	private static HashMap<String,String> configuration = new HashMap<String,String>();
	
	private static Set<Worker> workerList = ConcurrentHashMap.<Worker>newKeySet();
	
	public static void setConfiguration(String key, String value){
		configuration.put(key, value);
	}
	
	public static String getConfiguration(String key){
		return NullUtils.coalesce(configuration.get(key),"");
	}
	public static void setTesting(boolean testing){
		if(testing)
			setConfiguration("TESTING","t");
		else
			setConfiguration("TESTING","f");
	}
	public static boolean inTesting(){
		String testing = getConfiguration("TESTING");
		if(testing==null || !testing.equals("t"))
			return false;
		return true;
	}
	public static void initializeWorkers(){
		workerList.add(new Worker("upload"));
		workerList.add(new Worker("download"));
		workerList.add(new Worker("download"));
		workerList.add(new Worker("quick"));
		workerList.add(new Worker("quick"));
	}
	public static void startWorkers(){
		for(Worker w: workerList){
			if(!w.isAlive())
				w.start();
		}
	}
	public static void stopWorkers(){
		for(Worker w: workerList){
			w.noMore();
		}
	}
}
