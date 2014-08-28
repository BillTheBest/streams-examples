instagram-recentmedia-console
==============================

Requirements:
-------------
 - Authorized Instagram API credentials

Description:
------------
Retrieves new posts from a known list of users and writes to console.

Example Configuration:
----------------------

    instagram {
        "clientId": "",
        "clientSecret": "",
        "callbackUrl": "",
        "userIds": [

        ]
    }

Running:
--------

    java -cp target/datasift-streaming-console-0.1-SNAPSHOT.jar -Dconfig.file=src/main/resources/application.conf org.apache.streams.datasift.example.DatasiftStreamingConsole

Verification:
-------------
You should see activity documents presented immediately in the console.