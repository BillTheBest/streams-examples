elasticsearch-restore
==============================

Requirements:
-------------
 - A running ElasticSearch 1.0.0+ instance
 - A running hadoop cluster
 - A running HttpFs service

Description:
------------
Copies documents from HDFS to elasticsearch.  Inverse of elasticsearch-backup.

Example Configuration:
----------------------

    {
        "restore": {
            "source": {
                "host": "localhost",
                "port": 50070,
                "path": "/user/cloudera",
                "user": "cloudera",
                "pass": "cloudera",
                "readerPath": "example_activity"
            },
            "destination": {
                "hosts": [
                    "localhost"
                ],
                "port": 9300,
                "clusterName": "elasticsearch",
                "index": "example_activity",
                "type": "activity"
            }
        }
    }

Running:
--------

    java -cp target/elasticsearch-restore-0.1-SNAPSHOT.jar -Dconfig.file=src/main/resources/application.json org.apache.streams.elasticsearch.example.ElasticsearchRestore

