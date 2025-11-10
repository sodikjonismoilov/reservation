
# Use a lightweight Java runtime
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy Maven wrapper and project files
COPY . .

# Build the app (skip tests for faster build)
RUN ./mvnw -DskipTests package

# Expose port 8080 for Render
EXPOSE 8080

# Run the app
CMD ["java", "-jar", "target/reservation-0.0.1-SNAPSHOT.jar"]