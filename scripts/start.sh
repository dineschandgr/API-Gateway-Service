#!/usr/bin/env bash

cd /home/ec2-user/gateway-service

nohup java -javaagent:/home/ec2-user/dd-java-agent.jar -Ddd.logs.injection=true -Ddd.service=gateway-service -Ddd.env=prod -jar *.jar -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError >/dev/null 2>&1 &