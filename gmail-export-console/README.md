Apache Streams (incubating)
Licensed under Apache License 2.0 - http://www.apache.org/licenses/LICENSE-2.0
--------------------------------------------------------------------------------

gmail-export-standalone
==============================

Requirements:
-------------
 - A GMail account

Description:
------------
Pulls down a stream of emails for a given GMail account

Specification:
-----------------

[FacebookUserstreamElasticsearch.dot](src/main/resources/FacebookUserstreamElasticsearch.dot "ElasticsearchReserialize.dot" )

Diagram:
-----------------

![FacebookUserstreamElasticsearch.png](./FacebookUserstreamElasticsearch.png?raw=true)

Example Configuration:
----------------------

    include "reference"
    gmail {
        username = ""
        password = ""
    }

You will need to include the username and password for the GMail account you would like to connect to

Running:
--------

    java -cp target/gmail-export-standalone-0.1-SNAPSHOT.jar -Dconfig.file=src/main/resources/application.conf org.apache.streams.gmail.example.GMailExportStandalone

Verification:
-------------
You should see your latest emails streaming into the console