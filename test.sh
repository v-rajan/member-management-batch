#!/bin/bash

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
