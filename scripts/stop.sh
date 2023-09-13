#!/usr/bin/env bash

kill $(lsof -t -i:8080)
exit 0