FROM ubuntu:14.04

RUN \
  apt-get update  && \
  apt-get install -y software-properties-common && \
  add-apt-repository -y ppa:webupd8team/java

RUN \
  apt-get update  && \
  DEBIAN_FRONTEND=noninteractive apt-get upgrade -f -y --force-yes

RUN \
  echo "Installing Java..."  && \
  echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections  && \
  echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections  && \
  DEBIAN_FRONTEND=noninteractive apt-get install -y --force-yes oracle-java7-installer oracle-java8-installer oracle-java7-set-default

RUN apt-get install -y libgflags-dev libsnappy-dev zlib1g-dev libbz2-dev git curl tar build-essential maven

COPY ./rocksdb-bootstrap.sh /
RUN /rocksdb-bootstrap.sh

COPY ./tinycdxserver-bootstrap.sh /
RUN /tinycdxserver-bootstrap.sh

EXPOSE 8080
CMD /usr/lib/jvm/java-8-oracle/bin/java -jar tinycdxserver/tinycdxserver.jar -d /data -p 8080
