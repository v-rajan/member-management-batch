#!/bin/bash

sudo kill -9 $(sudo lsof -t -i:8181) 
./mvnw spring-boot:run