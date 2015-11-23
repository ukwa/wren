
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
  wget -q http://geolite.maxmind.com/download/geoip/database/GeoLite2-City.mmdb.gz && \
  gunzip GeoLite2-City.mmdb.gz && \
  curl -L -O https://download.elastic.co/beats/filebeat/filebeat_1.0.0-rc1_amd64.deb && \
  dpkg -i filebeat_1.0.0-rc1_amd64.deb

ADD filebeat.yml /etc/filebeat/filebeat.yml

ADD start.sh /start.sh

COPY logging.properties /logging.properties

EXPOSE 8443

ENV FOREGROUND true

#ENV MONITRIX_ENABLED true

ENV JAVA_OPTS -Xmx2g

VOLUME /heritrix3

VOLUME /bl-heritrix-modules

VOLUME /jobs

VOLUME /output

CMD /start.sh

