# Wren

An experiment in building large-scale web-rendering crawl tools on [Storm](http://storm.apache.org/), extending [storm-crawler](https://github.com/DigitalPebble/storm-crawler).

## Elastic Web Rendering

Wren is a prototype replacement for our suite of Python-based scripts that render URLs that are part of a Heritrix crawl in order to determine the URLs of dynamically transcluded dependencies.

Compared to the original implementation, the goals are:

- Fewer moving parts (less to maintain)
- Based on a scalable parallel processing framework (manually scaling is hard)
- Robust, guarenteed processing (won't drop URLs by accident)

It is also an experiment in building a more modular web crawling system.

