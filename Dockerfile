# Multi-stage build for Spring Boot backend

FROM gradle:8.10.2-jdk21 AS build
WORKDIR /app

# Copy Gradle wrapper and build files first for caching
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

# Copy source
COPY src ./src

# Build the application (bootJar)
RUN gradle --no-daemon clean bootJar

# Runtime image
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built jar from the previous stage
COPY --from=build /app/build/libs/*.jar /app/app.jar

EXPOSE 8080

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]


