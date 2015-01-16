Apache Streams (incubating)
Licensed under Apache License 2.0 - http://www.apache.org/licenses/LICENSE-2.0
--------------------------------------------------------------------------------

twitter-userstream-elasticsearch
==============================

Requirements:
-------------
 - Authorized Twitter API credentials
 - A running ElasticSearch 1.0.0+ instance
 - 'head' plugin for ElasticSearch (`elasticsearch/bin/plugin -install mobz/elasticsearch-head`)
 - 'marvel' plugin for ElasticSearch (`elasticsearch/bin/plugin -install elasticsearch/marvel/latest`)

Description:
------------
This example connects to an active twitter account and stores the userstream as activities in Elasticsearch

Specification:
-----------------

[TwitterUserstreamElasticsearch.dot](src/main/resources/TwitterUserstreamElasticsearch.dot "TwitterUserstreamElasticsearch.dot" )

Diagram:
-----------------

![TwitterUserstreamElasticsearch.png](./TwitterUserstreamElasticsearch.png?raw=true)

Example Configuration:
----------------------

    twitter {
        endpoint = "userstream"
        oauth {
                consumerKey = "bcg14JThZEGoZ3MZOoT2HnJS7"
                consumerSecret = "S4dwxnZni58CIJaoupGnUrO4HRHmbBGOb28W6IqOJBx36LPw2z"
                accessToken = ""
                accessTokenSecret = ""
        }
    }
    elasticsearch {
        hosts = [
            localhost
        ]
        port = 9300
        clusterName = elasticsearch
        index = userstream_activity
        type = activity
        batchSize = 1
    }

The consumerKey and consumerSecret are set for our streams-example application
The accessToken and accessTokenSecret can be obtained by navigating to:
 https://api.twitter.com/oauth/authenticate?oauth_token=UIJ0AUxCJatpKDUyFt0OTSEP4asZgqxRwUCT0AMSwc&oauth_callback=http%3A%2F%2Foauth.streamstutorial.w2odata.com%3A8080%2Fsocialauthdemo%2FsocialAuthSuccessAction.do

Running:
--------

Once the configuration file has been completed this example can be run with:

    java -cp target/twitter-userstream-elasticsearch-0.1-SNAPSHOT.jar -Dconfig.file=src/main/resources/application.conf org.apache.streams.twitter.example.TwitterUserstreamElasticsearch

Verification:
-------------
You should new posts and shares from your feed in elasticsearch