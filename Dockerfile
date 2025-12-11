# --- Build Stage ---
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy Maven wrapper and config
COPY mvnw .
COPY .mvn .mvn

# Copy project code
COPY pom.xml .
COPY src src

# Make mvnw executable
RUN chmod +x mvnw

# Build Spring Boot app
RUN ./mvnw package -DskipTests

# --- Run Stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Create uploads directory for attachments
RUN mkdir -p /app/uploads
VOLUME /app/uploads

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
