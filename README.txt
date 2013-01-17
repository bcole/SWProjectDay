The program to be executed is in the SoftwareProjectDay folder,
called Main.java.
When the program is executed, it begins immediately initializing objects
and preparing to start the "day." It then starts all the threads through
a latch. There is no user input needed.

As of right now there are no configurable constants that can be safely
changed in the code. But there are a few known constants:
1/20 - the chance that an employee will ask a question each time it loops.
12 - number of employees, with 3 teams containing 4 people (and 1 lead each)
1 - number of Managers
4 - capacity of conference room
1 - number of conference rooms

The program prints out several statements anytime an event occurs, like
an employee asking a question or a manager going to lunch.
When defining the different workers, we assign them a number for their
team and position. One example is Developer "32" - He is the second
developer on team 3. Team leads are considered the first developer in
their team.
Here is an example output from the program:
12:00 Manager goes to lunch
12:00 Developer 22 asks Team Lead a question (has answer).
12:06 Developer 23 goes to lunch
12:15 Developer 13 goes to lunch
12:15 Developer 24 asks Team Lead a question (doesn't have answer).
12:15 Developer 21 passes the question to the Manager.
12:15 Developer 22 asks Team Lead a question (has answer).
12:22 Developer 32 asks Team Lead a question (has answer).
12:22 Developer 33 goes to lunch
12:41 Developer 12 goes to lunch
1:00 Manager starts answering question from Developer 21.
1:02 Developer 11 goes to lunch
1:02 Developer 23 asks Team Lead a question (has answer).
1:10 Manager answers question.
1:10 Developer 32 goes to lunch