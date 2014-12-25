curl -X POST --connect-timeout 1  localhost:8765/monitoring/shutdown 


nohup sh -c 'mvn clean && export MAVEN_OPTS="-Xmx712m -XX:MaxPermSize=250m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8764" && mvn spring-boot:run' &
