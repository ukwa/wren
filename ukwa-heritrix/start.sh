#!/bin/sh

filebeat -v -e -c /etc/filebeat/filebeat.yml & 

./heritrix-3.3.0-SNAPSHOT/bin/heritrix -a heritrix:heritrix -b 0.0.0.0 -j /jobs