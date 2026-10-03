FROM node:22 AS frontend
WORKDIR /app
COPY riscv-frontend/ .
RUN npm ci && npm run build

FROM eclipse-temurin:25-jdk AS backend
WORKDIR /app
COPY riscv-backend/ .
COPY --from=frontend /app/dist src/main/resources/static
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=backend /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-Xmx350m", "-jar", "app.jar"]