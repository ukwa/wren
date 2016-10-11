FROM java:openjdk-8-jdk

ENV         ACTIVATOR_VERSION 1.3.6

# Install Typesafe Activator
RUN         cd /tmp && \
            wget -q http://downloads.typesafe.com/typesafe-activator/$ACTIVATOR_VERSION/typesafe-activator-$ACTIVATOR_VERSION.zip && \
            unzip -q typesafe-activator-$ACTIVATOR_VERSION.zip -d /usr/local && \
            mv /usr/local/activator-dist-$ACTIVATOR_VERSION /usr/local/activator && \
            rm typesafe-activator-$ACTIVATOR_VERSION.zip

WORKDIR /w3act

VOLUME /w3act

EXPOSE 9000

CMD /w3act/bin/w3act -Dconfig.file=/w3act/conf/dev.conf
