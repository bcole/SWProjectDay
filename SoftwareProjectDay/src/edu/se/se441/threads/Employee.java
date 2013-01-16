package edu.se.se441.threads;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.Random;

import edu.se.se441.*;

public class Employee extends Thread {
	// Latches and Barriers
	private CountDownLatch startSignal;
	
	// Times
	private long dayStartTime, dayEndTime, lunchTime, lunchDuration;
	
	// Meeting attended Booleans
	private volatile boolean attendedEndOfDayMeeting;
	private Object leadQLock = new Object();
	private int teamNumber, empNumber;
	private boolean isLead;
	private volatile boolean isWaitingQuestion;
	private volatile boolean hadLunch;
	private Office office;
	private long timeSpentWaitingForAnswers = 0;
	private long timeSpentInMeetings = 0;
	private long timeSpentWorking = 0;
	private long timeSpentAtLunch = 0;

	
	public Employee(boolean isLead, Office office, int teamNumber, int empNumber){
		this.isLead = isLead;
		this.office = office;
		hadLunch = false;
		isWaitingQuestion = false;
		this.teamNumber = teamNumber;
		this.empNumber = empNumber;
		if(isLead) office.setLead(teamNumber, this);
	}

	public void run(){
		this.setName(getEmployeeName());
		Random r = new Random();
		try {
			// Starting all threads at the same time (clock == 0 / "8:00AM").
			startSignal.await();
			
			// Arrive sometime between 8:00 and 8:30
			Thread.sleep(10 * r.nextInt(30));

			System.out.println(office.getStringTime() + " Developer " + (int)(teamNumber+1) + "" + (int)(empNumber+1) + " arrives at office");
			dayStartTime = office.getTime();
			dayEndTime = dayStartTime + 800;	// Work at least 8 hours
			lunchTime = r.nextInt(60) + 1200 + r.nextInt(2)*100;	// Lunch starts between 12 and 2
			// BUG FIX (if slow computer / overpowered / hardware problem, force a short lunch)
			if((1700-dayEndTime-70) < 0){
				lunchDuration = 30 + (1700-dayEndTime-70);
			} else {
				lunchDuration = r.nextInt((int)(1700-dayEndTime-70)) + 30;	// Figure out lunch duration
			}
			dayEndTime += lunchDuration;		// Add to end time.
			
			office.addTimeEvent(lunchTime);		// Lunch Time
			office.addTimeEvent(dayEndTime);	// End of day

			// Waiting for team leads for the meeting.
			if(isLead){
				long startCheck = System.currentTimeMillis();
				office.waitForStandupMeeting();
				System.out.println(office.getStringTime() + " Developer " + (int)(teamNumber+1) + "" + (int)(empNumber+1) + " is at standup meeting");
				Thread.sleep(150);
				long endCheck = System.currentTimeMillis();
				
				timeSpentInMeetings += (endCheck - startCheck)/10;
			}
			long startCheck = System.currentTimeMillis();
			
			// Wait for the team meeting to start.
			office.waitForTeamMeeting(teamNumber);
			office.haveTeamMeeting(teamNumber, this);
			
			long endCheck = System.currentTimeMillis();
			
			timeSpentInMeetings += (endCheck - startCheck)/10;
			
			System.out.println(office.getStringTime() + " Developer " + (int)(teamNumber+1) + "" + (int)(empNumber+1) + " left team meeting");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Start main while loop here.
		while(true){
			// Wait until Employee should do something
			long startCheck = System.currentTimeMillis();
			office.startWorking();
			long endCheck = System.currentTimeMillis();
			
			timeSpentWorking += (endCheck - startCheck)/10;
			
			if(!runChecks()){
				break;
			}

			// If Leader, and question, ask manager
			if(isLead && isWaitingQuestion){
				office.getManager().askQuestion(this);
			}
			
			// Should a question be asked?
			int random = r.nextInt(20);
			//decides whether or not to ask a question
			if(random == 0){
				//Team lead asking a question
				if(isLead){
					office.getManager().askQuestion(this);
				}
				//Employee asking a question
				else{
					if(r.nextBoolean()){
						office.getLead(teamNumber).askQuestion(this);						
					}
				}
			}
			
			if(!runChecks()){
				break;
			}
		}
		System.out.println(office.getStringTime() + " Developer " + (int)(teamNumber+1) + "" + (int)(empNumber+1) + " leaves");
		System.out.println("Developer " + getEmployeeName() +" report: a) " + timeSpentWorking + " b) " + 
					timeSpentAtLunch + " c) " +  timeSpentInMeetings + " d) " + timeSpentWaitingForAnswers);
	}
	
	/**
	 * @return if false, break;
	 */
	private boolean runChecks(){
		// Check 1: Is it time to go home?
		if(office.getTime() >= dayEndTime){
			// End the thread
			return false;
		}
		
		// Lunch time?
		if(office.getTime() >= lunchTime && !hadLunch){
			try {
				long startCheck = System.currentTimeMillis();
				System.out.println(office.getStringTime() + " Developer " + (int)(teamNumber+1) + "" + (int)(empNumber+1) + " goes to lunch");
				sleep(lunchDuration*10);
				long endCheck = System.currentTimeMillis();
				
				timeSpentAtLunch += (endCheck - startCheck)/10;
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			hadLunch = true;
		}
		
		// Is it time for the 4 oclock meeting?
		if(office.getTime() >= 1600 && !attendedEndOfDayMeeting){
			long startCheck = System.currentTimeMillis();
			office.waitForEndOfDayMeeting();
			try {
				System.out.println(office.getStringTime() + " Developer " + (int)(teamNumber+1) + "" + (int)(empNumber+1) + " attends end of day meeting");
				sleep(150);
				long endCheck = System.currentTimeMillis();
				
				timeSpentInMeetings += (endCheck - startCheck)/10;
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			attendedEndOfDayMeeting = true;
		}
		
		return true;
	}
	
	// Only for Team Leaders, called when asked a question that needs to be passed up to manager.
	public void askQuestion(Employee asker){
		Employee teamLeader = office.getLead(teamNumber);
		try {
			synchronized(leadQLock){
				long startTime = System.currentTimeMillis();
				// Leader already has a question that hasn't been answered.
				while(teamLeader.isWaitingQuestion()){
					leadQLock.wait();
				}
				if(office.getTime() < 1700) return;
				
				// Set our question.
				teamLeader.getsQuestion();
				office.notifyWorking();
				long endTime = System.currentTimeMillis();
				
				timeSpentWaitingForAnswers =+ (endTime - startTime)/10;
			}

			synchronized(office.getManager().getQuestionLock()){
				// Is the manager answering the question
				while(teamLeader.isWaitingQuestion()){
					if(office.getTime() >= 1600 && !attendedEndOfDayMeeting){
						long startCheck = System.currentTimeMillis();
						office.waitForEndOfDayMeeting();
						System.out.println(office.getTime() + " Developer " + getEmployeeName() + " attends end of day meeting." );
						sleep(150);
						long endCheck = System.currentTimeMillis();
						
						timeSpentInMeetings += (endCheck - startCheck)/10;
						
						attendedEndOfDayMeeting = true;
					}

					long startTime = System.currentTimeMillis();
					office.getManager().getQuestionLock().wait();
					long endTime = System.currentTimeMillis();
					
					timeSpentWaitingForAnswers =+ (endTime - startTime)/10;
					
				}
			
				
			}

			long startTime = System.currentTimeMillis();
			synchronized(leadQLock){
				teamLeader.questionAnswered();
				leadQLock.notifyAll();
			}

			long endTime = System.currentTimeMillis();

			timeSpentWaitingForAnswers =+ (endTime - startTime)/10;
			
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void setStartSignal(CountDownLatch startSignal) {
		this.startSignal = startSignal;
	}
	
	public void getsQuestion(){
		isWaitingQuestion = true;
	}
	
	public void questionAnswered(){
		isWaitingQuestion = false;
	}
	
	public boolean isWaitingQuestion() {
		return isWaitingQuestion;
	}

	public boolean isAttendedEndOfDayMeeting() {
		return attendedEndOfDayMeeting;
	}

	public void setAttendedEndOfDayMeeting(boolean attendedEndOfDayMeeting) {
		this.attendedEndOfDayMeeting = attendedEndOfDayMeeting;
	}
	
	public boolean isLead(){
		return isLead;
	}
	
	public String getEmployeeName(){
		String name = (int)(teamNumber+1) + "" + (int)(empNumber+1);
		return name;
	}
}
