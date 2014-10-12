elasticsearch-delete-from-metadata-file
==============================

Requirements:
-------------
 - A running ElasticSearch 1.0.0+ instance
 - A file containing the metadata of documents to delete

Example Configuration:
----------------------

{
    "elasticsearch": {
        "hosts": [
            "localhost"
        ],
        "port": 9300,
        "clusterName": "elasticsearch",
        "indexes": [
            "brand_twitteractivity"
        ],
        "types": [
            "twitteractivity"
        ]
    }
}

Populate source and destination with cluster / index / type details

Running:
--------

`java -cp target/elasticsearch-reindex-0.1-SNAPSHOT.jar -Dconfig.file=src/main/resources/application.json org.apache.streams.twitter.example.TwitterHistoryElasticsearch{Tweet|Retweet|Activity}`

