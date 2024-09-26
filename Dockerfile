FROM        maven:3.9-eclipse-temurin-21-alpine AS build
MAINTAINER  "lachy@lachy.space"
COPY        . /opt/lachsbot-build
WORKDIR     /opt/lachsbot-build
RUN         mvn clean install

FROM        eclipse-temurin:21-jdk-alpine
RUN         mkdir /opt/lachsbot-app
COPY        --from=build /opt/lachsbot-build/target/lachsbot.jar /opt/lachsbot-app/lachsbot.jar
WORKDIR     /opt/lachsbot-app
ENTRYPOINT  ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "./lachsbot.jar"]
