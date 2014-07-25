elasticsearch-reserialize
==============================

Requirements:
-------------
 - A running ElasticSearch 1.0.0+ instance
 - 'head' plugin for ElasticSearch (`elasticsearch/bin/plugin -install mobz/elasticsearch-head`)
 - 'marvel' plugin for ElasticSearch (`elasticsearch/bin/plugin -install mobz/elasticsearch-head`)

This stream reindexes activities (to a new index or in-place) and re-applies the appropriate serializer.

This is useful when the serializer has been updated to a new version and your index contains documents build with an older version.

Example Configuration:
----------------------

reindex {
    source {
        hosts = [
            localhost
        ]
        port = 9300
        clusterName = elasticsearch
        indexes = [
            source_activity
        ]
        types = [
            activity
        ]
    }
    destination {
        hosts = [
            localhost
        ]
        port = 9300
        clusterName = elasticsearch
        index = destination_activity
        type = activity
    }
}

Running:
--------

    java -cp elasticsearch-reserialize-0.1-SNAPSHOT.jar -Dconfig.file=application.conf org.apache.streams.elastisearch.example.ElasticsearchReserialize

