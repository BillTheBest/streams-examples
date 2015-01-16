Apache Streams (incubating)
Licensed under Apache License 2.0 - http://www.apache.org/licenses/LICENSE-2.0
--------------------------------------------------------------------------------

elasticsearch-directory
==============================

Requirements:
-------------
 - A running ElasticSearch 1.0.0+ instance
 - An index containing activities

Description:
------------
Scans an index of activities and creates a directory of all actors.

Useful if you want the ability to add and modify profile metadata apart from their activities.

Example Configuration:
----------------------

    include "reference"
reindex {
    source {
        hosts = [
            localhost
        ]
        port = 9300
        clusterName = elasticsearch
        indexes = [
            userhistory_activity
        ]
        types = [
            activity
        ]
    }
    destination {
        hosts = [
            localhost
        ]
        port = 9300
        clusterName = elasticsearch
        index = directory_activity
        type = activity
    }
}

Running:
--------

You will need to run `./install_templates.sh` in the resources folder in order to apply the templates to your ES cluster

Once the configuration file has been completed and the templates installed, this example can be run with:
`java -cp twitter-history-elasticsearch-0.1-SNAPSHOT.jar -Dconfig.file=userhistory.conf org.apache.streams.twitter.example.TwitterHistoryElasticsearch{Tweet|Retweet|Activity}`

**NOTE:** the class that you run will depend on what your configuration is set up for. If you set it up for tweets, then you would run:
org.apache.streams.twitter.example.TwitterHistoryElasticsearchTweet

Verification:
-------------
Open up http://localhost:9200/_plugin/head/ and confirm that all three indices now have data in them
Open up http://localhost:9200/_plugin/marvel and from the folder icon in the top right hand corner click
    Load -> Advanced -> Choose File and select either 'resources/reports/ActivityReport.json' or 'resources/reports/TweetReport.json'

You should now see dashboards displaying metrics about your tweets/activities
