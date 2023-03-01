#Demo - IoT Digital Twin - Spring
Not supported by Lightbend in any conceivable way, not open for contributions.
## Prerequisite
Java 17<br>
Apache Maven 3.6 or higher<br>
[Kalix CLI](https://docs.kalix.io/kalix/install-kalix.html) <br>
Docker 20.10.8 or higher (client and daemon)<br>
Container registry with public access (like Docker Hub)<br>
Access to the `gcr.io/kalix-public` container registry<br>
cURL<br>
IDE / editor<br>

## Create kickstart maven project

```
mvn \
archetype:generate \
-DarchetypeGroupId=io.kalix \
-DarchetypeArtifactId=kalix-spring-boot-archetype \
-DarchetypeVersion=LATEST
```
Define value for property 'groupId': `com.example`<br>
Define value for property 'artifactId': `iot-digital-twin-spring`<br>
Define value for property 'version' 1.0-SNAPSHOT: :<br>
Define value for property 'package' io.kx: : `com.example.digitaltwin`<br>

## Import generated project in your IDE/editor

##Configure message broker (Kafka)
```
kalix projects config set broker --broker-service kafka --broker-config-file confluent-kafka.properties
```
##Deploy
```
mvn deploy
```
## Run proxy
```
kalix service proxy iot-digital-twin-spring
```
##Test

Provision:
```
curl -XPOST -d '{
  "name": "dt1",
  "metricValueAlertThreshold": 10
}' http://localhost:8080/dt/1/provision -H "Content-Type: application/json"
```
Get:
```
curl -XGET http://localhost:8080/dt/1 -H "Content-Type: application/json"
```
Add Metric:
```
curl -XPOST -d '{
  "metricValue": 11
}' http://localhost:8080/dt/1/add-metric -H "Content-Type: application/json"
```

Get Kafka broker:
```
kalix project config get broker
```

Publish message to Kafka:
```
curl \
  -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic xxxx" \
  https://pkc-419q3.us-east4.gcp.confluent.cloud:443/kafka/v3/clusters/lkc-nw619k/topics/iot-input/records \
  -d @iot-input.json

```

