datasift-webhook
==============================

Requirements:
-------------
 - An active datasift account

This example collects interaction messages received in real-time and archives them in elasticsearch

Example Configuration:
----------------------

    template: Hello, %s!
    defaultName: datasift

    server:
      type: simple
      applicationContextPath: /
      adminContextPath: /admin
      connector:
        type: http
        port: 8000

    logging:
      level: DEBUG
      appenders:
        - type: console
          threshold: ALL
          target: stdout

    elasticsearch:
      hosts:
        - "localhost"
      port: 9300
      clusterName: elasticsearch
      index: datasift_webhook
      type: activity
      batchSize: 100

Example CSDL:
-------------

interaction.title contains_any "Apache"

Running:
--------

Create your stream from the Datasift console.

Launch the process on a server and port to which datasift's server can connect via TCP

Create a new Data Destination of type 'HTTP'

    URL: http://$host:$port/streams/webhooks/datasift
    Data format: JSON new line delimited

Create a new recording or historics query, connecting your stream to your destination

Verification:
-------------
Watch the posts pour in.

