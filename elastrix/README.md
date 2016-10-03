Elastrix - Elastic Heritrix
===========================

To make large scale crawling easier to manage, the idea is to shift Heritrix3 over to use a worker model similar to that used by Brozzler.

Auto-scaling
============

Once nice-to-have would be auto-scaling, where the heritrix workers are aware of each other and can cope as workers come and go from the pool.

* Looked at raft-based coordination, using [Atomix](http://atomix.io/atomix/docs/groups/), and it seemed to work. But it's Java 8 and the case where there were just two nodes seem to get confused after one of them had come and gone a few times.
* Looked at Gossip, and there is a [useful library entering Apache incubation](http://gossip.incubator.apache.org/) which seems to work well.

However, these are both likely to be overkill right now, and it should be sufficient to use the frontier itself (e.g. Redis) as the lock/coordination system (as Brozzler does with RethinkDB).

