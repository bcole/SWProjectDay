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
	private long dayStartTime, dayEndTime, lunchTime, lunchDuration;
	
	// Meeting attended Booleans
	private boolean attendedEndOfDayMeeting;
	
	private int teamNumber, empNumber;
	private boolean isLead;
	private boolean isWaitingQuestion;
	private boolean hadLunch;
	private Office office;

	
	public Employee(boolean isLead, Office office, int teamNumber, int empNumber){
		this.isLead = isLead;
		this.office = office;
		hadLunch = false;
		isWaitingQuestion = false;
		this.teamNumber = teamNumber;
		this.empNumber = empNumber;
	}

	public void run(){
		this.setName(getEmployeeName());
		Random r = new Random();
		try {
			// Starting all threads at the same time (clock == 0 / "8:00AM").
			startSignal.await();
			
			// Arrive sometime between 8:00 and 8:30
			Thread.sleep(10 * r.nextInt(30));
			

			System.out.println(office.getTime() + " Developer " + (int)(teamNumber+1) + "" + (int)(empNumber+1) + " arrives at office");
			dayStartTime = office.getTime();
			dayEndTime = dayStartTime + 800;	// Work at least 8 hours
			lunchTime = r.nextInt(200) + 1200;
			lunchDuration = r.nextInt((int) (1700-dayEndTime-30)) + 30;	// Figure out lunch duration
			dayEndTime += lunchDuration;	// Add to end time.
			
			office.setEndOfDay(dayEndTime);
			
			// Waiting for team leads for the meeting.
			if(isLead){
				office.waitForStandupMeeting();
				System.out.println(office.getTime() + " Developer " + (int)(teamNumber+1) + "" + (int)(empNumber+1) + " is at standup meeting");
				Thread.sleep(150);
			}
			
			// Wait for the team meeting to start.
			office.waitForTeamMeeting(teamNumber);
			office.haveTeamMeeting(teamNumber, this);
			
			System.out.println(office.getTime() + " Developer " + (int)(teamNumber+1) + "" + (int)(empNumber+1) + " left team meeting");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Start main while loop here.
		while(true){
			try {
				synchronized(this){
					wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// Check 1: Is it time to go home?
			if(office.getTime() >= dayEndTime){
				// End the thread
				break;
			}
			
			// Lunch time?
			if(office.getTime() >= lunchTime && !hadLunch){
				try {
					System.out.println(office.getTime() + " Developer " + (int)(teamNumber+1) + "" + (int)(empNumber+1) + " goes to lunch");
					sleep(lunchDuration*10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				hadLunch = true;
			}
			
			// Is it time for the 4 oclock meeting?
			if(office.getTime() >= 1600 && !attendedEndOfDayMeeting){
				office.waitForEndOfDayMeeting();
				try {
					System.out.println(office.getTime() + " Developer " + (int)(teamNumber+1) + "" + (int)(empNumber+1) + " attends end of day meeting");
					sleep(150);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				attendedEndOfDayMeeting = true;
			}
			
			if(isLead && isWaitingQuestion){
				office.getManager().askQuestion(this);
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
					if(r.nextBoolean()){
						office.getLead(teamNumber).askQuestion(this);						
					}
				}
			}
		}
		System.out.println(office.getTime() + " Developer " + (int)(teamNumber+1) + "" + (int)(empNumber+1) + " leaves");
	}
	
	// Only for Team Leaders, called when asked a question that needs to be passed up to manager.
	public void askQuestion(Employee asker){
		Employee teamLeader = office.getLead(teamNumber);
		try {
			//TODO add synchronized block.
			// Leader already has a question that hasn't been answered.
			while(teamLeader.isWaitingQuestion){
				wait();
			}
			
			// Set our question.
			teamLeader.getsQuestion();
			notifyAll();
			
			
			while(teamLeader.isWaitingQuestion){
				wait();
			}
			
			
			
			
			// Is the manager answering the question
			while(office.getManager().isLeadAsking(teamLeader)){
				if(office.getTime() >= 1600 && !attendedEndOfDayMeeting){
					office.waitForEndOfDayMeeting();
					sleep(150);
					attendedEndOfDayMeeting = true;
				}
				wait();
			}
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		teamLeader.questionAnswered();
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
	
	public String getEmployeeName(){
		String name = (int)(teamNumber+1) + "" + (int)(empNumber+1);
		return name;
	}
}
