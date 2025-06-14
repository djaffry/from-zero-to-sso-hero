FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY . .
RUN chmod +x ./gradlew && ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
COPY start.sh /app/start.sh

RUN apt-get update && apt-get install -y socat && \
    chmod +x /app/start.sh && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

EXPOSE 8071 8080
ENTRYPOINT ["/app/start.sh"]
