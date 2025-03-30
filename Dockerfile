# Stage 1: Build the application
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre
WORKDIR /opt
ENV PORT=8080
EXPOSE 8080
COPY --from=build /app/target/*.jar /opt/app.jar
ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-jar", "app.jar"]