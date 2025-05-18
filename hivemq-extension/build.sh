#!/bin/env bash

if ! mvn package 
then
    echo Build error
    exit 1
fi

unzip target/hivemq-test-1.0-SNAPSHOT-distribution.zip -d ../docker/extensions -o

docker compose -f ../docker/docker-compose.yml up