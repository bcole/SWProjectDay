// Test
package edu.se.se441;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import edu.se.se441.threads.Clock;

public class Office {
	private Clock clock;
	private CyclicBarrier standupMeeting = new CyclicBarrier(4);
	private boolean confRoom;
	private long[] timeRegistry;
	private int numEmployeesCheckedIn;
	
	public Office(Clock clock){
		this.clock = clock;
		confRoom = true;
		timeRegistry = new long[12];
		numEmployeesCheckedIn = 0;
	}
	
	public long getTime(){
		long time = clock.getTime();
		time = time/10;
		time += 800;
		return time;
	}
	
	public void waitForStandupMeeting(){
		try {
			standupMeeting.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
	}
	
	public void enterOffice() {
	    timeRegistry[numEmployeesCheckedIn] = getTime();
	    numEmployeesCheckedIn++;
	}
	public boolean confRoomOpen() {
	    return confRoom;
	}
	
	public void fillConfRoom() {
	    confRoom = false;
	}
	
	public void emptyConfRoom() {
	    confRoom = true;
	}

}

