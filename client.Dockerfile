
# base image is jdk
FROM openjdk:10-slim

# working directory
WORKDIR /app

# copy files
COPY target/chatroom-1.0-SNAPSHOT.jar /app/chatroom-1.0-SNAPSHOT.jar

# run client
ENTRYPOINT ["java", "-cp", "/app/chatroom-1.0-SNAPSHOT.jar", "tcp.Client", "localhost", "4000"]