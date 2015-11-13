Wren
====

An experiment aimed at building a scaleable, modular web archive system based on [Docker Compose](https://docs.docker.com/compose/) and [Apache Storm](http://storm.apache.org/).

Freely lifting useful ideas from:

* [storm-crawler](https://github.com/DigitalPebble/storm-crawler).
* [Brozzler](https://github.com/nlevitt/brozzler), an IA's distributed browser-based web crawler build on Docker which works along similar lines.
* [Browsertrix](https://github.com/ikreymer/browsertrix), which is currently more of a render assistant than a crawler, but leverages Docker Compose.
* Various Dockerised OpenWayback images, [LOCKSS](https://hub.docker.com/r/lockss/openwayback/), [UNB Libraries](https://github.com/unb-libraries/docker-openwayback), [Sawood Alam](https://github.com/ibnesayeed/docker-wayback).


Elastic Web Rendering
---------------------

Wren is a prototype replacement for our suite of Python-based scripts that render URLs that are part of a Heritrix crawl in order to determine the URLs of dynamically transcluded dependencies.

Compared to the original implementation, the goals are:

- Fewer moving parts (less to maintain)
- Based on a scalable parallel processing framework (manually scaling is hard)
- Robust, guaranteed processing of requests (won't drop URLs by accident)

It is also an experiment in building a more modular web crawling system.

Robust Crawl Launching
----------------------

We also need to reliably launch our regular crawls. The current system relies on a script ([w3start.py](https://github.com/ukwa/python-w3act/blob/master/w3start.py)) that is launched by and hourly cron job. However, if something goes wrong during the launch process, the system cannot retry. A better option is to use the cron job only to place the crawl request on a queue, and use a daemon process to watch that queue and launch the script.

One option is to create a normal server daemon process. We've tended to do this in the past, but this has led to various important services being spread over a number of machines. This makes the dependencies difficult to manage and the processing difficult to monitor.

Using Storm would allow us to centralise these daemons and integrate them into our overall monitoring approach. They would also retry robustly and be less dependent on specific hardware systems.

Scale-out Archiving Web Proxy
-----------------------------

- The [warcprox](https://github.com/internetarchive/warcprox) Dockerfile sets up warcprox on Ubuntu 14.04/Python 3.4 with the necessary dependencies.
- The [Squid](http://www.squid-cache.org/) caching forward proxy is used to set up a [Cache Hierarchy](http://wiki.squid-cache.org/Features/CacheHierarchy), but instead of caching the results, the 'parent' proxies can be instances of warcprox.
- This should allow proxy-based web archiving to be used on large scale crawls.
- Note that it may be possible to use the caching feature of Squid to avoid hitting the original site too often when extracting transcluded URLs.
- HAProxy in HTTP mode can redirect based on ```hdr(host)```, ```uri```, etc. (but not in TCP mode).

### Scaling out with Docker ###

To experiment with scaling out, first clean out any existing machines:

    $ docker-compose rm

Then define how many warcprox instances you want and ask for them to be configured:

    $ docker-compose scale warcprox=3

Then when you run 

    $ docker-compose up

The system will start up and configure a HAProxy instance that is configured to balance the load across all the warcprox instances. The provided configuration divides the load up using ```hdr(host)```, which send all requests relating to a particular host to the same warcprox instance. This ensures that URL-based de-duplication can work effectively. Further experimentation with the load balancing parameters is recommended.


### TO DO ###

- Use a shared [data volume container](https://docs.docker.com/userguide/dockervolumes/#creating-and-mounting-a-data-volume-container) to hold the WARCs.


CDX/Remote Resource Index Servers
---------------------------------

- Various web archiving components may benefit from having the [CDX index](https://archive.org/web/researcher/cdx_file_format.php) as an independent, scaleable service rather than the usual files.
- If the CDX server also present an API for updating its index, as well as reading it, it can act as a core, standalone component in a modular architecture.
- Potential uses include: playback, de-duplication, 'last seen' state during crawls.
- The [tinycdxserver](https://github.com/nla/tinycdxserver) Dockerfile sets up NLA's read/writable Remote Resource Index server (based on RocksDB) for experimentation.
- The read-only CDX servers ([pywb](https://github.com/ikreymer/pywb/wiki/CDX-Server-API),[OpenWayback](https://github.com/iipc/openwayback/tree/master/wayback-cdx-server-webapp)), could be unified and extended in this direction.
- Note that [warcbase](http://warcbase.org/) and [OpenWayback](https://github.com/iipc/openwayback) can be [used together](https://github.com/lintool/warcbase#waybackwarcbase-integration) for very large indexes that are best stored in HBase.



