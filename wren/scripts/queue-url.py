#!/usr/bin/env python
import pika


message = '{ "url" : "https://acid.matkelly.com/" }';

connection = pika.BlockingConnection(pika.ConnectionParameters(
               'localhost'))
channel = connection.channel()

channel.basic_publish(exchange='',
                      routing_key='to_render',
                      body=message)
print " [x] Sent: %s" % message

connection.close()
