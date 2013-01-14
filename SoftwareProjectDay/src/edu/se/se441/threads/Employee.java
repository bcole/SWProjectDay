package edu.se.se441.threads;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.Random;

import edu.se.se441.*;

public class Employee extends Thread {
	// Latches and Barries
	private CountDownLatch startSignal;
	
	// Times
	private long dayStartTime, dayEndTime;
	
	private boolean isLead;
	private boolean isWaitingQuestion;
	private boolean hadLunch;
	private Office office;
	
	public Employee(boolean isLead, Office office){
		this.isLead = isLead;
		this.office = office;
		hadLunch = false;
		isWaitingQuestion = false;
	}

	public void run(){
		Random r = new Random();
		try {
			// Starting all threads at the same time (clock == 0 / "8:00AM").
			startSignal.await();
			
			// Arrive sometime between 8:00 and 8:30
			Thread.sleep(10 * r.nextInt(30));
			dayStartTime = office.getTime();
			dayEndTime = dayStartTime + 800;	// Work at least 8 hours
			
			// Waiting for team leads for the meeting.
			if(isLead){
				office.waitForStandupMeeting();
				Thread.sleep(150);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Start main while loop here.
		while(true){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// Check 1: Is it time to go home?
			if(office.getTime() >= dayEndTime){
				// End the thread
				break;
			}
			
			
			
			
			// Last Check
			// Check 2: Should a question be asked?
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
}
