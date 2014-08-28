datasift-instagram-elasticsearch
==============================

Requirements:
-------------
 - An active datasift account
 - An active datasift managed source
 - A running ElasticSearch 1.0.0+ instance

Description:
------------
Connects to an active datasift stream, applies an Instagram serializer, and writes activities to elasticsearch.

Specification:
-----------------

[DatasiftInstagramElasticsearch.dot](src/main/resources/DatasiftInstagramElasticsearch.dot "DatasiftInstagramElasticsearch.dot" )

Diagram:
-----------------

![DatasiftInstagramElasticsearch.png](./DatasiftInstagramElasticsearch.png?raw=true)

Example Configuration:
----------------------

    datasift {
        apiKey = ""
        userName = ""
        streamHash = [
            03ab029f120f989bf75b0b9b8f118467   
        ]
    }
    elasticsearch {
        hosts = [
            localhost
        ]
        port = 9300
        clusterName = elasticsearch
        index = index_activity
        type = activity
    }

Running:
--------

    java -cp datasift-instagram-elasticsearch-0.1-SNAPSHOT.jar -Dconfig.file=application.conf org.apache.streams.datasift.example.DatasiftInstagramElasticsearch

Verification:
-------------
You should see documents being written to elasticsearch