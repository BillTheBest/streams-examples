elasticsearch-reindex
==============================

Requirements:
-------------
 - A running ElasticSearch 1.0.0+ instance
 - 'head' plugin for ElasticSearch (`elasticsearch/bin/plugin -install mobz/elasticsearch-head`)
 - 'marvel' plugin for ElasticSearch (`elasticsearch/bin/plugin -install mobz/elasticsearch-head`)

This example includes three separate jars: one for indexing tweets, one for indexing activities, and one for indexing retweets.
Each of these jars require a corresponding configuration file that defines both Twitter and ElasticSearch preferences

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
            userhistory_tweet
        ]
        types = [
            tweet
        ]
    }
    destination {
        hosts = [
            localhost
        ]
        port = 9300
        clusterName = elasticsearch
        index = userhistory_reindex_tweet
        type = tweet
    }
}
In the Twitter section you should place all of your relevant authentication keys and whichever Twitter IDs you're looking to follow
Twitter IDs can be converted from screennames at http://www.gettwitterid.com

In the ElasticSearch section, you should put in all necessary configuration information. For each of these jars, you will need
to change the 'index' and 'type' attributes to whatever is appropriate. For example, if you were running the tweet jar the index attribute
would be 'userhistory_tweet' and the type attribute would be 'tweet'

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
