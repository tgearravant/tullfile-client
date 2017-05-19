package com.gearreald.tullfileclient.worker;

public class HardStopException extends Exception {

	private static final long serialVersionUID = 3956022423149430823L;
	
	public HardStopException(){};
	public HardStopException(String message){
		super(message);
	}
	public HardStopException(Throwable cause){
		super(cause);
	}
	public HardStopException(String message,Throwable cause){
		super(message,cause);
	}
}
