sysomos-mongo
==============================

Requirements:
-------------
 - Authorized Sysomos API credentials
 - A running MongoDB 2.4+ instance

Description:
------------
Collects posts from heartbeat API, converts them to activities, and writes them to mongo

Configuration:
--------------
    include "reference"
    sysomos {
        heartbeatIds = [
            HBID
        ]
        apiBatchSize = 500
        apiKey = KEY
        minDelayMs = 10000
        scheduledDelayMs = 120000
        maxBatchSize = 10000
    }
    mongo {
        host = localhost
        port = 27017
        db = streamsdb
        collection = streams
    }

Running:
--------

    java -cp target/sysomos-mongo-0.1-SNAPSHOT.jar -Dconfig.file=src/main/resources/application.conf org.apache.streams.sysomos.example.SysomosMongo

Verification:
-------------
**NOTE:** It may take some time for enough tweets to come through before the buffers get flushed to ElasticSearch

Once this example has run for long enough, you should see the index you specified filled with data and that same data
should be mirrored on your HDFS instance.

