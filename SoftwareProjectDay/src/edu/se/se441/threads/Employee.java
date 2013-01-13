package edu.se.se441.threads;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.Random;

import edu.se.se441.*;

public class Employee extends Thread {
	// Latches and Barries
	private CountDownLatch startSignal;
	private CyclicBarrier standupMeeting;
	
	private boolean isLead;
	private boolean isWaitingQuestion;
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
		Random r = new Random();
		// Start main while loop here.
		while(true){
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int random = r.nextInt(20);
			//decides whether or not to ask a question
			if(random == 0){
				//Team lead asking a question
				if(isLead){
					
				}
				//Employee asking a question
				else{
					
				}
			}
			
			
		}
		
	}

	
	public void askQuestion(){
		
	}
	
	public void setStartSignal(CountDownLatch startSignal) {
		this.startSignal = startSignal;
	}
	
	public void setStandupBarrier(CyclicBarrier standupBarrier){
		standupMeeting = standupBarrier;
	}
}
