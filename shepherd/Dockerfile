FROM centos:7

ADD yum.conf /etc/yum.conf

RUN \
  yum install -y epel-release && \
  yum install -y git python-pip python-devel libpng-devel libjpeg-devel gcc gcc-c++ make libffi-devel openssl-devel supervisor && \
  pip install --trusted-host pypi.python.org requests[security] pika gunicorn supervisor-stdout

ADD python-shepherd /python-shepherd

ADD supervisord.conf /etc/supervisor/conf.d/supervisord.conf

EXPOSE 8000 8001

CMD ["/usr/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]