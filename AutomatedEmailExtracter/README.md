# README #
I authorize Prof. Grechanik and his RAs and TAs to use my google account credentials for this project for academic research purposes. I understand the
risk of sharing my private information with this account.

Gmail Account: cheesemonkey300@gmail.com
Password: monkeys?

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

The email sent is hard-coded through the build.grade file.
The Java program looks through all gmail email and extracts date, time,
and subject and adds them to a spreadsheet which is created through an API call.
I could not get JUnit to work, so I created my own test by having my Java
program write to a file with the latest row from the spreadsheet. That the
Python script picks up from the working directory and analyzes to see if the
strings are the same.

Let me know if something does not work, it's bound to happen, but I'd like to 
be informed! :)