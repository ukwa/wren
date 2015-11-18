Wren
====

An experiment aimed at building a scaleable, modular web archive system based on [Docker Compose](https://docs.docker.com/compose/) and [Apache Storm](http://storm.apache.org/).

To make any progress, we need to be able to effectively compare any new crawler with our current system. Therefore, we start by reproducing our existing crawl system via Docker Compose, and check we fully understand it before attempting to make any modifications. We will then look at ways of modifying, replacing or removing our current components in order to make the whole system more maintainable, manageable and scalable.

Our goals are:

- Fewer moving parts (less to maintain)
- Based on a scalable parallel processing framework (manually scaling is hard)
- Robust, guaranteed processing of requests (won't drop URLs by accident)

Freely lifting useful ideas from:

* [storm-crawler](https://github.com/DigitalPebble/storm-crawler).
* [Brozzler](https://github.com/nlevitt/brozzler), an IA's distributed browser-based web crawler build on Docker which works along similar lines.
* [Browsertrix](https://github.com/ikreymer/browsertrix), which is currently more of a render assistant than a crawler, but leverages Docker Compose.
* Various Dockerised OpenWayback images, [LOCKSS](https://hub.docker.com/r/lockss/openwayback/), [UNB Libraries](https://github.com/unb-libraries/docker-openwayback), [Sawood Alam](https://github.com/ibnesayeed/docker-wayback).

### Folder structure ###

Most of the folders in this repository are distinct Dockerized services. The folders beginning with ```compose-``` contain ```docker-compose.yml``` files that assemble these individual services into larger, integrated systems.

* Compositions
    * [UKWA Heritrix3 Test Crawl System](./compose-test-crawler/)
    * [Scale-out Archiving Proxy](./compose-warcprox/)

Where the services are under active development, the service folder is a ```git``` ```submodule```, pulling in the original repository and building it directly inside this parent project. This makes integrated development and testing much easier. However, if you clone this repository, you'll probably want to do so recursively, like this:

    $ git clone --recursive git@github.com:anjackson/wren.git

This will go and pull down all the ```submodules``` at the same time as the original clone.

As individual services stabilize, it should be possible to remove these submodules and run the Docker images instead.


Wren Storm Topologies
---------------------

We are evaluating whether Apache Storm provides a useful framework for modularizing and scaling the core crawl process itself. In particular, the way the framework provides guaranteed message processing (e.g. at-least-once semantics) should help ensure the integrity of the system.

### Elastic Web Rendering ###

Wren includes a prototype replacement for our suite of Python-based scripts that render URLs that are part of a Heritrix crawl in order to determine the URLs of dynamically transcluded dependencies.

### Robust Crawl Launching ###

We also need to reliably launch our regular crawls. The current system relies on a script ([w3start.py](https://github.com/ukwa/python-w3act/blob/master/w3start.py)) that is launched by and hourly cron job. However, if something goes wrong during the launch process, the system cannot retry. A better option is to use the cron job only to place the crawl request on a queue, and use a daemon process to watch that queue and launch the script.

One option is to create a normal server daemon process. We've tended to do this in the past, but this has led to various important services being spread over a number of machines. This makes the dependencies difficult to manage and the processing difficult to monitor.

Using Storm would allow us to centralize these daemons and integrate them into our overall monitoring approach. They would also retry robustly and be less dependent on specific hardware systems.


CDX/Remote Resource Index Servers
---------------------------------

- Various web archiving components may benefit from having the [CDX index](https://archive.org/web/researcher/cdx_file_format.php) as an independent, scaleable service rather than the usual files.
- If the CDX server also present an API for updating its index, as well as reading it, it can act as a core, standalone component in a modular architecture.
- Potential uses include: playback, de-duplication, 'last seen' state during crawls.
- The [tinycdxserver](https://github.com/nla/tinycdxserver) Dockerfile sets up NLA's read/writable Remote Resource Index server (based on RocksDB) for experimentation. See <https://gist.github.com/ato/b2ad8e65b35afe690921> for information on using it.
- The read-only CDX servers ([pywb](https://github.com/ikreymer/pywb/wiki/CDX-Server-API),[OpenWayback](https://github.com/iipc/openwayback/tree/master/wayback-cdx-server-webapp)), could be unified and extended in this direction.
- Note that [warcbase](http://warcbase.org/) and [OpenWayback](https://github.com/iipc/openwayback) can be [used together](https://github.com/lintool/warcbase#waybackwarcbase-integration) for very large indexes that are best stored in HBase.

Remote Browsers
---------------

* [Netcapsule](https://github.com/ikreymer/netcapsule)
* [guacamole-docker](http://guac-dev.org/doc/gug/guacamole-docker.html)

End-to-End Testing
------------------

* [Use Splinter?](https://splinter.readthedocs.org/en/latest/why.html)

