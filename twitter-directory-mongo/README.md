twitter-directory-mongo
==============================

Requirements:
-------------
 - Authorized Twitter API credentials
 - A running mongo instance

Description:
------------
Stores current user profiles of everyone mentioning any of a list of keywords in mongo.

Specification:
-----------------

[TwitterDirectoryMongo.dot](src/main/resources/TwitterDirectoryMongo.dot "TwitterGardenhoseElasticsearch.dot" )

Diagram:
-----------------

![TwitterDirectoryMongo.png](./TwitterDirectoryMongo.png?raw=true)

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
    mongo {
        host = localhost
        port = 27017
        db = streamsdb
        collection = directory
    }

Running:
--------

    java -cp target/twitter-history-elasticsearch-0.1-SNAPSHOT.jar -Dconfig.file=src/main/resources/application.conf org.apache.streams.twitter.example.TwitterProfilesHbaseActivity

Verification:
-------------
