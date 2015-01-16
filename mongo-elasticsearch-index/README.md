Apache Streams (incubating)
Licensed under Apache License 2.0 - http://www.apache.org/licenses/LICENSE-2.0
--------------------------------------------------------------------------------

mongo-elasticsearch-index
==============================

Requirements:
-------------
 - A running MongoDB 2.4+ instance
 - A running ElasticSearch 1.0.0+ instance

Description:
------------
Copies documents from mongo to elasticsearch

Specification:
-----------------

[MongoElasticsearchIndex.dot](src/main/resources/MongoElasticsearchIndex.dot "MongoElasticsearchIndex.dot" )

Diagram:
-----------------

![MongoElasticsearchIndex.png](./MongoElasticsearchIndex.png?raw=true)

Example Configuration:
----------------------

    {
        "reindex": {
            "source": {
                "mongo": {
                    "host": "localhost",
                    "port": 27017,
                    "db": "streamsdb",
                    "collection": "streams"
                }
            },
            "destination": {
                "hosts": [
                    "localhost"
                ],
                "port": 9300,
                "clusterName": "elasticsearch",
                "index": "brand-reindex-range_twitteractivity",
                "type": "twitteractivity"
            }
        }
    }

Running:
--------

    java -cp target/mongo-elasticsearch-index-0.1-SNAPSHOT.jar -Dconfig.file=src/main/resources/application.json org.apache.streams.elasticsearch.example.MongoElasticsearchIndex

