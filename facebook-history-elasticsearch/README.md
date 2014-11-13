facebook-history-elasticsearch
==============================

Requirements:
-------------
 - Authorized Facebook API credentials
 - A running ElasticSearch 1.0.0+ instance

Description:
------------
Retrieves as many posts from a known list of users as twitter API allows.

Specification:
-----------------

[FacebookHistoryElasticsearch.dot](src/main/resources/FacebookHistoryElasticsearch.dot "ElasticsearchReserialize.dot" )

Diagram:
-----------------

![FacebookHistoryElasticsearch.png](./FacebookHistoryElasticsearch.png?raw=true)

Example Configuration:
----------------------

    facebook {
        oauth {
            appId = ""
            appSecret = ""
            accessToken = ""
        }
        info = [
            42232950
        ]
    }
    elasticsearch {
        hosts = [
            localhost
        ]
        port = 9300
        clusterName = elasticsearch
        index = userhistory_activity
        type = activity
    }

In the Facebook section you should place all of your relevant authentication keys and whichever Facebook users and pages you're looking to follow

If your application is only allowed to access graph API v2, you'll only be able to retrieve public posts from users who have authorized your app.

If your application is allowed to access graph API v1, you'll be able to retrieve public posts from users as well.

Running:
--------

    java -cp target/twitter-history-elasticsearch-0.1-SNAPSHOT.jar -Dconfig.file=src/main/resources/application.conf org.apache.streams.twitter.example.TwitterHistoryElasticsearchActivity

Verification:
-------------

