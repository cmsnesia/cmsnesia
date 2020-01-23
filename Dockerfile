FROM maven:3.6.1-jdk-8-alpine AS builder

WORKDIR /workspace
COPY . .
RUN mvn -e -B clean package -DskipTests

FROM openjdk:14-jdk-alpine
#FROM openjdk:8-jre-alpine

LABEL APP="gri-web"
LABEL DOMAIN="gri"

RUN addgroup -S gri && adduser -S gri-web -G gri
USER gri-web:gri

WORKDIR /app

COPY --from=builder /workspace/gri-web/target/gri-web-*.jar /app/gri-web.jar

ENTRYPOINT ["java", "-Dserver.port=$PORT", "-jar", "/app/gri-web.jar"]