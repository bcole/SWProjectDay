package edu.se.se441.threads;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import edu.se.se441.Office;

public class Manager extends Thread {
	private Office office;
	private CyclicBarrier standupMeeting;
	private CountDownLatch startSignal;
	
	public Manager(Office office){
		this.office = office;
	}
	
	public void run(){
		try {
			standupMeeting.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
	}
	
	public void setStandupBarrier(CyclicBarrier standupBarrier){
		standupMeeting = standupBarrier;
	}
	
	public void setStartSignal(CountDownLatch startSignal) {
		this.startSignal = startSignal;
	}

}
