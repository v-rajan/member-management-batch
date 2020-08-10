#!/bin/bash

. ./setenv.sh

echo "Stopping and removing previous ${PROJECT_NAME} containers"
OLD=$(sudo docker ps -a | grep "${PROJECT_NAME}" | awk '{print $1}' | paste -sd ' ' -)
if [ ! -z $OLD ]; then
  sudo docker stop $OLD
  sudo docker rm $OLD
fi

echo "Start ${PROJECT_NAME}"
sudo docker run  -t \
	--name ${PROJECT_NAME} \
	-p 8181:8181 \
	-e SERVER_PORT=${SERVER_PORT} \
	-e DB_URL=${DB_URL} \
    -e DB_DRIVER_CLASS=${DB_DRIVER_CLASS} \
	-e DB_USERNAME=${DB_USERNAME} \
	-e DB_PASSWORD=${DB_PASSWORD} \
    -e DB_PLATFORM=${DB_PLATFORM} \
	-e DB_H2_CONSOLE_ENABLED=${DB_H2_CONSOLE_ENABLED} \
	-e JPA_GENERATE_DDL=${JPA_GENERATE_DDL} \
    -e JPA_HIBERNATE_DDL_AUTO=${JPA_HIBERNATE_DDL_AUTO} \
	-e JPA_SHOW_SQL=${JPA_SHOW_SQL} \
	-e BATCH_INIT_SCHEMA=${BATCH_INIT_SCHEMA} \
    -e LOG_LEVEL_ROOT=${LOG_LEVEL_ROOT} \
	-e MULTIPART_MAX_FILE=${MULTIPART_MAX_FILE} \
    -e MULTIPART_REQUEST_SIZE=${MULTIPART_REQUEST_SIZE} \
	-e BATCH_PROCESS_DIR=${BATCH_PROCESS_DIR} \
    -e BATCH_PROCESS_QUEUE_CONSUMER=${BATCH_PROCESS_QUEUE_CONSUMER} \
	${PROJECT_ID}/${PROJECT_NAME}:${PROJECT_VERSION}