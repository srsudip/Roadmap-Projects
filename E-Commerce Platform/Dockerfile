# ═══════════════════════════════════════════════════════
# Unified Dockerfile — packages a pre-built jar for any service.
# Build the jars first (run.py does this automatically):
#   cd backend && mvn clean package -DskipTests
#   cd frontend/frontend-service && mvn clean package -DskipTests
# Then:
#   docker build --target development --build-arg SERVICE=<name> .
#   (frontend also needs --build-arg MODULE_DIR=frontend/frontend-service)
# Jars are built on the host: in-container Maven downloads hit
# Maven Central rate limits (HTTP 429) across 9 parallel builds.
# ═══════════════════════════════════════════════════════

ARG SERVICE=eureka-server

# ── Development target ───────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS development
ARG SERVICE
ARG MODULE_DIR=backend/${SERVICE}
WORKDIR /app

COPY ${MODULE_DIR}/target/*.jar app.jar

EXPOSE 3000 8080 8081 8082 8083 8084 8085 8086 8761
ENTRYPOINT ["java", "-jar", "app.jar"]

# ── Production target ────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS production
ARG SERVICE
ARG MODULE_DIR=backend/${SERVICE}
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY ${MODULE_DIR}/target/*.jar app.jar

RUN chown appuser:appgroup app.jar
USER appuser

EXPOSE 3000 8080 8081 8082 8083 8084 8085 8086 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
