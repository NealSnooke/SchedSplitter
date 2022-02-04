# SchedSplitter
Simple tool to pre process csv data from a gps logger.

Installation:
=============

Sched Splitter is a JavaFX application that reads csv file containing gps data obtained from tracking devices.

The JRE packaged runtime version can be run using the runWin or runMac scripts.
These simply run the packaged java runtime with the relevant class file:
./customjre/bin/java --module schedsplitter/openjfx.MainApp

Building:
=========

The buildWin and buildMac scripts build the packaged application on the relevant platforms (assuming Java is installed).
The system was built using OpenJdk 14.0.2.
The javafx libraries need to be in a folder ./libraries. The current system was built and tested using the following versions of the JavaFx libraries:

Windows

javafx-jmods-17.0.0.1

javafx-sdk-17.0.0.1

Mac

javafx-jmods-11.0.2

javafx-sdk-11.0.2

Features:
=========

The application contains 3 tabs as follows. 

Settings tab.
-------------

An individual file or multiple files stored in a folder can be processed. A path or filename must be provided in the Folder/File area. The Browse button activates a standard file chooser.

The File Format area allows specification of which columns in the input files contains the latitude, longiture date and time information.

The date and time format can be specified using standard format specifiers and there is a dropdown that includes some common formats. Any format that can be parsed by the java DateTimeFormatter library can be used. See the following reference for a list of all formatting symbols:

https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html

Check the log tab for more information if data/time parsing is fails.

The Append time zone checkbox will allow the system to add a timezone offset if the data does not include one. The default value will assume GMT.

The data may include samples that have multiple sampling frequencies. For example minute data with occasional bursts of one second data for a few minutes each hour. The Time interval range area allows specification of the samples to be extracted. Notice that samples may be selected from a higher frequency sampling regime to provide the required frequency. In the example ablve some samples from the one second data will be used to produce minute data. The first sample that satisfies the minumum interval will be included and subsequently the next sample that satisfies the minimum from that time etc. 

Date selection may be included to extract data from specific periods.

The Process Files button will initial processing. This will cause the extracted data to be written to new files and a summary to be produced in the Result summary tab.

Results tab.
------------

This tab will show a summary of the processing results. Specifically the number of items (lines) read from each file and the number of items that matched the specification for columns provided.  

Log summary tab. 
----------------

This tab shows various information about the processing operations. The settings page allows for three levels of information to be displayed. The debug mode will display a great deal of information and may cause GUI resource problems if used on very large files. It is intended to solve issues where a small sample set of data is being used to set up or diagnose issues. 
