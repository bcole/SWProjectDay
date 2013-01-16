package edu.se.se441;

import java.util.concurrent.CountDownLatch;

import edu.se.se441.threads.Clock;
import edu.se.se441.threads.Employee;
import edu.se.se441.threads.Manager;

public class Main {
    private static final int NUM_OF_EMPLOYEES = 12;
    /**
     * Main method.
     * Creates a starting latch and initializes the different threads.
     * Starts all the threads, then joins them together to end the program.
     * @param args - command line arguments (none accepted presently)
     */
    public static void main(String[] args) {
	System.out.println("Starting simulation...");

	// Create a latch that will start all the threads at the same time.
	CountDownLatch startSignal = new CountDownLatch(1);

	// Create clock thread class, won't start until startSignal counts down.
	Clock clock = new Clock(startSignal);

	// Create an office data object
	Office office = new Office(clock);
	clock.setOffice(office);

	// Create Manager
	Manager manager = new Manager(office);
	manager.setStartSignal(startSignal);

	// Create Employees
	Employee[] employees = new Employee[NUM_OF_EMPLOYEES];

	for(int i=0; i<NUM_OF_EMPLOYEES; i++){
	    employees[i] = (i%4==0) ? new Employee(true, office, i/4, i%4): new Employee(false, office, i/4, i%4);
	    employees[i].setStartSignal(startSignal);
	}


	// START THREADS
	for(int i=0; i<NUM_OF_EMPLOYEES; i++){
	    employees[i].start();
	}
	manager.start();
	clock.start();
	// Countdown the latch to release the threads.
	startSignal.countDown();

	// Join the threads back together to end the simulation.
	try {
	    clock.join();
	    manager.join();
	    for(int i=0; i<NUM_OF_EMPLOYEES; i++){
		employees[i].join();
	    }
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}

	System.out.println("Ending simulation...");
    }

}
