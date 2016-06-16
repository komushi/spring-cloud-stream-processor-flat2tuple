# Flat2TupleTransformer
## For what is the simulator created for?
Data-MicroServices Application provided as Spring Cloud Dataflow Processor
Migrated from xd-flat2json-processor:
https://github.com/komushi/xd-flat2json-processor

# 1. Local

## Prerequisites for local

* Maven
* Spring Cloud Dataflow Shell
* Spring Cloud Dataflow Server - Local
* RabbitMQ or Kafka

## Quick Start

### Download and Build
You can skip this step if you use the jar I uploaded.

```
git clone https://github.com/komushi/spring-cloud-stream-processor-flat2tuple.git
cd spring-cloud-stream-processor-flat2tuple
mvn package
```

### Start RabbitMQ

```
rabbitmq-server
```

### Download the Spring Cloud Data Flow Server and Shell apps

```
wget http://repo.spring.io/snapshot/org/springframework/cloud/spring-cloud-dataflow-server-local/1.0.0.BUILD-SNAPSHOT/spring-cloud-dataflow-server-local-1.0.0.BUILD-SNAPSHOT.jar
wget http://repo.spring.io/snapshot/org/springframework/cloud/spring-cloud-dataflow-shell/1.0.0.BUILD-SNAPSHOT/spring-cloud-dataflow-shell-1.0.0.BUILD-SNAPSHOT.jar
```

### Start Dataflow Server Local using RabbitMQ

```
java -jar spring-cloud-dataflow-server-local-1.0.0.BUILD-SNAPSHOT.jar --binder=rabbit
```

### Start Dataflow Shell

```
java -jar spring-cloud-dataflow-shell-1.0.0.BUILD-SNAPSHOT.jar
```

### Register a custom app - flat2tuple in Dataflow Shell

Use the built jar
```
app register --name flat2tuple --type processor --uri file://<local_file_location>/Flat2TupleTransformer-0.0.1-SNAPSHOT.jar
```

Or use the uploaded jar

```
app register --name flat2tuple --type processor --uri https://rawgit.com/komushi/spring-cloud-stream-processor-flat2tuple/master/tmp/Flat2TupleTransformer-0.0.1-SNAPSHOT.jar
```

### Start a stream in Dataflow Shell

```
stream create --name http2tuple2log --definition "http --server.port=9000 | flat2tuple --outputType=application/json | log" --deploy
```

### Post a message

```
http post --target http://localhost:9000  --data "468244D1361B8A3EB8D206CC394BC9E9,BB899DFEA9CC964B50C540A1D685CCFB,2013-01-01 00:00:00,2013-01-01 00:04:00,240,1.71,-73.855383,40.779728,-73.967758,40.760326,CSH,6.50,0.50,0.50,0.00,0.00,7.50"
```

### Check the log sink stdout using tail -f


# 2. Cloud Foundry

## Prerequisites for Cloud Foundry

* Maven
* Spring Cloud Dataflow Shell
* Spring Cloud Dataflow Server for Cloud Foundry
* RabbitMQ & Redis Service for Cloud Foundry
* An account for Cloud Foundry - the example uses PWS

## Quick Start

### Download and Build

```
git clone https://github.com/komushi/spring-cloud-stream-processor-flat2tuple.git
cd spring-cloud-stream-processor-flat2tuple
mvn package
```

### Provision a Redis service instance on Cloud Foundry, for example at Pivotal Web Services

```
cf create-service rediscloud 30mb redis-dataflow
```

### Provision a Rabbit service instance on Cloud Foundry, for example at Pivotal Web Services

```
cf create-service cloudamqp lemur rabbit-dataflow
```

### Download the Spring Cloud Data Flow Server and Shell apps

