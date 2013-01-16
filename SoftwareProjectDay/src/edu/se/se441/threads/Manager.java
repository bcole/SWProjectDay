package edu.se.se441.threads;

import java.util.concurrent.*;

import edu.se.se441.Office;

public class Manager extends Thread {

    final int NUMQUESTIONS = 10;

    private Office office;
    private CountDownLatch startSignal;
    private volatile BlockingQueue<Employee> hasQuestion = new ArrayBlockingQueue<Employee>(NUMQUESTIONS);
    private volatile boolean attendedMeeting1 = false;
    private volatile boolean attendedMeeting2 = false;
    private volatile boolean ateLunch = false;
    private volatile boolean attendedFinalMeeting = false;
    private Object questionLock = new Object();
    private long timeSpentAnsweringQuestions = 0;
    private long timeSpentInMeetings = 0;
    private long timeSpentWorking = 0;
    private long timeSpentAtLunch = 0;
    private volatile boolean atWork; //If the Manager is at work or not


    /**
     * Creates a Manager with a given office
     * @param office the Office the Manager belongs to
     */
    public Manager(Office office){
	this.office = office;
	office.setManager(this);
    }

    /**
     * Simulates the daily job of a project Manager
     */
    public void run(){
	//Adds the time event interrupts to the office
	office.addTimeEvent(1000);	// 10AM Meeting
	office.addTimeEvent(1200);	// Lunch Time
	office.addTimeEvent(1400);	// 2PM Meeting
	office.addTimeEvent(1600);	// 4PM Meeting
	office.addTimeEvent(1700);	// 5PM end of day

	try {
	    // Starting all threads at the same time (clock == 0 / "8:00AM").
	    startSignal.await();

	    System.out.println(office.getStringTime() + " Manager arrives at office");
	    atWork = true;
	    long startCheck = System.currentTimeMillis();
	    // Waiting for team leads for the meeting.
	    office.waitForStandupMeeting();
	    long endCheck = System.currentTimeMillis();

	    timeSpentWorking += (endCheck - startCheck)/10;

	    startCheck = System.currentTimeMillis();
	    Thread.sleep(150);
	    endCheck = System.currentTimeMillis();

	    timeSpentInMeetings += (endCheck - startCheck)/10;

	} catch (InterruptedException e) {
	    e.printStackTrace();
	}

	//While we aren't yet at the end of the day
	while(office.getTime() < 1700){
	    //Start working until interrupted
	    long startCheck = System.currentTimeMillis();
	    office.startWorking();
	    long endCheck = System.currentTimeMillis();

	    timeSpentWorking += (endCheck - startCheck)/10; 
	    //While your question queue has questions in it
	    while(!hasQuestion.isEmpty()){
		//Analyze the situation to see if you can answer the question
		checkConditions();
		startCheck = System.currentTimeMillis();
		//Answer the question
		answerQuestion();
		endCheck = System.currentTimeMillis();

		timeSpentAnsweringQuestions += (endCheck - startCheck)/10;				
	    }
	    //Check the situation again to make sure you're good to go
	    checkConditions();

	}
	System.out.println(office.getStringTime() + " Manager leaves");
	atWork = false;
	//Get locks to ensure that no more questions get asked
	synchronized(office.getLeadQLock()){
	    office.getLeadQLock().notifyAll();
	}
	synchronized(questionLock){
	    questionLock.notifyAll();
	}
	//Report the manager's stats for the day
	System.out.println("Manager report: a) " + timeSpentWorking + " b) " + timeSpentAtLunch +
		" c) " + timeSpentInMeetings + " d) " + timeSpentAnsweringQuestions);
    }

    /**
     * Sets the start signal for the manager
     * @param startSignal the Latch to use as a start signal
     */
    public void setStartSignal(CountDownLatch startSignal) {
	this.startSignal = startSignal;
    }

    /**
     * Returns whether or not the Manager is at work
     * @return the location of the Manager, atWork or not
     */
    public boolean atWork(){
	return atWork;
    }

