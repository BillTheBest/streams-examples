elasticsearch-query
==============================

Requirements:
-------------
 - A running ElasticSearch 1.0.0+ instance

Description:
------------
Scans a set of indexes and types for matching documents and writes their metadata to the console.

Useful if you want to find a set of documents for some other action (enrichment, deletion) but curate the list before executing that action.

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
                "directory_activity"
            ],
            "types": [
                "activity"
            ],
            "_search": {
                {
                    "query": {
                        "query_string": {
                          "query": "actor.extensions.followers:(+>=10000)"
                        }
                    }
                }
            }
        }
    }

Running:
--------

    java -cp target/elasticsearch-query-0.1-SNAPSHOT.jar -Dconfig.file=src/main/resources/application.json org.apache.streams.elasticsearch.example.ElasticsearchQuery > query_result.txt

