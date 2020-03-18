FROM adoptopenjdk/openjdk11 AS builder

WORKDIR /workspace
COPY . .
RUN ./mvnw -e -B -s .mvn/release-settings.xml clean package -DskipTests

FROM adoptopenjdk/openjdk11:alpine-jre

LABEL APP="cmsnesia-web"
LABEL DOMAIN="cmsnesia"

RUN apk add --no-cache tzdata
RUN echo "Asia/Jakarta" > /etc/timezone

RUN addgroup -S cmsnesia && adduser -S cmsnesia-web -G cmsnesia
USER cmsnesia-web:cmsnesia

WORKDIR /app

COPY --from=builder /workspace/cmsnesia-web/target/cmsnesia-web-*.jar /app/cmsnesia-web.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app/cmsnesia-web.jar"]