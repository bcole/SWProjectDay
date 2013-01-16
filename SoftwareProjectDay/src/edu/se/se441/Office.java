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
	
	// Capacity of conference room.
	private int confRoom = 0;
	private int confRoomUsedBy = -1;
	
	// Locks.
	private Object workingLock = new Object();
	private Object confRoomLock = new Object();
	
	public Office(Clock clock){
		this.clock = clock;
		
		for(int i=0; i<teamMeetings.length; i++){
			teamMeetings[i] = new CyclicBarrier(4);
		}
	}
	
	/**
	 * Call when employee is doing nothing.
	 */
	public void startWorking(){
		synchronized(workingLock){
			try {
				workingLock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void notifyWorking(){
		synchronized(workingLock){
			workingLock.notifyAll();
		}
	}
	
	public long getTime(){
		// system milliseconds
		long time = clock.getTime();
		
		// simulation total minutes
		time = time/10;
		
		// get simulation hours
		int hours = (int) (time/60);
		
		// get just minutes (0<=minutes<60)
		int minutes = (int) time%60;
		
		// put hours and minutes together
		time = minutes + (hours * 100) + 800;
		return time;
	}
	
	public String getStringTime(){
		long time = getTime();
		
		int hours = (int) (time/100);
		hours = (hours>12) ? hours - 12 : hours;
		
		String minutes = Integer.toString((int) (time%100));
		minutes = (Integer.parseInt(minutes) < 10) ? "0" + minutes : minutes;
		
		return hours + ":" + minutes;
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
			synchronized(confRoomLock){
				while(getConfRoomUsedBy() != teamNumber){
					if(confRoomOpen()){
						// Fill the room.
						teamMeetings[teamNumber].reset();
						fillConfRoom(teamNumber);
					} else {
						//System.out.println(employee.getEmployeeName() + " stuck")
						// Wait until the room is open.
						confRoomLock.wait();
					
						//System.out.println(employee.getEmployeeName() + " Unstuck");
					}
				}
				
			}
			
			confRoom++;
			// Synchronize other team members to start the meeting at the same time.
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
			return confRoom==0;
		}
	}
	
	public void fillConfRoom(int teamNumber) {
		synchronized(confRoomLock){
			confRoomUsedBy = teamNumber;
			System.out.println(getStringTime() + " Conference room accquired by team " + (int)(teamNumber+1));
		}
	}
	
	public void emptyConfRoom() {
		synchronized(confRoomLock){
			confRoom--;
			
			// Only let the last one out notifyAll.
			if(confRoom == 0){
				confRoomUsedBy = -1;
				System.out.println(getStringTime() + " Conference Room Released");
				confRoomLock.notifyAll();
			}
		}
	}
	
	public int getConfRoomUsedBy() {
		return confRoomUsedBy;
	}

	public void setConfRoomUsedBy(int confRoomUsedBy) {
		this.confRoomUsedBy = confRoomUsedBy;
	}

}

