# Build stage: Use Gradle with JDK 17
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app

# Copy Gradle wrapper files
COPY gradlew gradlew.bat build.gradle ./

# Copy Gradle directory (for wrapper)
COPY gradle ./gradle

# Copy the source code and resources
COPY src ./src

# Build the application
RUN ./gradlew clean bootJar --no-daemon

# Runtime stage: Use lightweight JRE 17
FROM openjdk:17-jdk-slim AS runtime
WORKDIR /app

# Copy the built application JAR from the builder stage
COPY --from=builder /app/build/libs/online-course-management-system-0.1.0.jar app.jar

# Expose the application port
EXPOSE 8080

# Default command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]