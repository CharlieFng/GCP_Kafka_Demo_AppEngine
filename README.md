# Kafka Springboot Demo on App Engine
This demo is a spring boot application using kafka native libraries, aiming to explore kafka basic features such as topic provision, schema evolution, offset adjustment, generic avro producer and consumer, specific avro producer and consumer.

## Getting Started

### Prerequisites
1. Install gcloud on Deskstop 
2. Install AppEngine Cloud SDK for Java 8
```
gcloud components install app-engine-java
```

### Installing 
#### Start Confluent Kafka stack (controlceneter+schemaregistry+broker+zookeeper)
`docker-compose -f src/main/docker/docker-compose.yml up -d`

#### Start Kafka Spring boot Application 
`mvn appengine:run -Dspring.profiles.active=local`

#### Validate the environment via control center
[Control Center Dashboard](http://localhost:9021)

You can see the cluster is up, and there is one topic named **demo** has been provisioned by application.


## Running the tests

The application expose several controller endpoints as following:
* Topic Controller: 
    * create topic given topic name, partition number and replication factor 
* Schema Controller: 
    * retrieve current schema for a given topic
    * update schema for a given topic
* Offser Controller:
    * adjust offset of all partitions of a given topic to earliest or latest based on offset reset strategy
* Generic Avro Controller:
    * produce generic avro record for a given topic 
    * consume generic avro record for a given topic
* Specific Avro Controller:
    * produce specific avro record for a given topic 
    * consume specific avro record for a given topic 

During the app start up, a topic named **demo** and its v1 schema has been provisioned. Generic avro onsumer and specific avro consumer will share a same consumer group named *shared_consumer_group*.
To run the test, you easily import *postman-local.json* under project folder into postman or run curl commands as the following:

### Create a new topic named *test*
```
curl -X POST \
  http://localhost:8080/topic/create \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -H 'cache-control: no-cache' \
  -d '[
    {
        "name": "test",
        "partition_number": 1,
        "replication_factor": 1
    }
]'
```
All the following tests will use topic named **demo**

### Publish messages in schema v1 via Specific producer 
```
curl -X POST \
  http://localhost:8080/publish/specific/batch \
  -H 'Content-Type: application/json' \
  -H 'cache-control: no-cache' \
  -d '[
    {
        "name": "Charlie",
        "age": 18,
        "income": 2700
    },
    {
        "name": "Chorong",
        "age": 23,
        "income": 1700
    },
    {
        "name": "Kim",
        "age": 13,
        "income": 700
    }
]'
```

### Subscribe messages in schema v1 via Specific Consumer
```
curl -X GET \
  http://localhost:8080/subscribe/specific \
  -H 'cache-control: no-cache'
```

### Retrieve current v1 schema 
```
curl -X GET \
  http://localhost:8080/schema/demo \
  -H 'cache-control: no-cache'
```

### Evolve schema from v1 to v2
```
curl -X POST \
  http://localhost:8080/schema/update/demo \
  -H 'Content-Type: application/json' \
  -H 'cache-control: no-cache' \
  -d '{
    "type": "record",
    "name": "User",
    "namespace": "club.charliefeng.avro",
    "fields": [
        {
            "name": "name",
            "type": "string"
        },
        {
            "name": "age",
            "type": [
                "null",
                "int"
            ],
            "default": null
        },
        {
            "name": "height",
            "type": [
                "null",
                "double"
            ],
            "default": null
        }
    ]
}'
```

### Publish messages in schema v2 via Generic producer
```
curl -X POST \
  http://localhost:8080/publish/generic/batch \
  -H 'Content-Type: application/json' \
  -H 'cache-control: no-cache' \
  -d '[
    {
        "name": "Aaron",
        "age": 18,
        "height": 184.5
    },
    {
        "name": "Eric",
        "age": 23,
        "height": 165.6
    },
    {
        "name": "Tom",
        "age": 13,
        "height": 173.4
    }
]'
```

### Subscribe messages in schema v2 via Generic consumer
```
curl -X GET \
  http://localhost:8080/subscribe/generic \
  -H 'cache-control: no-cache'
```

By far, if you execute either generic consumer or specific consumer again, neither of them will response any messages because the offset for all partitions of *demo* topic have reached to latest so that there is no more messages to consume for *shared_consumer_group*. 

### To replay messages, reset offset to earliest to allow consuming from beginning 
```
curl -X POST \
  'http://localhost:8080/offset/demo?reset_policy=earliest' \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Length: 100' \
  -H 'Content-Type: application/json' \
  -H 'cache-control: no-cache'
```


### Consume all messages in schmema v1 and v2 via Generic consumer 
```
curl -X GET \
  http://localhost:8080/subscribe/generic \
  -H 'cache-control: no-cache'
```











## local start app
 mvn appengine:run

## deploy to cloud
 mvn appengine:deploy

mvn appengine:run -Dspring.profiles.active=local
mvn appengine:run -Dspring.profiles.active=dev

mvn appengine:deploy -Dspring.profiles.active=local
mvn package appengine:deploy -Dspring.profiles.active=dev