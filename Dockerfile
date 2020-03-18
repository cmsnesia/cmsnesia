FROM maven:3.6.1-jdk-8-alpine AS builder

WORKDIR /workspace
COPY . .
RUN mvn -e -B -s .mvn/release-settings.xml clean package -DskipTests

#FROM openjdk:14-jdk-alpine
FROM openjdk:8-jre-alpine

LABEL APP="cmsnesia-web"
LABEL DOMAIN="cmsnesia"

RUN apk add --no-cache tzdata
RUN echo "Asia/Jakarta" > /etc/timezone

RUN addgroup -S cmsnesia && adduser -S cmsnesia-web -G cmsnesia
USER cmsnesia-web:cmsnesia

WORKDIR /app

COPY --from=builder /workspace/cmsnesia-web/target/cmsnesia-web-*.jar /app/cmsnesia-web.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app/cmsnesia-web.jar"]