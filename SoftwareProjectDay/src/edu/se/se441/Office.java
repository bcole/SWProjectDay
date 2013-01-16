// Test
package edu.se.se441;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import edu.se.se441.threads.*;

public class Office {
	private Clock clock;
	
	// Barriers
	private CyclicBarrier standupMeeting = new CyclicBarrier(4);
	private CyclicBarrier[] teamMeetings = new CyclicBarrier[3];
	private CyclicBarrier endOfDayMeeting = new CyclicBarrier(13);
	
	// Manager and Employees
	private Manager manager;
	private Employee[] leads = new Employee[3];
	
	private boolean confRoom;
	private int confRoomUsedBy = -1;

	private Object confRoomLock = new Object();
	private long[] timeRegistry;
	private int numEmployeesCheckedIn;
	
	public Office(Clock clock){
		this.clock = clock;
		confRoom = true;
		timeRegistry = new long[12];
		numEmployeesCheckedIn = 0;
		
		for(int i=0; i<teamMeetings.length; i++){
			teamMeetings[i] = new CyclicBarrier(4);
		}
	}
	
	public long getTime(){
		long time = clock.getTime();
		time = time/10;
		time += 800;
		return time;
	}
	
	
	// Barrier functions (for meetings)
	public void waitForStandupMeeting(){
		try {
			standupMeeting.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (BrokenBarrierException e2) {
			e2.printStackTrace();
		}
	}
	
	public void waitForTeamMeeting(int teamNumber){
		try {
			teamMeetings[teamNumber].await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (BrokenBarrierException e2) {
			e2.printStackTrace();
		}
	}
	
	public void haveTeamMeeting(int teamNumber, Employee employee) {
		try {
			synchronized(employee){
				while(getConfRoomUsedBy() != teamNumber){
					if(confRoomOpen()){
						// Fill the room.
						teamMeetings[teamNumber].reset();
						fillConfRoom(teamNumber);
					} else {
						//System.out.println(employee.getEmployeeName() + " stuck")
						// Wait until the room is open.
						employee.wait();
					
						//System.out.println(employee.getEmployeeName() + " Unstuck");
					}
				}
				
			}
			// Synchronize other team members to start the meeting at the same time.
			System.out.println(employee.getEmployeeName() +" waiting for team meeting to start.");
			teamMeetings[teamNumber].await();
			
			// Meeting starts.
			Thread.sleep(150);
			// Meeting ends.
			emptyConfRoom();		
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (BrokenBarrierException e2) {
			e2.printStackTrace();
		}
	}

	public void waitForEndOfDayMeeting() {
		try {
			endOfDayMeeting.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (BrokenBarrierException e2) {
			e2.printStackTrace();
		}
	}
	
	
	
	// Getters and setters for Manager and Leads
	public Manager getManager() {
		return manager;
	}

	public void setManager(Manager manager) {
		this.manager = manager;
	}

	public Employee getLead(int teamNumber) {
		return leads[teamNumber];
	}
	
	public void setLead(int teamNumber, Employee lead) {
		this.leads[teamNumber] = lead;
	}

	public CyclicBarrier[] getTeamMeetings(){
		return teamMeetings;		
	}
	
	
	public synchronized void setEndOfDay(long time) {
	    //timeRegistry[numEmployeesCheckedIn] = time;
	    //numEmployeesCheckedIn++;
		
//		clock.addTimeEvent(time);
	}
	public boolean confRoomOpen() {
		synchronized(confRoomLock){
			return confRoom;
		}
	}
	
	public void fillConfRoom(int teamNumber) {
		System.out.println("filling1");
		synchronized(confRoomLock){
			confRoom = false;
			confRoomUsedBy = teamNumber;
			System.out.println("Conference room accquired by team " + (int)(teamNumber+1));
		}
		System.out.println("filled");
	}
	
	public void emptyConfRoom() {
		synchronized(confRoomLock){
			confRoom = true;
			confRoomUsedBy = -1;
			System.out.println("Conference Room Released");
		}
		synchronized(this){
			notifyAll();
		}
	}
	
	public int getConfRoomUsedBy() {
		return confRoomUsedBy;
	}

	public void setConfRoomUsedBy(int confRoomUsedBy) {
		this.confRoomUsedBy = confRoomUsedBy;
	}

}

