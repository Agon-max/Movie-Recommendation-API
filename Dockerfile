FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app
LABEL authors="ramad"

COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
COPY src ./src

RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
