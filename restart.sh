nohup sh -c 'mvn jetty:stop && mvn clean && export MAVEN_OPTS="-Xmx712m -XX:MaxPermSize=250m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8764" && mvn jetty:run' &
