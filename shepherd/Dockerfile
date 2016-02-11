FROM centos:7

RUN \
  yum install -y epel-release && \
  yum install -y git python-pip python-devel libpng-devel libjpeg-devel gcc gcc-c++ make libffi-devel openssl-devel supervisor libxml2 libxml2-devel libxslt libxslt-devel && \
  pip install requests[security] pika gunicorn supervisor-stdout warctools python-dateutil lxml

ADD python-shepherd /python-shepherd

RUN \
  cd /python-shepherd/python-warcwriterpool && \
  python setup.py install

RUN \
  cd /python-shepherd/python-heritrix && \
  python setup.py install

ADD supervisord.conf /etc/supervisor/conf.d/supervisord.conf

ADD setup-testrun.sh /

EXPOSE 8000 8001

CMD ["/usr/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]