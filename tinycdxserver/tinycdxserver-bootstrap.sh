export JAVA_HOME=/usr/lib/jvm/java-8-oracle

git clone https://github.com/anjackson/tinycdxserver.git
cd tinycdxserver/

mvn package || exit

cp target/tiny*.jar tinycdxserver.jar