    /**
     * 
     */
    public void checkConditions(){
	//If we haven't had our 10AM meeting but it's 10AM+...
	if(office.getTime() >= 1000 && !attendedMeeting1){
	    try {
		long startCheck = System.currentTimeMillis();
		//Go to the meeting
		System.out.println(office.getStringTime() + " Manager goes to meeting");
		sleep(600);
		long endCheck = System.currentTimeMillis();

		timeSpentInMeetings += (endCheck - startCheck)/10;

	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	    //Now we have attended the first meeting
	    attendedMeeting1 = true;
	    //Notify people that we can answer questions
	    synchronized(questionLock){
		questionLock.notifyAll();
	    }
	}
	//If it's around 12pm and we haven't eaten lunch yet
	if(office.getTime() >= 1200 && !ateLunch){
	    try {
		//Eat some lunch
		long startCheck = System.currentTimeMillis();
		System.out.println(office.getStringTime() + " Manager goes to lunch");
		sleep(600);
		long endCheck = System.currentTimeMillis();

		timeSpentAtLunch += (endCheck - startCheck)/10;

	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }			
	    //Now we have eaten lunch
	    ateLunch = true;
	    //Notify people that we can answer questions
	    synchronized(questionLock){
		questionLock.notifyAll();
	    }
	}
	//If it's around 2PM and we haven't had the 2PM meeting yet
	if(office.getTime() >= 1400 && !attendedMeeting2){
	    try {
		//Go to the meeting
		long startCheck = System.currentTimeMillis();
		System.out.println(office.getStringTime() + " Manager goes to meeting");
		sleep(600);
		long endCheck = System.currentTimeMillis();

		timeSpentInMeetings += (endCheck - startCheck)/10;

	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	    //Now we have attended the meeting
	    attendedMeeting2 = true;
	    //Notify people that we can answer questions
	    synchronized(questionLock){
		questionLock.notifyAll();
	    }
	}

	// Is it time for the 4 o'clock meeting?
	if(office.getTime() >= 1600 && !attendedFinalMeeting){
	    //Announce to answer any immediate questions
	    synchronized(questionLock){
		questionLock.notifyAll();
	    }
	    //Notify the office leads about further questions
	    for(int i = 0; i < 3; i++){
		synchronized(office.getLead(i).getLeadQLock()){
		    office.getLead(i).getLeadQLock().notifyAll();
		}
	    }
	    //Wait for the end of the day meeting to start
	    //System.out.println("Manager is waiting for end of day meeting");
	    office.waitForEndOfDayMeeting();
	    try {
		long startCheck = System.currentTimeMillis();				
		System.out.println(office.getStringTime() + " Manager attends the end of day meeting");
		sleep(150);
		long endCheck = System.currentTimeMillis();

		timeSpentInMeetings += (endCheck - startCheck)/10;

	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	    //Now we have attended the meeting
	    attendedFinalMeeting = true;
	    //Notify any remaining questioners
	    synchronized(questionLock){
		questionLock.notifyAll();
	    }
	}
    }

    /**
     * Asks a question for a given employee
     * @param employee the Employee to assist
     */
    public void askQuestion(Employee employee){
		// Add question to queue
    	long startCheck = System.currentTimeMillis();
    	
		synchronized(hasQuestion){
		    hasQuestion.add(employee);
		}
		
		// Waiting until question can be answered
		while(hasQuestion.contains(employee)){
		    // Is it time for the 4 o'clock meeting?
		    try {
				if(office.getTime() >= 1600 && !employee.isAttendedEndOfDayMeeting()){
				    office.waitForEndOfDayMeeting();
				    System.out.println(office.getStringTime() + " Developer " + employee.getEmployeeName() + " attends end of day meeting");
				    sleep(150);
				    employee.setAttendedEndOfDayMeeting(true);
				}
				synchronized(questionLock){
				    // Tell the Manager there is a question.
				    office.notifyWorking();
				    questionLock.wait();
				}
		    }catch (InterruptedException e) {
		    	e.printStackTrace();
		    }
		}
		
		synchronized(questionLock){
		    // Question is being answered
		    while(employee.isWaitingQuestion()){
				try {
				    questionLock.wait();
				} catch (InterruptedException e) {
				    e.printStackTrace();
				}
		    }
		}
		
		long endCheck = System.currentTimeMillis();
		
		employee.addToWaitingQuestion((endCheck - startCheck)/10);

		System.out.println(office.getStringTime() + " Developer " + employee.getEmployeeName() + "'s question is answered");
    }

    /**
     * Checks if the lead is asking a question
     * @param lead the Team Leader
     * @return if the question queue has the given leader in it
     */
    public boolean isLeadAsking(Employee lead){
	return hasQuestion.contains(lead);
    }

    /**
     * Returns the Manager's question lock
     * @return the Manager's question lock
     */
    public Object getQuestionLock(){
	return questionLock;
    }

    /**
     * Answers a question for a given employee
     */
    private void answerQuestion(){
	//Gets the top question from the queue
	Employee employee;
	//If it's still before closing time
	if(office.getTime() < 1700){
	    //Notify that a question is being answered
	    synchronized(questionLock){
	    employee = hasQuestion.poll();
		questionLock.notifyAll();
	    }
	    //Answer the question
	    try {
		System.out.println(office.getStringTime() + " Manager starts answering question from Developer " + employee.getEmployeeName() +  ".");
		sleep(100);
		System.out.println(office.getStringTime() + " Manager answers question.");
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	    //Tell the employee the answer to the question
	    employee.questionAnswered();
	    //Notify remaining questioners
	    synchronized(questionLock){
		questionLock.notifyAll();
	    }
	} else{
	    //If it's not closing time, keep answering questions
	    while(!hasQuestion.isEmpty()){
		employee = hasQuestion.poll();
		employee.questionAnswered();
	    }
	    //Inform employees that you have answered a question
	    synchronized(questionLock){
		questionLock.notifyAll();
	    }
	}
    }

}
