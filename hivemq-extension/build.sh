#!/bin/env bash

if ! mvn package 
then
    echo Build error
    exit 1
fi


if [ "$1" = "local" ]
then
    mkdir ../docker/extensions > /dev/null
    rm -rf ../docker/extensions/hivemq-test
    unzip target/hivemq-test-1.0-SNAPSHOT-distribution.zip -d ../docker/extensions || exit 2

    docker compose -f ../docker/docker-compose.yml up
    exit 0
fi

if scp target/hivemq-test-1.0-SNAPSHOT-distribution.zip hivemq.lan:.
then
    ssh hivemq.lan "./run-server.sh"
fi
