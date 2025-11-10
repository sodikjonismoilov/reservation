# ---------- Build stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy project files
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw
COPY src ./src

# Build (skip tests)
RUN ./mvnw -DskipTests package

# ---------- Run stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built jar
COPY --from=build /app/target/*-SNAPSHOT.jar /app/app.jar

# Render Docker web services must listen on $PORT (Render sets it)
EXPOSE 10000
ENV JAVA_OPTS=""

# Bind Spring Boot to the port Render provides
CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=$PORT -jar /app/app.jar"]