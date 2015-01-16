Apache Streams (incubating)
Licensed under Apache License 2.0 - http://www.apache.org/licenses/LICENSE-2.0
--------------------------------------------------------------------------------

datasift-streaming-console
==============================

Requirements:
-------------
 - An active datasift account
 - At least one active datasift stream

Description:
------------
This example connects to an active datasift stream and displays messages received in real-time

Specification:
-----------------

[DatasiftStreamingConsole.dot](src/main/resources/DatasiftStreamingConsole.dot "DatasiftStreamingConsole.dot" )

Diagram:
-----------------

![DatasiftStreamingConsole.png](./DatasiftStreamingConsole.png?raw=true)

Example Configuration:
----------------------

    datasift {
        apiKey = ""
        userName = ""
        streamHash = [
            03ab029f120f989bf75b0b9b8f118467   
        ]
    }

Example CSDL:
-------------

interaction.title contains_any "Apache"

Running:
--------

    java -cp target/datasift-streaming-console-0.1-SNAPSHOT.jar -Dconfig.file=src/main/resources/application.conf org.apache.streams.datasift.example.DatasiftStreamingConsole

Verification:
-------------
You should see messages matching the CSDL of any hash in your config file presented immediately in the console.