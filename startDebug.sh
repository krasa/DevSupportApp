#!/bin/sh
curl -X POST --connect-timeout 1 localhost:8765/monitoring/shutdown

export JAVA_OPTS="-Xmx712m -XX:MaxPermSize=250m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8764"

nohup /opt/java/jdk1.8.0_71/bin/java $JAVA_OPTS -jar vojtitko.jar

