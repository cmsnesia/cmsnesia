FROM openjdk:13-jdk-alpine
ARG SERVICE_NAME
ARG JAR_FILE

VOLUME /tmp

COPY ${JAR_FILE} gri-web.jar

ENV SERVICE_NAME=${SERVICE_NAME}
ENV PORT=${PORT}
LABEL APP=${SERVICE_NAME}
LABEL DOMAIN="gri"

ENTRYPOINT [ "java", "-Xmx128m", "-Xms64m", "-Dserver.port=$PORT", "-Dspring.profiles.active=dev", "-jar", "/gri-web.jar"]