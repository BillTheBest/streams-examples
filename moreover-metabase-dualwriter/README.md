Apache Streams (incubating)
Licensed under Apache License 2.0 - http://www.apache.org/licenses/LICENSE-2.0
--------------------------------------------------------------------------------

moreover-metabase-dualwriter
==============================

Requirements:
-------------
 - Authorized Moreover API credentials
 - A running ElasticSearch 1.0.0+ instance
 - A running hadoop cluster
 - A running HttpFs service

Description:
------------
Ingest Moreover metabase, write to Elasticsearch and HDFS.

Specification:
-----------------

[MoreoverMetabaseDualWriter.dot](src/main/resources/MoreoverMetabaseDualWriter.dot "MoreoverMetabaseDualWriter.dot" )

Diagram:
-----------------

![MoreoverMetabaseDualWriter.png](./MoreoverMetabaseDualWriter.png?raw=true)

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
    hdfs {
        host = "localhost"
        port = "50070"
        writerPath = "/history/moreover/metabase"
        path = "/user/cloudera"
        user = "cloudera"
        password = "cloudera"
        writerFilePrefix = "streams-"
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

    java -cp target/moreover-metabase-dualwriter-0.1-SNAPSHOT.jar -Dconfig.file=src/main/resources/application.conf org.apache.streams.moreover.example.MoreoverMetabaseDualWriter

Verification:
-------------

Open up Hue - http://localhost:8888/ and confirm that files are being written

Open up http://localhost:9200/_plugin/head/ and confirm that documents are being written.
