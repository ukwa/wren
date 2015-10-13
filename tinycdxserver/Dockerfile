FROM ubuntu:14.04

RUN \
  echo "Adding webupd8team repository..."  && \
  echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee /etc/apt/sources.list.d/webupd8team-java.list  && \
  echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee -a /etc/apt/sources.list.d/webupd8team-java.list  && \
  apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EEA14886

RUN \
  echo "Updating packages..."  && \
  apt-get update  && \
  DEBIAN_FRONTEND=noninteractive apt-get upgrade -f -y --force-yes

RUN \
  echo "Installing Java..."  && \
  echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections  && \
  echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections  && \
  DEBIAN_FRONTEND=noninteractive apt-get install -y --force-yes oracle-java8-installer oracle-java8-set-default

RUN apt-get install -y libgflags-dev libsnappy-dev zlib1g-dev libbz2-dev git curl tar build-essential maven

COPY ./rocksdb-bootstrap.sh /

RUN /rocksdb-bootstrap.sh

COPY ./tinycdxserver-bootstrap.sh /

RUN /tinycdxserver-bootstrap.sh




