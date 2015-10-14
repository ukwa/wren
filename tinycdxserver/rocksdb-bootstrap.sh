#git clone https://github.com/facebook/rocksdb.git
#cd rocksdb/
#git checkout rocksdb-3.11.2
curl -L -O https://github.com/facebook/rocksdb/archive/rocksdb-3.13.1.tar.gz
tar xvfz rocksdb-3.13.1.tar.gz
cd rocksdb-rocksdb-3.13.1/
make static_lib
export JAVA_HOME=/usr/lib/jvm/java-7-oracle
make rocksdbjava
make jtest
make install
cd ..

