FROM eclipse-temurin:21-jdk-jammy
LABEL authors="ramad"
COPY build/libs/Movie-Recommendation-API-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]