// Test
package edu.se.se441;

import java.util.concurrent.CyclicBarrier;

import edu.se.se441.threads.Clock;

public class Office {
	private Clock clock;
	private CyclicBarrier standupMeeting = new CyclicBarrier(4);
	private boolean confRoom;
	
	public Office(Clock clock){
		this.clock = clock;
	}
	
	public long getTime(){
		long time = clock.getTime();
		time = time/10;
		time += 800;
		return time;
	}

}
