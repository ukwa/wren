#!/bin/sh

# Enable logging to Monitrix (ELK):
if [ "$MONITRIX_ENABLED" ]; then
    echo Attempting to send logs to Monitrix
    filebeat -v -e -c /etc/filebeat/filebeat.yml & 
else
	echo Monitrix crawl logging disabled
fi

# Check we are built and up to date.
#cd /heritrix3
#mvn install -DskipTests
#cd contrib
#mvn install -DskipTests
#cd /

#cd /bl-heritrix-modules
#mvn install -DskipTests
#cd /

# Unpack crawler:
tar xvfz heritrix3/dist/target/heritrix-3.3.0-SNAPSHOT-dist.tar.gz
cp /heritrix3/contrib/target/heritrix-contrib-*.jar ./heritrix-3.3.0-SNAPSHOT/lib
cp /bl-heritrix-modules/target/bl-heritrix-modules-*jar-with-dependencies.jar ./heritrix-3.3.0-SNAPSHOT/lib
cp /logging.properties /heritrix-3.3.0-SNAPSHOT/conf/logging.properties

# And fire it up:
./heritrix-3.3.0-SNAPSHOT/bin/heritrix -a heritrix:heritrix -b 0.0.0.0 -j /jobs