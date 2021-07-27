#!/usr/bin/env bash

cd /home/ec2-user/edge-service

nohup java -jar *.jar >/dev/null 2>&1 &