docker-compose up
docker exec -i -t "composetestcrawler_solr_1" /opt/solr/bin/solr create_collection  -c waindex -shards 2 -p 8983

