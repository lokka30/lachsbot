FROM        eclipse-temurin:21-jre-jammy
LABEL       org.opencontainers.image.authors = "lachy@lachy.space"
RUN         groupadd -r lachsbot && useradd -r -g lachsbot lachsbot && mkdir /opt/lachsbot-app && chown lachsbot:lachsbot /opt/lachsbot-app
USER        lachsbot
COPY        target/lachsbot.jar /opt/lachsbot-app/lachsbot.jar
WORKDIR     /opt/lachsbot-app
ENTRYPOINT  ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "./lachsbot.jar"]
