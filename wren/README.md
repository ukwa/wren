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



