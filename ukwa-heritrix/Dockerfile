
FROM java:openjdk-7-jdk

MAINTAINER Andrew Jackson "anj@anjackson.net"

# update packages and install maven
RUN \
  export DEBIAN_FRONTEND=noninteractive && \
  sed -i 's/# \(.*multiverse$\)/\1/g' /etc/apt/sources.list && \
  apt-get update && \
  apt-get -y upgrade && \
  apt-get install -y vim wget curl git maven

ADD heritrix3 /heritrix3

ADD bl-heritrix-modules /bl-heritrix-modules

RUN \
  cd heritrix3 && \
  mvn install -DskipTests && \
  cd contrib && \
  mvn install -DskipTests

RUN \
  cd bl-heritrix-modules && \
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

ADD start.sh /start.sh

EXPOSE 8443

ENV FOREGROUND true

ENV JAVA_OPTS -Xmx2g

VOLUME /jobs

VOLUME /output

CMD /start.sh

