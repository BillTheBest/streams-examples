elasticsearch-backup
==============================

Requirements:
-------------
 - A running ElasticSearch 1.0.0+ instance
 - A running hadoop cluster
 - A running HttpFs service

Description:
------------
Copies documents from elasticsearch to HDFS.  Inverse of elasticsearch-restore.

Example Configuration:
----------------------

{
    "backup": {
        "source": {
            "hosts": [
                "localhost"
            ],
            "port": 9300,
            "clusterName": "elasticsearch",
            "indexes": [
                "example_activity"
            ],
            "types": [
                "activity"
            ]
        },
        "destination": {
            "host": "localhost",
            "port": 50070,
            "path": "/user/cloudera",
            "user": "cloudera",
            "pass": "cloudera",
            "writerPath": "example_activity"
        }
    }
}

Running:
--------

`java -cp target/elasticsearch-backup-0.1-SNAPSHOT.jar -Dconfig.file=src/main/resources/application.json org.apache.streams.twitter.example.TwitterHistoryElasticsearch{Tweet|Retweet|Activity}`

