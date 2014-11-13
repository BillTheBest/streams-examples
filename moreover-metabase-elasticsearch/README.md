moreover-metabase-elasticsearch
==============================

Requirements:
-------------
 - Authorized Moreover API credentials
 - A running ElasticSearch 1.0.0+ instance

Description:
------------
Ingest Moreover metabase, write to Elasticsearch.

Specification:
-----------------

[MoreoverMetabaseElasticsearch.dot](src/main/resources/MoreoverMetabaseElasticsearch.dot "MoreoverMetabaseElasticsearch.dot" )

Diagram:
-----------------

![MoreoverMetabaseElasticsearch.png](./MoreoverMetabaseElasticsearch.png?raw=true)

Example Configuration:
----------------------

    moreover {
        apiKeys {
            key1 {
                key = ""
                startingSequence = ""
            }
        }
    }

    elasticsearch {
        hosts = [
            localhost
        ]
        port = 9300
        clusterName = elasticsearch
        index = moreover
        type = moreover
    }

Running:
--------

You will need to run `./install_templates.sh` in the resources folder in order to apply the templates to your ES cluster

Once the configuration file has been completed and the templates installed, this example can be run with:

    java -cp target/moreover-metabase-elasticsearch-0.1-SNAPSHOT.jar -Dconfig.file=src/main/resources/application.conf org.apache.streams.moreover.example.MoreoverMetabaseElasticsearch

Verification:
-------------
Open up http://localhost:9200/_plugin/head/ and confirm that documents are being written.

