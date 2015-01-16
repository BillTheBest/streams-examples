Apache Streams (incubating)
Licensed under Apache License 2.0 - http://www.apache.org/licenses/LICENSE-2.0
--------------------------------------------------------------------------------

elasticsearch-delete
==============================

Requirements:
-------------
 - A running ElasticSearch 1.0.0+ instance
 - An index containing data
 - A list of identifiers to delete in a local text file

Description:
------------
Deletes a list of items from specified index of specified type
 
Example Configuration:
----------------------

    {
        "elasticsearch": {
             "hosts": [
                 "localhost"
             ],
             "port": 9300,
             "clusterName": "elasticsearch",
             "index": "example_activity",
             "type": "activity"
        }
    }

Running:
--------

    cat ids_to_delete.txt | java -cp target/elasticsearch-delete-0.1-SNAPSHOT.jar -Dconfig.file=src/main/resources/application.json org.apache.streams.elasticsearch.example.ElasticsearchDelete

