Apache Streams (incubating)
Licensed under Apache License 2.0 - http://www.apache.org/licenses/LICENSE-2.0
--------------------------------------------------------------------------------

hdfs-converttoactivity
==============================

Requirements:
-------------
 - A running Cloudera VM with HDFS/Hadoop running
 - A directory in HDFS populated with files containing raw tweets
 - An empty directory in HDFS to store the activities generated from these tweets

This example includes a Pig script that takes in a directory which has files containing raw tweets
and exports activities generated from those tweets

Example Script Configuration:
----------------------

    REGISTER /home/cloudera/Desktop/hdfs-converttoactivity-0.1-SNAPSHOT.jar

    DEFINE SERIALIZER org.apache.streams.pig.StreamsSerializerExec('org.apache.streams.twitter.serializer.TwitterJsonActivitySerializer');

    tweets = LOAD 'testtweets/*' USING PigStorage('\t') AS (tweetid: chararray, source: chararray, timestamp: chararray, object: chararray);

    activities = FOREACH tweets GENERATE
        tweetid AS tweetid,
        source AS source,
        timestamp AS timestamp,
        SERIALIZER(object) AS activity;

    result = FILTER activities BY activity IS NOT NULL;

    STORE result INTO 'twitter-activity';

You need to:
 - Point the script to the location of the hdfs-converttoactivity-0.1-SNAPSHOT.jar
 - Change 'testtweets/*' to the directory on your HDFS instance which is storing the raw tweets
 - Change 'twitter-activity' to the directory where you would like to store the generated activity files

Running:
--------

Once the script has been changed to reflect your environment, you simply need to execute it on your HDFS instance

Verification:
-------------
The directory that you set up to store the activities should now be filled with activities that were generated from the raw tweets