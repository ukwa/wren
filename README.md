# Wren

An experiment in building large-scale web-rendering crawl tools. Based on [Storm](http://storm.apache.org/), and [storm-crawler](https://github.com/DigitalPebble/storm-crawler).

## Elastic Web Rendering

Wren is a prototype replacement for our suite of Python-based scripts that render URLs that are part of a Heritrix crawl in order to determine the URLs of dynamically transcluded dependencies.

Compared to the original implementation, the goals are:

- Fewer moving parts (less to maintain)
- Based on a scalable parallel processing framework (manually scaling is hard)
- Robust, guaranteed processing of requests (won't drop URLs by accident)

It is also an experiment in building a more modular web crawling system.

## Robust Crawl Launching

We also need to reliably launch our regular crawls. The current system relies on a script ([w3start.py](https://github.com/ukwa/python-w3act/blob/master/w3start.py)) that is launched by and hourly cron job. However, if something goes wrong during the launch process, the system cannot retry. A better option is to use the cron job only to place the crawl request on a queue, and use a daemon process to watch that queue and launch the script.

One option is to create a normal server daemon process. We've tended to do this in the past, but this has led to various important services being spread over a number of machines. This makes the dependencies difficult to manage and the processing difficult to monitor.

Using Storm would allow us to centralise these daemons and integrate them into our overall monitoring approach. They would also retry robustly and be less dependent on specific hardware systems.