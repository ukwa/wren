Wren
====

This is the Storm-based crawler engine.


Crawl Queues
------------

Rather than baking in a queue system, to ensure we have robust, persistent and monitorable queues we will re-use an off-the-shelf queueing system.  Currently we use RabbitMQ.

* Seeds (define scope?)
* Discovered (found but need to be scoped/dropped it seen recently)
* Fetching (queue of accepted URLs to be fetched)
* Delayed (Fetching URLs that have been backed-off)

SURT scope status is not persisted in this mode.

So, store scope in DB too?

Need 'last seen', 'last downloaded' in DB to decide whether to crawl and for deduplication.

Downstream queue(s):

* Downloaded - has been downloaded and the result of that need recording elsewhere.
* Index - ready for indexing into Solr?
* Verify? - Compare with archived screenshot.

Other things to record:

Location of requests, metadata/extracted URLs, screenshots etc.
Outcome of deciderules - URL and what happened.
(maybe a secondard DB of URLs keyed on referrer/URL discovered from?)



Running Wrender
===============

    $ mvn package

Wrender is a Storm-based screenshotter to augment an existing crawl.

http://storm.apache.org/documentation/flux.html

This seems to work okay for local jobs: ```-s -1```

    $ storm jar target/wren-0.0.1-SNAPSHOT.jar org.apache.storm.flux.Flux -s 3600000 --local src/main/resources/wrender.yaml
    
WrenBot is more like an actual crawler

    $ storm jar target/wren-0.0.1-SNAPSHOT.jar org.apache.storm.flux.Flux -s 3600000 --local src/main/resources/wrenbot.yaml
    
Or, on the Mac at least

    $ brew install zookeeper storm

Start zookeeper

     $ storm nimbus &
     $ storm supervisor &
     $ storm ui &
     
 Then on 8080 you get the UI, and then you can use ```--remote`` mode instead.
 
    

