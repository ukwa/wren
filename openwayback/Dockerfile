
FROM java:openjdk-7-jdk

MAINTAINER Andrew Jackson "anj@anjackson.net"

# update packages and install maven
RUN \
  export DEBIAN_FRONTEND=noninteractive && \
  sed -i 's/# \(.*multiverse$\)/\1/g' /etc/apt/sources.list && \
  apt-get update && \
  apt-get -y upgrade && \
  apt-get install -y tar wget curl git maven

# move to /opt and download the tomcat package
RUN cd /opt && wget "http://www.mirrorservice.org/sites/ftp.apache.org/tomcat/tomcat-7/v7.0.65/bin/apache-tomcat-7.0.65.tar.gz" 
RUN cd /opt && tar -zxvf apache-tomcat-7.0.65.tar.gz 
RUN cd /opt && ln -sf apache-tomcat-7.0.65 tomcat

# make tomcat scripts executable
RUN chmod +x /opt/tomcat/bin/startup.sh
RUN chmod +x /opt/tomcat/bin/shutdown.sh
RUN chmod +x /opt/tomcat/bin/catalina.sh

# Cleanup webapps directory
RUN cd /opt/tomcat/webapps && rm -rf *

RUN \
  git clone https://github.com/ukwa/openwayback.git && \
  cd openwayback && \
  git checkout use-correct-archive-filename && \
  mvn install -DskipTests

RUN \
  unzip /openwayback/wayback-webapp/target/openwayback-2.3.0-SNAPSHOT.war -d /opt/tomcat/webapps/ROOT

COPY server.xml /opt/apache-tomcat-7.0.65/conf/server.xml

COPY logging.properties /opt/apache-tomcat-7.0.65/conf/logging.properties
COPY logging.properties /opt/apache-tomcat-7.0.65/webapps/ROOT/WEB-INF/classes/logging.properties

COPY wayback.xml /opt/tomcat/webapps/ROOT/WEB-INF/

COPY RemoteCollection.xml /opt/tomcat/webapps/ROOT/WEB-INF/

COPY CDXCollection.xml /opt/tomcat/webapps/ROOT/WEB-INF/

EXPOSE 8080 8090

ENV JAVA_OPTS -Xmx2g

VOLUME /data

#Fire up tomcat
CMD /opt/tomcat/bin/startup.sh && tail -F /opt/tomcat/logs/catalina.out


