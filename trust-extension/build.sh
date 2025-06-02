#!/bin/env bash

if ! mvn package 
then
    echo Build error
    exit 1
fi


if [ "$1" = "local" ]
then
    mkdir ../docker/extensions > /dev/null
    rm -rf ../docker/extensions/trust-extension
    unzip target/trust-extension-1.0-distribution.zip -d ../docker/extensions || exit 2

    docker compose -f ../docker/docker-compose.yml up
    exit 0
fi

if scp target/trust-extension-1.0-distribution.zip hivemq.lan:.
then
    ssh hivemq.lan "./run-server.sh"
    ssh hivemq.lan "docker compose -f hivemq/docker-compose.yml stop hivemq"
fi
