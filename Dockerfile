# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy only the dependencies to take advantage of layer caching
COPY pom.xml .
RUN mvn -B -ntp -q -e -DskipTests dependency:go-offline

# Copy source code and build the jar
COPY src ./src
RUN mvn -B -ntp -DskipTests package

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Mount point inside the container for the H2 file DB
VOLUME ["/data"]

# Copy the jar
COPY --from=build /app/target/*-SNAPSHOT.jar /app/app.jar

# Expose the HTTP port
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -jar /app/app.jar"]