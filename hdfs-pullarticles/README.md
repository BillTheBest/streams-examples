Apache Streams (incubating)
Licensed under Apache License 2.0 - http://www.apache.org/licenses/LICENSE-2.0
--------------------------------------------------------------------------------

hdfs-pullarticles
==============================

Requirements:
-------------
 - A running Cloudera VM with HDFS/Hadoop running
 - A directory in HDFS populated with files containing a stream of activities (with their links unwound)
 - An empty directory in HDFS to store the articles associated with each of those links

This example includes a Pig script that takes in a directory which has files containing activities (with unwound links)
and outputs each corresponding article to a specified directory

Example Script Configuration:
----------------------

    REGISTER /home/cloudera/Desktop/hdfs-pullarticles-0.1-SNAPSHOT.jar

    DEFINE TIKAPULL org.apache.streams.pig.StreamsProcessDocumentExec('org.apache.streams.tika.TikaProcessor');

    activities = LOAD 'twitter-unwound/*' USING PigStorage('\t') AS (activityid: chararray, source: chararray, timestamp: long, object: chararray);

    articles = FOREACH activities GENERATE activityid, source, timestamp, TIKAPULL(object);

    result = FILTER articles BY $3 IS NOT NULL;

    STORE result INTO 'twitter-articles';

You need to:
 - Point the script to the location of the hdfs-pullarticles-0.1-SNAPSHOT.jar
 - Change 'twitter-unwound/*' to the directory on your HDFS instance which is storing the activity files (wiht unwound links)
 - Change 'twitter-articles' to the directory where you would like to store the fetched articles

Running:
--------

Once the script has been changed to reflect your environment, you simply need to execute it on your HDFS instance

Verification:
-------------
The directory that you set up to store the articles should now be populated with fetched articles from the activity links