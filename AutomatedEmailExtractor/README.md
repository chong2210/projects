# README #

This is a project I made for my CS 441 course at UIC. The idea is to send an email from a Python script. The Python script will invoke a Java program to extract all of the email's send times and subject lines from the receiving Gmail account. That will be stored into a Google Spreedsheet. The information will be extracted from that Spreedsheet and saved into a text file to compare after returning to the Python script. This is then compared to what was sent from the script to act as a unit test.

I included: build.gradle, GmailQuickstart.java, sendEmail.py, and 
client_secret.json.

Environment is being assumed to be set up. By that I mean, all of the Gradle:
gradle init --type basic
mkdir -p src/main/java src/main/resources

and the files are moved to their respective directories:
GmailQuickstart.java moved to src/main/java
build.gradle is my build.gradle in the repo

My gradle has a task in it called runPython

This script will send an email to my Gmail account,
after that it will run the Java program by running gradle -q run,
and finally, come back to compare the date and subject matter of this
Python Script and the Java Program.

The program will be ran by only one command: gradle runPython