```
wget http://repo.spring.io/snapshot/org/springframework/cloud/spring-cloud-dataflow-server-cloudfoundry/1.0.0.BUILD-SNAPSHOT/spring-cloud-dataflow-server-cloudfoundry-1.0.0.BUILD-SNAPSHOT.jar
wget http://repo.spring.io/snapshot/org/springframework/cloud/spring-cloud-dataflow-shell/1.0.0.BUILD-SNAPSHOT/spring-cloud-dataflow-shell-1.0.0.BUILD-SNAPSHOT.jar
```

### Deploying the Server app on PWS
You must use a unique name for your app; an app with the same name in the same organization will cause your deployment to fail

```
cf push <dataflow_server_name> --no-start -p spring-cloud-dataflow-server-cloudfoundry-1.0.0.BUILD-SNAPSHOT.jar
cf bind-service <dataflow_server_name> redis-dataflow
cf bind-service <dataflow_server_name> rabbit-dataflow
cf set-env <dataflow_server_name> SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_URL https://api.run.pivotal.io
cf set-env <dataflow_server_name> SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_ORG <org>
cf set-env <dataflow_server_name> SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_SPACE <space>
cf set-env <dataflow_server_name> SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_DOMAIN cfapps.io
cf set-env <dataflow_server_name> SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_SERVICES redis,rabbit
cf set-env <dataflow_server_name> SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_USERNAME <email>
cf set-env <dataflow_server_name> SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_PASSWORD <password>
cf set-env <dataflow_server_name> SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_SKIP_SSL_VALIDATION true
cf set-env <dataflow_server_name> SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_MEMORY 512
cf set-env <dataflow_server_name> SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_DISK 512
cf set-env <dataflow_server_name> SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_INSTANCES 1
cf start <dataflow_server_name>
```

### Start Dataflow Shell

```
java -jar spring-cloud-dataflow-shell-1.0.0.BUILD-SNAPSHOT.jar
```

### Connect to the Dataflow Server

```
server-unknown:>dataflow config server http://<dataflow_server_name>.cfapps.io
Successfully targeted http://<dataflow_server_name>.cfapps.io
dataflow:>
```

### Register sink, source & processor

```
dataflow:>app register --name http --type source --uri maven://org.springframework.cloud.stream.app:http-source-rabbit:1.0.0.BUILD-SNAPSHOT
Successfully registered application 'source:http'

dataflow:>app register --name log --type sink --uri maven://org.springframework.cloud.stream.app:log-sink-rabbit:1.0.0.BUILD-SNAPSHOT
Successfully registered application 'sink:log'

dataflow:>app register --name flat2tuple --type processor --uri https://rawgit.com/komushi/spring-cloud-stream-processor-flat2tuple/master/tmp/Flat2TupleTransformer-0.0.1-SNAPSHOT.jar
Successfully registered application 'processor:flat2tuple'

dataflow:>app list
╔══════╤══════════╤════╤════╗
║source│processor │sink│task║
╠══════╪══════════╪════╪════╣
║http  │flat2tuple│log │    ║
╚══════╧══════════╧════╧════╝
```

### Create a stream

It takes a little time for all the 3 Spring Boot Applications to start.

```
dataflow:>stream create --name http2tuple2log --definition "http | flat2tuple --outputType=application/json | log" --deploy
Created and deployed new stream 'http2tuple2log'

dataflow:>stream list
╔══════════════╤═════════════════════════════════════════════════════╤══════════╗
║ Stream Name  │                  Stream Definition                  │  Status  ║
╠══════════════╪═════════════════════════════════════════════════════╪══════════╣
║http2tuple2log│http | flat2tuple --outputType=application/json | log│undeployed║
╚══════════════╧═════════════════════════════════════════════════════╧══════════╝

dataflow:>stream list
╔══════════════╤═════════════════════════════════════════════════════╤════════╗
║ Stream Name  │                  Stream Definition                  │ Status ║
╠══════════════╪═════════════════════════════════════════════════════╪════════╣
║http2tuple2log│http | flat2tuple --outputType=application/json | log│deployed║
╚══════════════╧═════════════════════════════════════════════════════╧════════╝
```

