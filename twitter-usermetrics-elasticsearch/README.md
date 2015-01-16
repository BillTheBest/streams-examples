Apache Streams (incubating)
Licensed under Apache License 2.0 - http://www.apache.org/licenses/LICENSE-2.0
--------------------------------------------------------------------------------

twitter-usermetrics-elasticsearch
==============================

Requirements:
-------------
 - An active twitter account

This example connects to an active twitter account, opens a user-stream, and builds
a timeseries of the author bio details of all active followed users in elasticsearch.

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
        follow = [
                2189174101
        ]
    }

The consumerKey and consumerSecret are set for our streams-example application
The accessToken and accessTokenSecret can be obtained by navigating to:
 https://api.twitter.com/oauth/authenticate?oauth_token=UIJ0AUxCJatpKDUyFt0OTSEP4asZgqxRwUCT0AMSwc&oauth_callback=http%3A%2F%2Foauth.streamstutorial.w2odata.com%3A8080%2Fsocialauthdemo%2FsocialAuthSuccessAction.do

Running:
--------

Once the configuration file has been completed this example can be run with:
`java -cp target/twitter-usermetrics-elasticsearch-0.1-SNAPSHOT.jar -Dconfig.file=src/main/resources/application.conf org.apache.streams.twitter.example.TwitterUsermetricsElasticsearch`

Verification:
-------------
You should see the bios of each tweet indexed with @timestamp set