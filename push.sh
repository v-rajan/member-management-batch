#!/bin/bash

. ./setenv.sh 

mvn clean package -Dmaven.test.skip=true
rm target/*.original
docker build -f  src/main/docker/Dockerfile.nobuild.jvm -t ${PROJECT_NAME}:${PROJECT_VERSION} .

docker tag ${PROJECT_NAME}:${PROJECT_VERSION} ${PROJECT_ID}/${PROJECT_NAME}:${PROJECT_VERSION}
docker push ${PROJECT_ID}/${PROJECT_NAME}:${PROJECT_VERSION}