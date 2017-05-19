package com.gearreald.tullfileclient.worker;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.gearreald.tullfileclient.job.Job;

/**
 * A static class containing the queues for the workers. The currently expected queue names are 'download','upload','quick'.
 * @author tgearr34
 *
 */
public class WorkerQueues {
	private static Map<String,Queue<Job>> queues = new ConcurrentHashMap<String,Queue<Job>>();
	private static Map<Job,Integer> jobAttempts = new ConcurrentHashMap<Job,Integer>();
	private static Set<Job> failedJobs = ConcurrentHashMap.<Job>newKeySet();
	
	/**
	 * Tracks attempts of jobs. Always call this before attempting a job.
	 * If it throws a HardStopException, give up on the job.
	 * @param job The job to be attempted.
	 * @throws HardStopException This is thrown when the job should no longer be attempted.
	 */
	public static void attemptJob(Job job) throws HardStopException{
		if(!jobAttempts.containsKey(job))
			jobAttempts.put(job, 0);
		int currentAttempts = jobAttempts.get(job);
		if(currentAttempts >= job.getRetries()){
			failedJobs.add(job);
			throw new HardStopException("Job "+job.getJobName()+" has failed too many times. Trashing.");
		}
		jobAttempts.put(job,currentAttempts+1);
	}
	
	public static boolean attempted(Job job){
		if(job==null)
			return false;
		Integer attempts = jobAttempts.get(job);
		if(attempts==null || attempts.equals(new Integer(0)))
			return false;
		return true;
	}
	
	public static void addJobToQueue(String queueName, Job job){
		getQueue(queueName).add(job);
	}
	public static Job getJobFromQueue(String queueName){
		return getQueue(queueName).poll();
	}
	private static Queue<Job> getQueue(String queueName){
		if(!queues.containsKey(queueName))
			queues.put(queueName, new ConcurrentLinkedQueue<Job>());
		return queues.get(queueName);
	}
}
