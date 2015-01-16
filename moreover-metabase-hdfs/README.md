Apache Streams (incubating)
Licensed under Apache License 2.0 - http://www.apache.org/licenses/LICENSE-2.0
--------------------------------------------------------------------------------

moreover-metabase-hdfs
==============================

Requirements:
-------------
 - Authorized Moreover API credentials
 - A running hadoop cluster
 - A running HttpFs service

Description:
------------
Ingest Moreover metabase, write to HDFS.

Specification:
-----------------

[MoreoverMetabaseHdfs.dot](src/main/resources/MoreoverMetabaseHdfs.dot "MoreoverMetabaseHdfs.dot" )

Diagram:
-----------------

![MoreoverMetabaseHdfs.png](./MoreoverMetabaseHdfs.png?raw=true)

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

Running:
--------

    java -cp target/moreover-metabase-hdfs-0.1-SNAPSHOT.jar -Dconfig.file=src/main/resources/application.conf org.apache.streams.moreover.example.MoreoverMetabaseHdfs

Verification:
-------------
Open up Hue - http://localhost:8888/ and confirm that files are being written

