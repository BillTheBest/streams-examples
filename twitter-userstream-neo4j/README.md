twitter-userstream-rexster
==============================

Requirements:
-------------
 - Authorized Twitter API credentials
 - A running rexster graph database

Description:
------------
This example connects to an active twitter account and builds a social graph database of observed activities

Specification:
-----------------

[TwitterUserstreamRexster.dot](src/main/resources/TwitterUserstreamRexster.dot "TwitterUserstreamRexster.dot" )

Diagram:
-----------------

![TwitterUserstreamRexster.png](./TwitterUserstreamRexster.png?raw=true)

Example Configuration:
----------------------

    twitter {
        endpoint = "userstream"
        oauth {
                consumerKey = "bcg14JThZEGoZ3MZOoT2HnJS7"
                consumerSecret = "S4dwxnZni58CIJaoupGnUrO4HRHmbBGOb28W6IqOJBx36LPw2z"
                accessToken = ""
                accessTokenSecret = ""
        }
    }
    blueprints {
        host = localhost
        port = 8182
        graph = emptygraph
        vertices = {
            verbs = [
                follow
            ]
            objectTypes = [
                page
            ]
        }
        edges = {
            verbs = [
                follow
                post
                share
                like
            ]
            objectTypes = [
                page
            ]
        }
    }

The consumerKey and consumerSecret are set for our streams-example application
The accessToken and accessTokenSecret can be obtained by navigating to:
 https://api.twitter.com/oauth/authenticate?oauth_token=UIJ0AUxCJatpKDUyFt0OTSEP4asZgqxRwUCT0AMSwc&oauth_callback=http%3A%2F%2Foauth.streamstutorial.w2odata.com%3A8080%2Fsocialauthdemo%2FsocialAuthSuccessAction.do

Running:
--------

Once the configuration file has been completed this example can be run with:

    java -cp target/twitter-userstream-rexster-0.1-SNAPSHOT.jar -Dconfig.file=src/main/resources/application.conf org.apache.streams.twitter.example.TwitterUserstreamRexster

Verification:
-------------
You should see your twitter account and users you are connected to in doghouse.