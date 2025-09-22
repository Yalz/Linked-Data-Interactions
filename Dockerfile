# Stage 1: Build React frontend
FROM node:18-alpine AS frontend-builder

WORKDIR /app
COPY ldio-core/frontend/package*.json ./
RUN npm install
COPY ldio-core/frontend ./
RUN npm run build

# Stage 2: Build Micronaut backend using gradlew
FROM eclipse-temurin:24-jdk-alpine AS backend-builder

WORKDIR /app

# Copy backend source and gradlew
COPY . .

# Ensure gradlew is executable
RUN chmod +x gradlew

# Copy frontend build into Micronaut static resources
COPY --from=frontend-builder /app/dist/ ldio-core/src/main/resources/static/
COPY --from=frontend-builder /app/dist/assets/ ldio-core/src/main/resources/static/assets/

# Build fat JAR using shadowJar
RUN ./gradlew :ldio-core:shadowJar --no-daemon

# Stage 3: Final runtime image
FROM eclipse-temurin:24-jre-alpine

WORKDIR /app

# Copy the fat JAR from the builder stage
COPY --from=backend-builder /app/ldio-core/build/libs/ldio-core-*-all.jar ./app.jar

EXPOSE 8080
CMD ["java", "-Dmicronaut.environments=prod", "-jar", "app.jar"]
