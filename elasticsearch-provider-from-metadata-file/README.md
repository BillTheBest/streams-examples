elasticsearch-provider-from-metadata-file
==============================

Requirements:
-------------
 - A running ElasticSearch 1.0.0+ instance
 - A file containing the metadata of documents to process

Example Configuration:
----------------------

{
    "reindex": {
        "source": {
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

Populate source and destination with cluster / index / type details

Running:
--------

`java -cp target/elasticsearch-reindex-0.1-SNAPSHOT.jar -Dconfig.file=src/main/resources/application.json org.apache.streams.twitter.example.TwitterHistoryElasticsearch{Tweet|Retweet|Activity}`

