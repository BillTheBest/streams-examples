Apache Streams (incubating)
Licensed under Apache License 2.0 - http://www.apache.org/licenses/LICENSE-2.0
--------------------------------------------------------------------------------

# Perform setup steps to prepare #

[Setup instructions](https://github.com/w2ogroup/streams-examples/tree/master/SETUP.md "Setup")

# Switch to streamstutorial branch #

From home directory:

    cd incubator-streams
    git fetch
    git checkout streamstutorial
    mvn clean install -Dmaven.test.skip=true
    cd ../streams-examples
    git pull
    mvn clean package

# Setup and start elasticsearch

From home directory:

    cd elasticsearch-1.1.1
    nano config/elasticsearch.yml
    
Edit the # cluster.name line, removing # and selecting a unique name
        
    bin/elasticsearch &
    
# Run the twitter history example #

[Twitter History README](https://github.com/w2ogroup/streams-examples/tree/master/twitter-history-elasticsearch "Twitter History")

# Run the userstream example #

[Userstream README](https://github.com/w2ogroup/streams-examples/tree/master/twitter-userstream-elasticsearch "User Stream")

# Run the gardenhose example #

[Gardenhose README](https://github.com/w2ogroup/streams-examples/tree/master/twitter-gardenhose-elasticsearch "Gardenhose")
