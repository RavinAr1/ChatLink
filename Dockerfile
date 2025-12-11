# --- Build Stage ---
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy Maven wrapper and config
COPY mvnw .
COPY .mvn .mvn

# Copy project code
COPY pom.xml .
COPY src src

# Build Spring Boot app
RUN ./mvnw package -DskipTests

# --- Run Stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
