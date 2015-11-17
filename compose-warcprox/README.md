Scale-out Archiving Web Proxy
-----------------------------

This project should allow proxy-based web archiving to be used on large scale crawls by scaling it out behind a proxying load balancer. The load balancer attempts to route based on the URL, so that the same URLs are always routed to the same warcprox instance, thus ensuring deduplication works as expected without having to share state between the archiving proxies.

- The [warcprox](https://github.com/internetarchive/warcprox) Dockerfile sets up warcprox on Ubuntu 14.04/Python 3.4 with the necessary dependencies.
- The [Squid](http://www.squid-cache.org/) caching forward proxy is used to set up a [Cache Hierarchy](http://wiki.squid-cache.org/Features/CacheHierarchy), but instead of caching the results, the 'parent' proxies can be instances of warcprox.
    - Note that it may be possible to use the caching feature of Squid to avoid hitting the original site too often when extracting transcluded URLs.
- [HAProxy](https://github.com/tutumcloud/haproxy) in HTTP mode can redirect based on ```hdr(host)```, ```uri```, etc. (but not in TCP mode).

### Scaling out with Docker ###

To experiment with scaling out, first clean out any existing machines:

    $ docker-compose rm

Then define how many warcprox instances you want and ask for them to be configured:

    $ docker-compose scale warcprox=3

Then when you run 

    $ docker-compose up

The system will start up and configure a HAProxy instance that is configured to balance the load across all the warcprox instances. The provided configuration divides the load up using ```hdr(host)```, which send all requests relating to a particular host to the same warcprox instance. This ensures that URL-based de-duplication can work effectively. Further experimentation with the load balancing parameters is recommended.


### TO DO ###

- The [Brozzler branch of warcprox](https://github.com/nlevitt/warcprox/tree/brozzler) has some useful features for the future.
- Use the download-started datetime for the WARC and add this as the ```[Memento-Datetime](https://github.com/mementoweb/timegate/wiki/HTTP-Response-Headers)``` to the response. Use that to indicate that the archiving should have worked, and then pass it along to another queue for checking later on. *ALTERNATIVELY* (in case of collisions etc.) use a time-based UUID or similar to be a ```WARC-Record-ID``` and add this in a separate ```Warcprox-WARC-Record-ID:``` header. That record ID can then be tracked, although this will require a new index rather than leveraging the CDX.
- Expose the playback interface too.
- Consider using an external CDX database engine, e.g. the ```tinycdxserver```.
