Apache Streams (incubating)
Licensed under Apache License 2.0 - http://www.apache.org/licenses/LICENSE-2.0
--------------------------------------------------------------------------------

twitter-profiles-hbase
==============================

Requirements:
-------------
 - Authorized Twitter API credentials
 - A running hadoop cluster
 - A running hbase service

Description:
------------
Stores current user profiles of everyone mentioning any of a list of keywords in mongo.

Specification:
-----------------

[TwitterDirectoryHbase.dot](src/main/resources/TwitterDirectoryHbase.dot "TwitterGardenhoseElasticsearch.dot" )

Diagram:
-----------------

![TwitterDirectoryHbase.png](./TwitterDirectoryHbase.png?raw=true)

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
    zookeeper {
        znode {
            parent = "/hbase"
            rootserver = "cloudera-vm-local"
        }
    }
    hbase {
        rootdir = "hdfs://cloudera-vm-local:8020/hbase"
        zookeeper {
            quorum = "cloudera-vm-local"
            property {
                clientPort = 2181
            }
        }
        table = "test_table"
        family = "test_family"
        qualifier = "test_column"
    }

Running:
--------

    java -cp target/twitter-profiles-hbase-0.1-SNAPSHOT.jar -Dconfig.file=src/main/resources/application.conf org.apache.streams.twitter.example.TwitterDirectoryHbase

Verification:
-------------
