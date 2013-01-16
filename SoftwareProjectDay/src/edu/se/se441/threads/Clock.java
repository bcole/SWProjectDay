package edu.se.se441.threads;

import java.util.Iterator;
import java.util.Vector;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import edu.se.se441.Office;

public class Clock extends Thread{

    private CountDownLatch startSignal;
    private long startTime;	// When the simulation starts.
    private Vector<Long> timeRegistry;
    private Office office;

    /**
     * Instantiates a new clock thread
     * @param startSignal the Latch that allows the clock to start
     */
    public Clock(CountDownLatch startSignal){
	this.startSignal = startSignal;
	timeRegistry = new Vector<Long>();
    }

    /**
     * Performs the actions of a time clock that simulates a day of work.
     */
    public void run(){
	try {
	    // Starting all threads at the same time (clock == 0 / "8:00AM").
	    startSignal.await();
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
	//Initializing the current time and the Random object
	startTime = System.currentTimeMillis();
	Random r = new Random();
	System.out.println("CLOCK STARTED");
	int numOfQuestions = 0;
	while(this.getTime() <= 5400){ //Simulation starts at 800 (time 0000) and ends at 1700 (time 5400).
	    synchronized(timeRegistry){
		Iterator<Long> iter = timeRegistry.iterator();
		while(iter.hasNext()){
		    Long t = iter.next();
		    if(this.getTime() >= t && office != null){
			iter.remove();
			office.notifyWorking();
		    }
		}
	    }
	    int random = r.nextInt(5000000);
	    if(random == 0 && office != null){	// Firing random questions.
		numOfQuestions++;
		office.notifyWorking();
	    }	
	}
	System.out.println(numOfQuestions);
	System.out.println("CLOCK ENDED");
	office.notifyWorking();
	synchronized(office.getManager().getQuestionLock()){
	    office.getManager().getQuestionLock().notifyAll();
	}
    }

    /**
     * Returns the current time in milliseconds
     * @return The time of day in ms (0ms is 8:00AM)
     */
    public long getTime(){
	return System.currentTimeMillis() - startTime;
    }

    /**
     * Adds a new time event to the registry
     * @param timeOfEvent the time of the event to be tracked
     */
    public void addTimeEvent(long timeOfEvent){
	synchronized(timeRegistry){
	    timeRegistry.add(timeOfEvent);
	}
    }
    
    /**
     * Sets the Clock's associated Office
     * @param o the Office to manage
     */
    public void setOffice(Office o){
	office = o;
    }
}
