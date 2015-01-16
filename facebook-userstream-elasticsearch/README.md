Apache Streams (incubating)
Licensed under Apache License 2.0 - http://www.apache.org/licenses/LICENSE-2.0
--------------------------------------------------------------------------------

facebook-history-elasticsearch
==============================

Requirements:
-------------
 - Authorized Facebook API credentials
 - A running ElasticSearch 1.0.0+ instance

Description:
------------
Polls an authorized user's feed and stores new activities in elasticsearch.

Specification:
-----------------

[FacebookUserstreamElasticsearch.dot](src/main/resources/FacebookUserstreamElasticsearch.dot "ElasticsearchReserialize.dot" )

Diagram:
-----------------

![FacebookUserstreamElasticsearch.png](./FacebookUserstreamElasticsearch.png?raw=true)

Example Configuration:
----------------------

    facebook {
        oauth {
            appId = ""
            appSecret = ""
            accessToken = ""
        }
    }
    elasticsearch {
        hosts = [
            localhost
        ]
        port = 9300
        clusterName = elasticsearch
        index = facebook_userstream_activity
        type = activity
    }

Running:
--------

You will need to run `./install_templates.sh` in the resources folder in order to apply the templates to your ES cluster

Once the configuration file has been completed and the templates installed, this example can be run with:
`java -cp target/twitter-history-elasticsearch-0.1-SNAPSHOT.jar -Dconfig.file=src/main/resources/application.conf org.apache.streams.twitter.example.TwitterHistoryElasticsearchActivity`

Verification:
-------------
Open up http://localhost:9200/_plugin/head/ and confirm that all three indices now have data in them

Download https://github.com/w2ogroup/streams-examples/blob/master/twitter-history-elasticsearch/src/main/resources/reports/ActivityReport.json

Open up http://localhost:9200/_plugin/marvel and from the folder icon in the top right hand corner click
    Load -> Advanced -> Choose File and select the report you downloaded

The gear on the top-right allows you to change the report index

You should now see dashboards displaying metrics about your twitter activity