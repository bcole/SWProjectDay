package edu.se.se441;

import edu.se.se441.threads.Employee;

public class Main {
	private static final int NUM_OF_EMPLOYEES = 12;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Create an office data object
		Office office = new Office();
		
		// Create Manager
//		Manager manager = new Manager(office);
		
		// Create Employees
		Employee[] employees = new Employee[NUM_OF_EMPLOYEES];
		for(int i=0; i<NUM_OF_EMPLOYEES; i++){
//			employees[i] = new Employee(office);
		}
		
	}

}
