datasift-console-elasticsearch
==============================

Requirements:
-------------
 - An active datasift account
 - At least one active datasift stream
 - A running ElasticSearch 1.0.0+ instance

Description:
------------
Reads json lines piped in via console and write them to elasticsearch.

This is useful if you want to perform a one-off data capture task and load the result to an index.

Example Configuration:
----------------------

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

    cat json_export.txt | java -cp datasift-console-elasticsearch-0.1-SNAPSHOT.jar -Dconfig.file=application.conf org.apache.streams.datasift.example.DatasiftConsoleElasticsearch

Verification:
-------------
You should see documents being written to elasticsearch.