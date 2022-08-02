FROM openjdk:16-jdk-alpine
EXPOSE 8080
ARG JAR_FILE=target/application-1.0-SNAPSHOT.jar
WORKDIR /opt/app
COPY ${JAR_FILE} app.jar
EXPOSE $PORT
ENTRYPOINT ["java","-jar","app.jar"]