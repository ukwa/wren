tinycdxserver
=============

This Docker image packaged up the NLA's [tinycdxserver](https://github.com/nla/tinycdxserver), which is a CDX server you can write to as well as read from, based on the RocksDB database engine.  To run it:

    docker pull anjackson/tinycdxserver
    docker run -p 18080:8080 -v data:/data anjackson/tinycdxserver

You should now be able to go to ```http://<docker-host-ip-address>:18080/``` and see the CDX server status. See <https://gist.github.com/ato/b2ad8e65b35afe690921> for details of how to use it.

Note that when you share a folder with a Docker app when using ```selinux``` you might need to [tweak the folder attributes](http://stackoverflow.com/questions/24288616/permission-denied-on-accessing-host-directory-in-docker).

