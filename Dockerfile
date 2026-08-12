# --- Stage 1: build the jar inside the image so hosts like Render/Railway can
#     build straight from source with no local Maven needed ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Cache dependencies first, then build.
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# --- Stage 2: lightweight runtime image ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the fat jar built in stage 1.
COPY --from=build /build/target/*.jar app.jar

# Expose default Spring Boot port
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
