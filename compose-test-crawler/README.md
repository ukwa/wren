UKWA Test Crawler
-----------------

This is a Dockerised version of the UKWA Heritrix3 crawl system. It runs:

* Heritrix3 as the main crawler.
* ClamAV clamd for virus scanning during the crawl.
* RabbitMQ
* The webrender system, composed of the HAR Daemon and the PhantomJS web renderer, used to capture screenshots etc. and pass transcluded URLs back to Heritrix3.
* Monitrix (based on ELK, but where Logstash understand Heritrix3's crawl.log format).
* OpenWayback (but useless at present as nothing is generating the CDX)


### TODO ###

- Make the ukwa-heritrix filebeat (i.e. coupling to Monitrix) optional so we don't have to run everything all the time.
- Add W3ACT, where users can decide what should be crawled.
- Add python-w3act, using the data in W3ACT to drive Heritrix
    - Also add a test-engine mode that orchestrates crawl execution?
