package edu.se.se441.threads;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import edu.se.se441.Office;

public class Manager extends Thread {
	
	final int NUMQUESTIONS = 10;
	
	private Office office;
	private CyclicBarrier standupMeeting;
	private CountDownLatch startSignal;
	private BlockingQueue<Employee> hasQuestion = new ArrayBlockingQueue<Employee>(NUMQUESTIONS);
	private boolean attendedMeeting1 = false;
	private boolean attendedMeeting2 = false;
	private boolean ateLunch = false;
	
	public Manager(Office office){
		this.office = office;
	}
	
	public void run(){
		try {
			// Starting all threads at the same time (clock == 0 / "8:00AM").
			startSignal.await();
			
			// Waiting for team leads for the meeting.
			standupMeeting.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
		while(office.getTime() < 1700){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(!hasQuestion.isEmpty()){
				while(!hasQuestion.isEmpty()){
					answerQuestion();
				}
			}
			if(office.getTime() >= 1000 && !attendedMeeting1){
				 try {
					sleep(600);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				 attendedMeeting1 = true;
			}
			if(office.getTime() >= 1200 && !ateLunch){
				try {
					sleep(600);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}				
				ateLunch = true;
			}
			if(office.getTime() >= 1400 && !attendedMeeting2){
				try {
					sleep(600);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				attendedMeeting2 = true;
			}
		}
	}
	
	public void setStandupBarrier(CyclicBarrier standupBarrier){
		standupMeeting = standupBarrier;
	}
	
	public void setStartSignal(CountDownLatch startSignal) {
		this.startSignal = startSignal;
	}
	
	public void askQuestion(Employee employee){
		synchronized(hasQuestion){
			hasQuestion.add(employee);
		}
		while(hasQuestion.contains(employee)){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void answerQuestion(){
		try {
			sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		hasQuestion.poll();
		notifyAll();
	}

}
