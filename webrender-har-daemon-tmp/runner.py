import gzip
import json
import pika
import uuid
import time
import shutil
import logging
import requests
from harchiverd import settings, callback
from daemonize import Daemon
from datetime import datetime
from urlparse import urlparse
from hanzo.warctools import WarcRecord
from warcwriterpool import WarcWriterPool, warc_datetime_str

# Default logging level:
logging.getLogger().setLevel(logging.WARN)

# And for our code:
logger = logging.getLogger("harchiverd")
handler = logging.FileHandler(settings.LOG_FILE)
formatter = logging.Formatter("[%(asctime)s] %(levelname)s: %(message)s")
handler.setFormatter(formatter)
logger.addHandler(handler)
logger.setLevel(logging.INFO)

# Wait so RabbitMQ has time to come up - should really wait and retry rather than have to do this.
time.sleep(15)

if __name__ == "__main__":
        warcwriter = WarcWriterPool(gzip=True, output_dir=settings.OUTPUT_DIRECTORY)
        while True:
            try:
                logger.info("Starting connection: %s" % (settings.AMQP_URL))
                parameters = pika.URLParameters(settings.AMQP_URL)
                connection = pika.BlockingConnection(parameters)
                channel = connection.channel()
                channel.exchange_declare(exchange=settings.AMQP_EXCHANGE,
                                         type="direct", 
                                         durable=True, 
                                         auto_delete=False)
                channel.queue_declare(queue=settings.AMQP_QUEUE, 
                                      durable=True, 
                                      exclusive=False, 
                                      auto_delete=False)
                channel.queue_bind(queue=settings.AMQP_QUEUE, 
                       exchange=settings.AMQP_EXCHANGE,
                       routing_key=settings.AMQP_KEY)
                for method_frame, properties, body in channel.consume(settings.AMQP_QUEUE):
                    callback(warcwriter, body)
                    channel.basic_ack(method_frame.delivery_tag)
            except Exception as e:
                logger.error(str(e))
                requeued_messages = channel.cancel()
                logger.info("Requeued %i messages" % requeued_messages)

