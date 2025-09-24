# Build stage
FROM gradle:8-jdk17 AS builder
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test --no-daemon

# Runtime stage
FROM openjdk:17-slim
WORKDIR /app
COPY --from=builder /app/build/libs/ ./libs/
RUN mv libs/springboot-gamez24-*[!plain].jar app.jar && rm -rf libs/

# Add some debugging
RUN ls -la app.jar
ENTRYPOINT ["sh", "-c", "echo 'PORT is set to: '$PORT && java -jar /app/app.jar"]