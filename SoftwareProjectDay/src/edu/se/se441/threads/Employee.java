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
	
	// Meeting attended Booleans
	private boolean attendedEndOfDayMeeting;
	
	private int teamNumber;
	private boolean isLead;
	private boolean isWaitingQuestion;
	private boolean hadLunch;
	private Office office;

	
	public Employee(boolean isLead, Office office, int teamNumber){
		this.isLead = isLead;
		this.office = office;
		hadLunch = false;
		isWaitingQuestion = false;
		this.teamNumber = teamNumber;
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
			office.setEndOfDay(dayEndTime);
			
			// Waiting for team leads for the meeting.
			if(isLead){
				office.waitForStandupMeeting();
				Thread.sleep(150);
			}
			
			// Wait for the team meeting to start.
			office.waitForTeamMeeting(teamNumber);
			office.haveTeamMeeting(teamNumber);
			
			
			
			
			Thread.sleep(150);
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
			
			// Check 2: Is it time for the 4 oclock meeting?
			if(office.getTime() >= 1600 && !attendedEndOfDayMeeting){
				office.waitForEndOfDayMeeting();
				try {
					sleep(150);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				attendedEndOfDayMeeting = true;
			}
			
			// Last Check
			// Check 2: Should a question be asked?
			int random = r.nextInt(20);
			//decides whether or not to ask a question
			if(random == 0){
				//Team lead asking a question
				if(isLead){
					office.getManager().askQuestion(this);
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

	public boolean isAttendedEndOfDayMeeting() {
		return attendedEndOfDayMeeting;
	}

	public void setAttendedEndOfDayMeeting(boolean attendedEndOfDayMeeting) {
		this.attendedEndOfDayMeeting = attendedEndOfDayMeeting;
	}
}
