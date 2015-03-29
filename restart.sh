#!/bin/env sh
TIMEOUT_COUNTER=30
PID="$(< application.pid)"
if [ -z "${PID}" ]; then
  echo "No PID found"
fi
    echo -n "Shutting down the Application [PID: ${PID}] "
    kill ${PID}
    while [ $(( TIMEOUT_COUNTER-- )) -gt 0 ] && kill -0 ${PID} 2>/dev/null; do
      sleep 1
      echo -n "."
    done
if [ ${TIMEOUT_COUNTER} -lt 1 ]; then
  echo "Killing processes [PID: ${PID}] which didn't stop after $TIMEOUT seconds"
  kill -9 ${PID}
else
  echo
  echo "Server has been stopped gracefully"
  rm application.pid 2> /dev/null
fi


nohup sh -c 'mvn clean && export MAVEN_OPTS="-Xmx712m -XX:MaxPermSize=250m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8764" && mvn spring-boot:run' &
