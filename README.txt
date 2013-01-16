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
8:43 14 has entered the conference room.
8:43 12 has entered the conference room.
8:43 Team 1 starts their team meeting.
8:58 Team 1 meeting has ended.
8:58 Team 1 meeting has ended.
8:58 Developer 12 left team meeting
8:58 Developer 13 left team meeting