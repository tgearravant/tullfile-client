package com.gearreald.tullfileclient.worker;

public class WorkerException extends Exception {

	private static final long serialVersionUID = -6914462334325487413L;

	public WorkerException(){};
	public WorkerException(String message){
		super(message);
	}
	public WorkerException(Throwable cause){
		super(cause);
	}
	public WorkerException(String message,Throwable cause){
		super(message,cause);
	}
}
