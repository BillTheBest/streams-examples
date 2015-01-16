Apache Streams (incubating)
Licensed under Apache License 2.0 - http://www.apache.org/licenses/LICENSE-2.0
--------------------------------------------------------------------------------

# Download the tutorial VM #

Start downloading the VM here: https://dl.dropboxusercontent.com/u/9422012/Apache%20Streams%20Tutorial.zip

# Create a twitter account if you don't have one #

Do you tweet?  And know your twitter password?  If so skip this step.

# Find out your twitter numeric ID #

You can find this out at http://mytwitterid.com/

You have to tweet at least once to be assigned a twitterid!

Why not tell the twitterverse something interesting you've learned, or someone interesting you've met at the conference?

# Pick an account to analyze #

If you haven't tweeted very often, or don't follow many people, it's OK!

Make a short list of accounts you think might be interesting, and go find their numeric IDs.

You can analyze the tweets and retweets of @bigdataconf by using the list below.

    follow = [
        969542076
    ]

You can have multiple accounts in the array - delimited by new-lines, no commas necessary.

Place this in a text file on your Desktop for future reference.

# Create a developer account: Set yourself up a developer account on Twitter #

You need to visit the official Twitter developer site and register for
a developer account. This is a free and necessary step to make
requests for the v1.1 API.

https://dev.twitter.com/

# Create an application: Create an application on the Twitter developer site #

Click the "Create Application" button.

So, the point of creating an application is to give yourself (and
Twitter) a set of keys. These are:

The consumer key
The consumer secret
The access token
The access token secret

There's a little bit of information here on what these tokens for.

# Create access tokens: You'll need these to make successful requests #

OAuth requests a few tokens. So you need to have them generated for you.

Click "create my access token" at the bottom. Then once you scroll to
the bottom again, you'll have some newly generated keys.

# Create a json snippet in a local text editor, using these keys #

    oauth {
            consumerKey = ""
            consumerSecret = ""
            accessToken = ""
            accessTokenSecret = ""
    }

Place this in a text file on your Desktop for future reference.

You will need to paste it into your configuration files to run your streams.

# Prepare an authorized credentials test #

Set Request URI: to the suggested value
https://api.twitter.com/1.1/statuses/home_timeline.json

Click 'See OAuth signature for this request'

The cURL command is the final check that everything is good to go - copy and paste it to a text file on your desktop.

By now your VM should be ready to set-up, so let's finish us those steps, and then we'll execute the curl command in the VM.

# Set up the tutorial VM in virtualbox #

In Preferences, set up a new NAT network
Click port forwarding
  Create forwarding rules for SSH and Elasticsearch

# Start the tutorial VM in virtualbox #

Configure the VM for bridged networking on an active connection

log in as root : streamstutorial

# Confirm that you have an ip address #

'ifconfig' and look for inet address on eth0

if you don't see one:

service network restart
ifup eth0

# Confirm that you have a working internet connection #

ping google.com
should not timeout

curl -X GET https://api.twitter.com
should output a web page

# Open an ssh session to the virtual machine #

ssh root@127.0.0.1 -p 2222 

this will allow you to copy/paste into the terminal

# Final readiness check

Execute the cURL command from before.

If all is well, you will get a response from twitter containing tweets.

# One last thing... Download this file #

https://raw.githubusercontent.com/w2ogroup/streams-examples/master/twitter-history-elasticsearch/src/main/resources/reports/ActivityReport.json