### Start to monitor the log sink app

```
cf apps
Getting apps ...
OK

name                        requested state   instances   memory   disk   urls
...
...
http2tuple2log-flat2tuple   started           1/1         512M     512M   http2tuple2log-flat2tuple.cfapps.io
http2tuple2log-http         started           1/1         512M     512M   http2tuple2log-http.cfapps.io
http2tuple2log-log          started           1/1         512M     512M   http2tuple2log-log.cfapps.io

cf logs http2tuple2log-log
Connected, tailing logs for app http2tuple2log-log ...
```

### Send some data to the source app
```
dataflow:>http post --target http://http2tuple2log-http.cfapps.io/  --data "468244D1361B8A3EB8D206CC394BC9E9,BB899DFEA9CC964B50C540A1D685CCFB,2013-01-01 00:00:00,2013-01-01 00:04:00,240,1.71,-73.855383,40.779728,-73.967758,40.760326,CSH,6.50,0.50,0.50,0.00,0.00,7.50"

```

### The tailing log output
```
2016-06-17T03:35:21.93+0900 [APP/0]      OUT 2016-06-16 18:35:21.931  INFO 19 --- [ttp2tuple2log-1] log.sink                                 : {"pickupLatitude":40.760326,"pickupLongitude":-73.967758,"dropoffLatitude":40.779728,"dropoffLongitude":-73.855383,"pickupDatetime":"2013-01-01 00:00:00","dropoffDatetime":"2013-01-01 00:04:00","route":"C161.160_C180.155","newid":{"route":"C161.160_C180.155"}}
```

# 3. Run with Gemfire Sink
dataflow shell
```
dataflow:>app register --name flat2tuple --type processor --uri file:///Users/lei_xu/Desktop/Flat2TupleTransformer-0.0.1-SNAPSHOT.jar
Successfully registered application 'processor:flat2tuple'

dataflow:>stream create http2gem --definition "http --server.port=9000 | flat2tuple --outputType=application/json | gemfire --regionName=RegionRaw --json=true --keyExpression=payload.getField('uuid')" --deploy
Created and deployed new stream 'http2gem'

dataflow:>http post --target http://localhost:9000  --data "468244D1361B8A3EB8D206CC394BC9E9,BB899DFEA9CC964B50C540A1D685CCFB,2013-01-01 00:00:00,2013-01-01 00:04:00,240,1.71,-73.856383,40.769728,-73.968758,40.750326,CSH,6.50,0.50,0.50,0.00,0.00,7.50"

dataflow:>http post --target http://localhost:9000  --data "468244D1361B8A3EB8D206CC394BC9E9,BB899DFEA9CC964B50C540A1D685CCFB,2013-01-01 00:00:00,2013-01-01 00:04:00,240,1.71,-73.857383,40.760728,-73.969758,40.750326,CSH,6.50,0.50,0.50,0.00,0.00,7.50"

```

gfsh
```
gfsh>query --query="select * from /RegionRaw"

Result     : true
startCount : 0
endCount   : 20
Rows       : 2

                uuid                 |       route       |   timestamp   | pickupLatitude | pickupLongitude | dropoffLatitude | dropoffLongitude |   pickupDatetime    | dropoffDatetime
------------------------------------ | ----------------- | ------------- | -------------- | --------------- | --------------- | ---------------- | ------------------- | -------------------
488fb515-0bba-448b-b590-fce23cc00699 | C161.162_C180.159 | 1466102556782 | 40.750326      | -73.969758      | 40.760728       | -73.857383       | 2013-01-01 00:00:00 | 2013-01-01 00:04:00
026d8de7-22a1-46c6-89b4-58aacf5b6392 | C161.162_C180.157 | 1466102536059 | 40.750326      | -73.968758      | 40.769728       | -73.856383       | 2013-01-01 00:00:00 | 2013-01-01 00:04:00

NEXT_STEP_NAME : END
```
