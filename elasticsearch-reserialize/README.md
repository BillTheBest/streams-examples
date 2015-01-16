Apache Streams (incubating)
Licensed under Apache License 2.0 - http://www.apache.org/licenses/LICENSE-2.0
--------------------------------------------------------------------------------

elasticsearch-reserialize
==============================

Requirements:
-------------
 - A running ElasticSearch 1.0.0+ instance

Description:
------------

Reindexes activities (to a new index or in-place) and re-applies the appropriate serializer.

This is useful when the serializer has been updated to a new version and your index contains documents build with an older version.

Specification:
-----------------

[ElasticsearchReserialize.dot](src/main/resources/ElasticsearchReserialize.dot "ElasticsearchReserialize.dot" )

Diagram:
-----------------

![ElasticsearchReserialize.png](./ElasticsearchReserialize.png?raw=true)

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

    java -cp elasticsearch-reserialize-0.1-SNAPSHOT.jar -Dconfig.file=application.conf org.apache.streams.elasticsearch.example.ElasticsearchReserialize

