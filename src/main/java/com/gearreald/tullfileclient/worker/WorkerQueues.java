package com.gearreald.tullfileclient.worker;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A static class containing the queues for the workers. The currently expected queue names are 'download','upload','quick'.
 * @author tgearr34
 *
 */
public class WorkerQueues {
	private static HashMap<String,Queue<Workable>> queues = new HashMap<String,Queue<Workable>>();
	
	public static void addJobToQueue(String queueName, Workable job){
		if(!queues.containsKey(queueName))
			queues.put(queueName, new ConcurrentLinkedQueue<Workable>());
		queues.get(queueName).add(job);
	}
	public static Workable getJobFromQueue(String queueName){
		return queues.get(queueName).poll();
	}
}
