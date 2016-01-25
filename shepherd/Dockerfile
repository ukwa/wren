FROM centos:7

RUN \
  yum install -y epel-release && \
  yum install -y git python-pip python-devel libpng-devel libjpeg-devel gcc gcc-c++ make libffi-devel openssl-devel supervisor && \
  pip install requests[security] pika gunicorn supervisor-stdout warctools

ADD python-shepherd /python-shepherd

RUN \
  cd /python-shepherd/python-warcwriterpool && \
  python setup.py install

ADD supervisord.conf /etc/supervisor/conf.d/supervisord.conf

EXPOSE 8000 8001

CMD ["/usr/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]