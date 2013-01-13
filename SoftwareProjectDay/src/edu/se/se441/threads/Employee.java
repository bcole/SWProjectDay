package edu.se.se441.threads;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import edu.se.se441.*;

public class Employee extends Thread {
	// Latches and Barries
	private CountDownLatch startSignal;
	private CyclicBarrier standupMeeting;
	
	private boolean isLead;
	private Office office;
	
	public Employee(boolean isLead, Office office){
		try {
			// Starting all threads at the same time (clock == 0 / "8:00AM").
			startSignal.await();
			
			// Waiting for team leads for the meeting.
			if(isLead) standupMeeting.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
		
		// Start main while loop here.
		
		
	}

	public void setStartSignal(CountDownLatch startSignal) {
		this.startSignal = startSignal;
	}
	
	public void setStandupBarrier(CyclicBarrier standupBarrier){
		standupMeeting = standupBarrier;
	}
}
