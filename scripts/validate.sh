#!/bin/bash

echo "Waiting for 30 seconds before checking health.."
sleep 30

status_code=$(curl --write-out %{http_code} --silent --output /dev/null http://localhost:8080/actuator/health)
if [[ "$status_code" -ne 200 ]] ; then
  echo "App is not healthy - $status_code"
  exit 1
else
  echo "App is responding with $status_code"
  exit 0
fi


