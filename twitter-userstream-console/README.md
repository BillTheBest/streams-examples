twitter-userstream-console
==============================

Requirements:
-------------
 - Authorized Twitter API credentials

Description:
------------
This example connects to an active twitter account and displays the userstream

Specification:
-----------------

[TwitterUserstreamConsole.dot](src/main/resources/TwitterUserstreamConsole.dot "TwitterUserstreamConsole.dot" )

Diagram:
-----------------

![TwitterUserstreamConsole.png](./TwitterUserstreamConsole.png?raw=true)

Example Configuration:
----------------------

    twitter {
        host = "api.twitter.com"
        endpoint = "userstream"
        oauth {
            consumerKey = "bcg14JThZEGoZ3MZOoT2HnJS7"
            consumerSecret = "S4dwxnZni58CIJaoupGnUrO4HRHmbBGOb28W6IqOJBx36LPw2z"
            accessToken = ""
            accessTokenSecret = ""
        }
    }

Running:
--------

Once the configuration file has been completed this example can be run with:

    java -cp target/twitter-userstream-console-0.1-SNAPSHOT.jar -Dconfig.file=src/main/resources/application.conf org.apache.streams.twitter.example.TwitterUserstreamConsole

Verification:
-------------
You should see activity documents presented immediately in the console.