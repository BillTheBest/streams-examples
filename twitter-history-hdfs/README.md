Apache Streams (incubating)
Licensed under Apache License 2.0 - http://www.apache.org/licenses/LICENSE-2.0
--------------------------------------------------------------------------------

twitter-history-hdfs
==============================

Requirements:
-------------
 - Authorized Twitter API credentials
 - A running hadoop cluster
 - A running HttpFs service

Description:
------------
Retrieves as many posts from a known list of users as twitter API allows.

This example includes three classes: one for indexing tweets, one for indexing activities, and one for indexing retweets.
Each of these jars require a corresponding configuration file that defines both Twitter and ElasticSearch preferences

Specification:
-----------------

[TwitterHistoryHdfs.dot](src/main/resources/TwitterHistoryHdfs.dot "TwitterGardenhoseElasticsearch.dot" )

Diagram:
-----------------

![TwitterHistoryHdfs.png](./TwitterHistoryHdfs.png?raw=true)


Example Configuration:
----------------------

    twitter {
        host = "api.twitter.com"
        endpoint = "statuses/user_timeline"
        oauth {
            consumerKey = ""
            consumerSecret = ""
            accessToken = ""
            accessTokenSecret = ""
        }
        follow = [
            42232950
        ]
    }
    hdfs {
        host = "localhost"
        port = "50070"
        writerPath = "/history/twitter/example"
        path = "/user/cloudera"
        user = "cloudera"
        password = "cloudera"
        writerFilePrefix = "streams-"
    }

In the Twitter section you should place all of your relevant authentication keys and whichever Twitter IDs you're looking to follow
Twitter IDs can be converted from screennames at http://www.gettwitterid.com

Running:
--------

    java -cp target/twitter-history-elasticsearch-0.1-SNAPSHOT.jar -Dconfig.file=src/main/resources/application.conf org.apache.streams.twitter.example.TwitterHistoryHdfsActivity

Verification:
-------------
