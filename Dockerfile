# ===== Build stage =====
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache deps first
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Build
COPY src ./src
RUN mvn -q -DskipTests clean package

# ===== Runtime stage =====
FROM eclipse-temurin:21-jre
WORKDIR /app

# (optional) run as non-root
RUN useradd -r -u 1001 appuser
USER appuser

# Copy fat jar
COPY --from=build /app/target/*.jar app.jar

# Expose HTTP
EXPOSE 8080

# Tunable JVM flags
ENV JAVA_OPTS="\
 -XX:MaxRAMPercentage=75.0 \
 -Djava.security.egd=file:/dev/./urandom \
"

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]