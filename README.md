Author: 
Gelin	Jiofack	Nguedong
Gerrit	Schelter
Magnus	Halbe
Philipp	Staudt
Sven	Stauden
Timo	Speith
Tobias	Sander



# Introduction #

**DroidMateGUI** is an front-end for the GUI test generator **DroidMate**.

This file pertains to DroidMateGUI source. You should have found it at DroidMateGUI
repository root dir. 

The repository address:

  https://se.st.cs.uni-saarland.de/web-front-end-for-android-gui-test-generator.git
  
 This file is part of the “DroidMateGUI” project.
 
 
 This file explains:

- How DroidMateGUI works.
- How to build and test DroidMateGUI.
- How to setup an IDE for development.


# How DroidMateGUI works #

DroidMateGUI connects to DroidMate application and provides multiple .apk files for exploration which have previously been selected by the user.
It reads DroidMates` output via stdout and visualizes it in realtime in the browser.

As input, DroidMateGUI takes a folder containing .apk files for testing. This folder can be selected by the user in the browser in which DroidMateGUI runs.
After folder selection, DroidMateGUI passes the file path of the selected files to DroidMate for processing.

# Building and testing DroidMate #

DroidMateGUI is built as a .war using e.g. an IDE like eclipse EE (http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/mars1). 


## First build (just after cloning from repo)

To build DroidMate for first time, follow these steps:


### 1. Setup the dependencies ###
1. Install Java Development Kit (JDK) 8, 7 and 6.

2. Install Tomcat 8.0

3. Install Eclipse EE


### 2. Do the build ###

1. Import the project in Eclipse.

2. Setup a Tomcat Server in Eclipse (Window->Preferences->Server->Runtime Environment->Add) by selecting a Apache Tomcat Server 8.0 and set the path to the installed Tomcat 8.0 directory.

3. Install maven dependencies via maven update and maven install.

4. Build the .war file by File->Export->WAR File

5. Place the .war file in your apache-tomcat-folder\webapps\ folder.

6. Visit the url: http://localhost.com:8080/DroidMate


### Dependencies ###

json: https://github.com/douglascrockford/JSON-java


