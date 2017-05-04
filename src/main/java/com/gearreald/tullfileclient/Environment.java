package com.gearreald.tullfileclient;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.gearreald.tullfileclient.worker.Workable;

import net.tullco.tullutils.NullUtils;

public class Environment {
	private static HashMap<String,String> configuration = new HashMap<String,String>();
	
	private static ConcurrentLinkedQueue<Workable> jobQueue = new ConcurrentLinkedQueue<Workable>();
	
	public static void setConfiguration(String key, String value){
		configuration.put(key, value);
	}
	
	public static String getConfiguration(String key){
		return NullUtils.coalesce(configuration.get(key),"");
	}
	public static Queue<Workable> getJobQueue(){
		return jobQueue;
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
}
