#!/usr/bin/python

import re
import sys
import smtplib
import datetime
from email.mime.text import MIMEText
from subprocess import call
from subprocess import check_output

#Default email address as the from address
fromEmailAddr = "cheesemonkey300@gmail.com"

#Today's Date
todayDate = datetime.datetime.now().date()

#Accepts a string for the to address and for email subject
#A MIMEText object will be returned with to and from address,
#body, and subject will be set to values
def formatMail(toEmailAddr="cheesemonkey300@hotmail.com", emailSubject=""):
   message = MIMEText("Hello World!");

   message['Subject'] = emailSubject
   message['From'] = fromEmailAddr
   message['To'] = toEmailAddr

   return message



toEmailAddr = sys.argv[1]        #First command line argument is sent to toEmailAddr
emailSubject = ""                #emailSubject is set to empty string

#Checks for the first arguement to not be in email format
if not re.match("\w+@[A-Za-z]+\.[A-Za-z]+", toEmailAddr):
   toEmailAddr = ""
   print "Invalid Email Address\n" 
   sys.exit()                    #Exits program

#Concatentates the remainder of command line arguments as subject
for subjectCounter in range(2, len(sys.argv)):
   emailSubject = emailSubject + sys.argv[subjectCounter] + " "

print toEmailAddr + ": " + emailSubject + "\n"

#Calls formatMail to get a MIMEText object
message = formatMail(toEmailAddr, emailSubject)

#Sets up connection for Gmail SMTP
smtpConn = smtplib.SMTP('smtp.gmail.com:587')
smtpConn.ehlo()
smtpConn.starttls()

#Login to my account
smtpConn.login(fromEmailAddr, "monkeys?")

#Sends the MIMEText as a string to toEmailAddr from fromEmailAddr
smtpConn.sendmail(fromEmailAddr, [toEmailAddr], message.as_string())

#Quits SMTP connection
smtpConn.quit()


call(["gradle", "-q", "run"])             #Calls Java program to run


testFile = open("TestFile.txt", "rb+")    #Opens text file made by Java program

testFileArr = testFile.read().split(",")  #Splits String into array

formatTodayDate = ""                      #Holds new formatted string
todayArr = str(todayDate).split("-")      #Splits date into array

#Iterates through elements and concats them in backwards order
for s in range(len(todayArr)-1, -1, -1):
   formatTodayDate = formatTodayDate + todayArr[s]

#Checks for the same strings from text file and compares to Python values before email sent
if(str(testFileArr[2] + " ") == str(emailSubject) and str(formatTodayDate) == str(testFileArr[0])):
   print "Correct Subject and Date"
else:
   print "Incorrect Subject and Date"
