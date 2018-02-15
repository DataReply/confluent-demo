from confluent_kafka import Producer
import json

p = Producer({'bootstrap.servers': 'localhost:9092'})
message = {}
message['camera_id'] = 1
message['pill_id'] = 1024
message['score'] = 0.639
# message['coordinates'] = (5,5)
message['pill_x'] = 5
message['pill_y'] = 5
message['color'] = 1
message['damaged'] = 1

json_data = json.dumps(message)

print json_data
for i in range(1,110):
        p.produce('mnm-input', bytes(json_data))

from random import randint
print(0)
for i in range(1,100):
   message['color'] = 0
   c = randint(1, 2)
   for j in range(1,2):
        message['damaged'] = randint(0, 1)
        json_data = json.dumps(message)
        print json_data
        p.produce('mnm-input', bytes(json_data), str(0))

   p.flush()

print(1)
for i in range(1,100):
   message['color'] = 1
   message['damaged'] = randint(0, 1)
   c = randint(1, 100)
   for j in range(1,2):
        message['damaged'] = randint(0, 1)
        json_data = json.dumps(message)
        print json_data
        p.produce('mnm-input', bytes(json_data), str(1))

   p.flush()


print(2)
for i in range(1,100):
   message['color'] = 2
   message['damaged'] = randint(0, 1)
   c = randint(1, 100)
   for j in range(1,2):
        message['damaged'] = randint(0, 1)
        json_data = json.dumps(message)
        print json_data
        p.produce('mnm-input', bytes(json_data), str(2))

   p.flush()

print "Produced messages"

p.flush()