// Test
package edu.se.se441;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import edu.se.se441.threads.Clock;

public class Office {
	private Clock clock;
	
	// Barriers
	private CyclicBarrier standupMeeting = new CyclicBarrier(4);
	private CyclicBarrier[] teamMeetings = new CyclicBarrier[3];
	
	private boolean confRoom;
	private int confRoomUsedBy = 0;

	private Object confRoomLock;
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
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
	}
	
	public void waitForTeamMeeting(int teamNumber){
		try {
			teamMeetings[teamNumber].await();
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public synchronized void enterOffice() {
	    timeRegistry[numEmployeesCheckedIn] = getTime() + 800;
	    numEmployeesCheckedIn++;
	}
	public boolean confRoomOpen() {
		synchronized(confRoomLock){
			return confRoom;
		}
	}
	
	public void fillConfRoom(int teamNumber) {
		synchronized(confRoomLock){
			confRoom = false;
			confRoomUsedBy = teamNumber;
		}
	}
	
	public void emptyConfRoom() {
		synchronized(confRoomLock){
			confRoom = true;
			confRoomUsedBy = 0;
		}
		notifyAll();
	}
	
	public int getConfRoomUsedBy() {
		return confRoomUsedBy;
	}

	public void setConfRoomUsedBy(int confRoomUsedBy) {
		this.confRoomUsedBy = confRoomUsedBy;
	}

	public void haveTeamMeeting(int teamNumber) {
		try {
			synchronized(confRoomLock){
				while(getConfRoomUsedBy() != teamNumber){
					if(confRoomOpen()){
						// Fill the room.
						fillConfRoom(teamNumber);
						teamMeetings[teamNumber].reset();
					} else {
						// Wait until the room is open.
						wait();
					}
				}
				// Synchronize other team members to start the meeting at the same time.
				teamMeetings[teamNumber].await();
			}
			// Meeting starts.
			Thread.sleep(150);
			// Meeting ends.
			emptyConfRoom();		
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
	}

}

