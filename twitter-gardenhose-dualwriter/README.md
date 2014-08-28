twitter-gardenhose-dualwriter
==============================

Requirements:
-------------
 - Authorized Twitter API credentials
 - A running ElasticSearch 1.0.0+ instance
 - A running hadoop cluster
 - A running HttpFs service

Description:
------------
Listens for tweets, converts them to activities, and writes them in activity form to elasticsearch and raw form to HDFS

Configuration:
--------------
    include "reference"
    twitter {
        endpoint = "sample"
        oauth {
            consumerKey = ""
            consumerSecret = ""
            accessToken = ""
            accessTokenSecret = ""
        }
        track = [
            apache
            hadoop
            pig
            hive
            cassandra
            elasticsearch
            mongo
            data
            apache storm
            apache streams
            big data
            asf
            opensource
            open source
            apachecon
            apache con
        ]
    }
    elasticsearch {
        hosts = [
            localhost
        ]
        port = 9300
        clusterName = elasticsearch
        index = gardenhose_activity
        type = activity
    }
    hdfs {
        host = "localhost"
        port = "50070"
        writerPath = "/streaming/twitter/example"
        path = "/user/cloudera"
        user = "cloudera"
        password = "cloudera"
        writerFilePrefix = "streams-"
    }

You will need to change the Twitter keys to reflect the contents your personal token

Running:
--------

    java -cp target/twitter-gardenhose-dualwriter-0.1-SNAPSHOT.jar -Dconfig.file=src/main/resources/application.conf org.apache.streams.twitter.example.TwitterGardenhoseDualWriter

Verification:
-------------
**NOTE:** It may take some time for enough tweets to come through before the buffers get flushed to ElasticSearch

Once this example has run for long enough, you should see the index you specified filled with data and that same data
should be mirrored on your HDFS instance.

