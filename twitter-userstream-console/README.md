twitter-userstream-console
==============================

Requirements:
-------------
 - Authorized Twitter API credentials

Description:
------------
This example connects to an active twitter account and displays the userstream

Example Configuration:
----------------------

    include "reference"
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

    java -cp target/twitter-userstream-standalone-0.1-SNAPSHOT.jar -Dconfig.file=src/main/resources/application.conf org.apache.streams.twitter.example.TwitterUserstreamConsole

Verification:
-------------
