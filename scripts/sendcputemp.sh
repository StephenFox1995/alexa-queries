#!/usr/bin/env bash

# Sends CPU temp to API on MacOs.
while true
do
    timestamp=$(date -u +"%Y-%m-%dT%H:%M:%SZ")
    temp=$(./osx-cpu-temp/osx-cpu-temp)
    echo $temp
    curl -X POST \
    http://localhost:8080/api/alexa/temperature \
    -H "content-type:application/json" \
    -d "{ \"data\": [{\"temperature\": ${temp}, \"timestamp\": \"${timestamp}\"}]}"
    sleep 2
done