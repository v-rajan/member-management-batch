#! /bin/bash

export PROJECT_NAME=member-management-batch
export PROJECT_VERSION=latest
export PROJECT_ID=vrajan

export SERVER_PORT=8181

export DB_URL=jdbc:h2:mem:db
export DB_DRIVER_CLASS=org.h2.Driver
export DB_USERNAME=sa
export DB_PASSWORD=
export DB_PLATFORM=org.hibernate.dialect.H2Dialect
export DB_H2_CONSOLE_ENABLED=false

export JPA_GENERATE_DDL=true
export JPA_HIBERNATE_DDL_AUTO=create
export JPA_SHOW_SQL=false

export BATCH_INIT_SCHEMA=ALWAYS

export LOG_LEVEL_ROOT=INFO

export MULTIPART_MAX_FILE=100MB
export MULTIPART_REQUEST_SIZE=100MB

export BATCH_PROCESS_DIR=/tmp
export BATCH_PROCESS_QUEUE_CONSUMER=3



