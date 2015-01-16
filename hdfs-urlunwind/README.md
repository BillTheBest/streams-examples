Apache Streams (incubating)
Licensed under Apache License 2.0 - http://www.apache.org/licenses/LICENSE-2.0
--------------------------------------------------------------------------------

hdfs-urlunwind
==============================

Requirements:
-------------
 - A running Cloudera VM with HDFS/Hadoop running
 - A directory in HDFS populated with files containing a stream of activities
 - An empty directory in HDFS to store those same activities, but with their links unwound

This example includes a Pig script that takes in a directory which has files containing activities and unwinds the links
that are present in those activities. These activities (with the now unwound links) will then be written to an empty
directory in HDFS

Example Script Configuration:
----------------------

    REGISTER /home/cloudera/Desktop/hdfs-urlunwind-0.1-SNAPSHOT.jar

    DEFINE UNWINDER org.apache.streams.pig.StreamsProcessDocumentExec('org.apache.streams.urls.LinkResolverProcessor');

    activities = LOAD 'twitter-activity/*' USING PigStorage('\t') AS (activityid: chararray, source: chararray, timestamp: long, object: chararray);

    unwound = FOREACH activities GENERATE activityid, source, timestamp, UNWINDER(object);

    result = FILTER unwound BY $3 IS NOT NULL;

    STORE result INTO 'twitter-unwound';

You need to:
 - Point the script to the location of the hdfs-urlunwind-0.1-SNAPSHOT.jar
 - Change 'twitter-activity/*' to the directory on your HDFS instance which is storing the activity files
 - Change 'twitter-unwound' to the directory where you would like the new activies (with unwound links) to be stored

Running:
--------

Once the script has been changed to reflect your environment, you simply need to execute it on your HDFS instance

Verification:
-------------
The directory that you set up to store the unwound activities should now contain all the activities with their links unwound