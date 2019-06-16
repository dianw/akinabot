FROM openjdk:8-jdk-alpine
VOLUME /akinabot
ARG JAR_FILE=*.jar
ADD target/${JAR_FILE} /akinabot/app.jar
ENV JAVA_OPTS=""
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /akinabot/app.jar