Wauldock
========

An experiment aimed at building scaleable, modular web archive components based on Docker containers.


Scale-out Archiving Web Proxy
-----------------------------

- The [warcprox](https://github.com/internetarchive/warcprox) Dockerfile sets up warcprox on Ubuntu 14.04/Python 3.4 with the necessary dependencies.
- The [Squid](http://www.squid-cache.org/) caching forward proxy is used to set up a [Cache Hierarchy](http://wiki.squid-cache.org/Features/CacheHierarchy), but instead of caching the results, the 'parent' proxies can be instances of warcprox.
- This should allow proxy-based web archiving to be used on large scale crawls.
- Note that it may be possible to use the caching feature of Squid to avoid hitting the original site too often when extracting transcluded URLs.

### TO DO ###

- Add a [docker-compose](https://docs.docker.com/compose/) file that spins up multiple warcprox engines behind SQUID and test it.
- Use a shared [data volume container](https://docs.docker.com/userguide/dockervolumes/#creating-and-mounting-a-data-volume-container) to hold the WARCs.


CDX/Remote Resource Index Servers
---------------------------------

- Various web archiving components may benefit from having the [CDX index](https://archive.org/web/researcher/cdx_file_format.php) as an independent, scaleable service rather than the usual files.
- If the CDX server also present an API for updating its index, as well as reading it, it can act as a core, standalone component in a modular architecture.
- Potential uses include: playback, de-duplication, 'last seen' state during crawls.
- The [tinycdxserver](https://github.com/nla/tinycdxserver) Dockerfile sets up NLA's read/writable Remote Resource Index server (based on RocksDB) for experimentation.
- The read-only CDX servers ([pywb](https://github.com/ikreymer/pywb/wiki/CDX-Server-API),[OpenWayback](https://github.com/iipc/openwayback/tree/master/wayback-cdx-server-webapp)), could be unified and extended in this direction.
- Note that [warcbase](http://warcbase.org/) and [OpenWayback](https://github.com/iipc/openwayback) can be [used together](https://github.com/lintool/warcbase#waybackwarcbase-integration) for very large indexes that are best stored in HBase.


See also
--------

* [Brozzler](https://github.com/nlevitt/brozzler), an experimental distributed browser-based web crawler build on Docker which works along similar lines.
*  Various Dockerised OpenWayback images, [LOCKSS](https://hub.docker.com/r/lockss/openwayback/), [UNB Libraries](https://github.com/unb-libraries/docker-openwayback), [Sawood Alam](https://github.com/ibnesayeed/docker-wayback).



