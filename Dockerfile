# Dockerfile for Spring Boot Application with Multi-Stage Build

# Step 1: Build the application using Maven
FROM maven:3.8.5-openjdk-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Step 2: Run the application with a JRE
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
