Merge a CSV and a XML file into a JSON file. 

![solution](doc/images/solution.png)

The solution is deployed on Spring Boot with Spring Batch and Apache Camel. 

[Spring Boot](https://spring.io/projects/spring-boot)

[Spring Batch](https://camel.apache.org)

[Apache Camel](https://www.mailgun.com)

# Prerequites
* JDK 11
* Maven

# Setup
### Build
```console
cd member-management-batch
mvn clean install
```
### Start Server

```console
./mvnw spring-boot:run
```

# Testing

### Run test
```console
cd src/test/resources/payload/sample001
./curl-test.sh
```

# Properties
Key | Description
:------- | ------                               
| batch.process.dir | Directory used to store the files to be processed.
| batch.process.queue.consumer  | The concurent consumers used by the apache camel seda endpoint.

# Docker
[member-management-batch](https://hub.docker.com/repository/docker/vrajan/member-management-batch)
## Start Server
```console
cd ..
docker pull vrajan/member-management-batch:latest

docker run  -t \
    --name member-management-batch \
    -p 8181:8181 \
    -e SERVER_PORT=8181 \
    -e DB_URL=jdbc:h2:mem:db \
    -e DB_DRIVER_CLASS=org.h2.Driver \
    -e DB_USERNAME=sa \
    -e DB_PASSWORD= \
    -e DB_PLATFORM=org.hibernate.dialect.H2Dialect \
    -e DB_H2_CONSOLE_ENABLED=false \
    -e JPA_GENERATE_DDL=true \
    -e JPA_HIBERNATE_DDL_AUTO=create \
    -e JPA_SHOW_SQL=false \
    -e BATCH_INIT_SCHEMA=ALWAYS \
    -e LOG_LEVEL_ROOT=INFO \
    -e MULTIPART_MAX_FILE=100MB \
    -e MULTIPART_REQUEST_SIZE=100MB \
    -e BATCH_PROCESS_DIR=/tmp \
    -e BATCH_PROCESS_QUEUE_CONSUMER=3 \
    vrajan/member-management-batch:latest
	
