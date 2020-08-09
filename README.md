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
