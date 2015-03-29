#!/bin/sh
curl -X POST --connect-timeout 1 localhost:8765/monitoring/shutdown

export MAVEN_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8764"

nohup mvn spring-boot:run

