package edu.se.se441.threads;

import java.util.concurrent.CountDownLatch;

public class Clock extends Thread{
	
	private CountDownLatch startSignal;
	
	public Clock(CountDownLatch startSignal){
		this.startSignal = startSignal;
	}

}
