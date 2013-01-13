package edu.se.se441;

import java.util.concurrent.CountDownLatch;

import edu.se.se441.threads.Employee;
import edu.se.se441.threads.Manager;

public class Main {
	private static final int NUM_OF_EMPLOYEES = 12;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Create a latch that will start all the threads at the same time.
		CountDownLatch startSignal = new CountDownLatch(1);
		
		// Create an office data object
		Office office = new Office();
		
		// Create Manager
		Manager manager = new Manager(office);
		manager.setStartSignal(startSignal);
		
		// Create Employees
		Employee[] employees = new Employee[NUM_OF_EMPLOYEES];
		for(int i=0; i<NUM_OF_EMPLOYEES; i++){
			employees[i] = (i%4==0) ? new Employee(true, office): new Employee(false, office);
			employees[i].setStartSignal(startSignal);
		}
		
		
		// START THREADS
		manager.start();
		for(int i=0; i<NUM_OF_EMPLOYEES; i++){
			employees[i].start();
		}
		startSignal.countDown();
		
	}

}
