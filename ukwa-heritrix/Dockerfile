
FROM java:openjdk-7-jdk

MAINTAINER Andrew Jackson "anj@anjackson.net"

# update packages and install maven
RUN \
  export DEBIAN_FRONTEND=noninteractive && \
  sed -i 's/# \(.*multiverse$\)/\1/g' /etc/apt/sources.list && \
  apt-get update && \
  apt-get -y upgrade && \
  apt-get install -y vim wget curl git maven

RUN \
  git clone https://github.com/ukwa/heritrix3.git && \
  cd heritrix3 && \
  mvn install -DskipTests

RUN \
  git clone https://github.com/ukwa/bl-heritrix-modules.git && \
  cd bl-heritrix-modules && \
  mvn install -DskipTests

RUN \
  cd heritrix3/contrib && \
  mvn install -DskipTests

RUN \
  wget -q http://geolite.maxmind.com/download/geoip/database/GeoLite2-City.mmdb.gz && \
  gunzip GeoLite2-City.mmdb.gz && \
  tar xvfz heritrix3/dist/target/heritrix-3.3.0-SNAPSHOT-dist.tar.gz && \
  cp /heritrix3/contrib/target/heritrix-contrib-*.jar ./heritrix-3.3.0-SNAPSHOT/lib && \
  cp /bl-heritrix-modules/target/bl-heritrix-modules-*jar-with-dependencies.jar ./heritrix-3.3.0-SNAPSHOT/lib

RUN \
  curl -L -O https://download.elastic.co/beats/filebeat/filebeat_1.0.0-rc1_amd64.deb && \
  dpkg -i filebeat_1.0.0-rc1_amd64.deb

ADD filebeat.yml /etc/filebeat/filebeat.yml

EXPOSE 8443

ENV FOREGROUND true

ENV JAVA_OPTS -Xmx2g

VOLUME /jobs

VOLUME /output

CMD filebeat -e -c /etc/filebeat/filebeat.yml & ./heritrix-3.3.0-SNAPSHOT/bin/heritrix -a heritrix:heritrix -b 0.0.0.0 -j /jobs

