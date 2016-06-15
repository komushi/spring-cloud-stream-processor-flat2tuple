# Flat2TupleTransformer
## For what is the simulator created for?
Data-MicroServices Application provided as Spring Cloud Dataflow Processor
Migrated from xd-flat2json-processor:
https://github.com/komushi/xd-flat2json-processor

# 0. Local

```
java -jar target/Flat2TupleTransformer-0.0.1-SNAPSHOT.jar -—spring.cloud.stream.defaultBinder=kafka
```

# 1. Local

## Prerequisites for local

* Maven
* Spring Cloud Dataflow Shell
* Spring Cloud Dataflow Server - Local
* RabbitMQ or Kafka

## Quick Start

### Download and Build

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
wget http://repo.spring.io/snapshot/org/springframework/cloud/spring-cloud-dataflow-server-cloudfoundry/1.0.0.BUILD-SNAPSHOT/spring-cloud-dataflow-server-cloudfoundry-1.0.0.BUILD-SNAPSHOT.jar
wget http://repo.spring.io/snapshot/org/springframework/cloud/spring-cloud-dataflow-shell/1.0.0.BUILD-SNAPSHOT/spring-cloud-dataflow-shell-1.0.0.BUILD-SNAPSHOT.jar
```

### Start Dataflow Server Local

```
java -jar spring-cloud-dataflow-server-local-1.0.0.BUILD-SNAPSHOT.jar --binder=rabbit
```

### Start Dataflow Shell

```
java -jar spring-cloud-dataflow-shell-1.0.0.BUILD-SNAPSHOT.jar
```

### Register a custom app - flat2tuple in Dataflow Shell

```
app register --name flat2tuple --type processor --uri http://<public_file_location>/Flat2TupleTransformer-0.0.1-SNAPSHOT.jar
app register --name flat2tuple --type processor --uri file://<local_file_location>/Flat2TupleTransformer-0.0.1-SNAPSHOT.jar
```

### Start a stream in Dataflow Shell

```
stream create --name http2tuple2log --definition "http --server.port=9000 | flat2tuple | log" --deploy
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
cf set-env dataflow-server SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_URL https://api.run.pivotal.io
cf set-env dataflow-server SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_ORG <org>
cf set-env dataflow-server SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_SPACE <space>
cf set-env dataflow-server SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_DOMAIN cfapps.io
cf set-env dataflow-server SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_SERVICES redis,rabbit
cf set-env dataflow-server SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_USERNAME <email>
cf set-env dataflow-server SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_PASSWORD <password>
cf set-env dataflow-server SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_SKIP_SSL_VALIDATION true
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

dataflow:>app register --name flat2tuple --type processor --uri http://ks3001165.kimsufi.com/webui-aria2/Flat2TupleTransformer-0.0.1-SNAPSHOT.jar
Successfully registered application 'processor:flat2tuple'
```


