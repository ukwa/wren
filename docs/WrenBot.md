# teacup (storm in a)

An experiment in building a large-scale crawler on [Storm](http://storm.apache.org/), extending [storm-crawler](https://github.com/DigitalPebble/storm-crawler).

Other names:

* [Brixham](https://en.wikipedia.org/wiki/Brixham_trawler))
* Tempest


Current version uses [Storm Flux](https://github.com/apache/storm/tree/master/external/flux) to describe the job topology, and hooks in RabbitMQ via [storm-rabbitmq](https://github.com/ppat/storm-rabbitmq) to act as the queueing part of the crawl frontier.

Notes:

* No filter for 'seen' URLs at present, so it will keep re-queuing and then re-crawling URLs.
   * [PatriciaTrie](https://commons.apache.org/proper/commons-collections/apidocs/org/apache/commons/collections4/trie/PatriciaTrie.html) seems like a good option.
   * Or reuse the 'forgetting' cache as used [here](https://github.com/DigitalPebble/storm-crawler/blob/22fa21509c97c6cc3e52ae238ad610ebe90b8477/core/src/main/java/com/digitalpebble/storm/crawler/bolt/SimpleFetcherBolt.java#L95).
* Needs an elegant implementation of the crawl-delay:
    * Current storm-crawler implements  [queues of waiting Fetch Items](https://github.com/DigitalPebble/storm-crawler/blob/20890f11a6ca02ce37c1d56134637191716428d3/core/src/main/java/com/digitalpebble/storm/crawler/bolt/FetcherBolt.java#L238), or a simpler [throttle mechanism here](https://github.com/DigitalPebble/storm-crawler/blob/20890f11a6ca02ce37c1d56134637191716428d3/core/src/main/java/com/digitalpebble/storm/crawler/bolt/FetcherBolt.java#L238).
    * H3 mixes this in the Frontier classes by using queue 'snoozing' to implement the delays.
    * I feel like there should be a simpler approach. Maybe a 'delay queue'?
    * Looks like RabbitMQ has [some built in support for delaying messages](https://www.rabbitmq.com/blog/2015/04/16/scheduling-messages-with-rabbitmq/), but I guess that's not quite the kind of delay we need?
    * Simplest technique would just be to add the 'rejected' message to the end of the queue (as new messages), so the fetchers just keep spinning through messages and re-queuing them until they find one they can work on. This would mean potentially quite a lot of queue churn, and I guess for very large crawls it might mean big delays before the system gets back to a particular URL? Worst-case crawl delay is very large.
    * I kinda like the idea of separate 'seeds', 'next' and 'hot' queues. Idea would be to put 'new' hosts on the 'next' queue, and only pull from there when the 'hot' queue starts cooling off. When 'next' cools off, we pull in a new seed, and then that host becomes 'hot'. Well, more specifically, the 'next' queue spout sits there blocked/sleeping, watching the queue level on the 'hot' queue, and only moving the 'next' message over to it when the level drops below some pre-defined value. But I guess that this has the weakness that a small number of hosts with many links could dominate the 'hot' queue and so we'd starve the fetchers.
    * So, how about 'next', 'delayed' and 'first'? We go through 'first' first, and those that need a crawl-delay over a certain amount are pushed into the delayed queue. No, still feels wrong.
    * Okay, so queues-per-group-of-domains. We have active.a, active.b, active.c and so on, where the a/b/c part is the first letter of the hash of the domain. Still rather messy.
    * RabbitMQ does support [message priority](https://www.rabbitmq.com/priority.html), which might be enough, but might also bake things a bit to close to one implementation.
    * Alternatively, the fetcher could defer the crawl-delay to the WARChiving web proxy. As long as the timeouts on the fetcher were set high enough, this would be fine, but it does assume that we'd be taking that approach.
    * Thread per domain, thread sleeps after ack, lotsa threads?
    * So, to reformulate the goal, we ideally want to try to ensure that hosts and nearby content (linked material with shorted hop paths) are close together in time. 
    * We also want to keep all fetcher threads busy at all times, so any scheduling mechanism should avoid starving worker threads.
    * We should ensure that the appropriate crawl-delay is maintained per host (or optionally, per IP).
    

Okay, consider having many Throttlers, that are distributed by host/queue-key, and that manage the slow release of messages.  Downstream we can have fetcher threads that are randomly distributed, which will make the load easier to manage (as the fetching, possibly including rendering, is the expensive part.

Filter (scope and uniqueness)
: Consume 'next' and 'fetch' queues, and strip out URLs already seen, and any that are out of scope. Emits URIs keyed on queue ID.

Throttle
: Distributed by queue-id key, then for each key, monitor when the last request was started, and defer to the 'delayed' queue if crawling is not due for that key. Otherwise, pass URI to the next stage. This is the only part that needs to be distributed by key, and may therefore hit load balancing issues.

Fetch or Render
: Actually pull down the URI, either raw or as a rendering process. If we are not using a WARC writing proxy, this step need to handle that as we can't pass large binaries through Storm unless we can store them in a common location (e.g. HDFS).

Parse
: Depending on the format, parse the payload and determine any outlinks. Pass them to the 'fetch' queue to be filtered. OR filter them first, but having the filter in two locations seems rather clumsy.





So, if we had one big queue of discovered URLs, that get passed (to the Filters and then) to the Throttlers. These throttlers make sure we have the appropriate crawl delay, and will stack up X unique URLs per domain and release them at the (current) crawl rate (Overflowing URLs are put on the 'delay' queue).

The Fetchers then download the items (and WARC them if that's the mode of operation we're under). Then downstream Bolts parse the payload (? locality/size ?) and then pass the links back to the 'fetch' queue.
