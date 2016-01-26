README

Getting Started
---------------

In order to run the QA for LegalWise application we need the following requirements:
1.	VM running a Linux distribution 
2.	Java 1.7 or higher 
3.	Maven 3.0 or higher


The question and answer system is currently hosted in a VM at FIU. Download the source code or clone it using GIT. 
The code is currently at https://github.com/FIU-SCIS-Senior-Projects/LegalWise
To run the back end application make sure to have the software requirements install in your system. 
On the command line set up your JAVA_HOME environment variable. Build the application using maven. 

Building the Source
-------------------

Prior to building the source, for those previously unfamiliar with Maven,
it may be wise to read this to avoid future hassles:
http://maven.apache.org/guides/getting-started/maven-in-five-minutes.html

To build the source, in QA_HOME:

   mvn clean package 
