FROM ubuntu:14.04

RUN apt-get update && \
    apt-get install -y python3-dev python3-gdbm curl git libffi-dev libssl-dev && \
    curl "https://bootstrap.pypa.io/get-pip.py" | python3.4

RUN pip3.4 install warcprox 

CMD warcprox -d /output/warcs --base32 -z -P 8800 --rollover-idle-time 3600 2>&1 | tee /output/log

