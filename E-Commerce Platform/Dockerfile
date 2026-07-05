ARG SERVICE=eureka-server

# ═══════════════════════════════════════════════════════
# Unified Dockerfile — builds any frontend or backend service
# Usage: docker build --target development --build-arg SERVICE=<name> .
#        docker build --target production  --build-arg SERVICE=<name> .
# ═══════════════════════════════════════════════════════

# ── Build stage (shared) ─────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build
ARG SERVICE
WORKDIR /app

COPY backend/pom.xml backend/pom.xml
COPY backend/eureka-server/pom.xml backend/eureka-server/
COPY backend/api-gateway/pom.xml backend/api-gateway/
COPY backend/user-service/pom.xml backend/user-service/
COPY backend/product-service/pom.xml backend/product-service/
COPY backend/cart-service/pom.xml backend/cart-service/
COPY backend/order-service/pom.xml backend/order-service/
COPY backend/payment-service/pom.xml backend/payment-service/
COPY backend/notification-service/pom.xml backend/notification-service/
COPY frontend/frontend-service/pom.xml frontend/frontend-service/pom.xml

RUN cd backend && mvn dependency:go-offline -B || true
RUN cd frontend/frontend-service && mvn dependency:go-offline -B || true

COPY backend/ backend/
COPY frontend/frontend-service/ frontend/frontend-service/

RUN if [ "$SERVICE" = "frontend" ]; then \
      cd frontend/frontend-service && mvn clean package -DskipTests -B; \
      cp target/*.jar /app/app.jar; \
    else \
      cd backend && mvn clean package -pl ${SERVICE} -am -DskipTests -B; \
      cp ${SERVICE}/target/*.jar /app/app.jar; \
    fi

# ── Development target ───────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS development
ARG SERVICE
WORKDIR /app

COPY --from=build /app/app.jar app.jar

EXPOSE 3000 8080 8081 8082 8083 8084 8085 8086 8761
ENTRYPOINT ["java", "-jar", "app.jar"]

# ── Production target ────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS production
ARG SERVICE
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=build /app/app.jar app.jar

RUN chown appuser:appgroup app.jar
USER appuser

EXPOSE 3000 8080 8081 8082 8083 8084 8085 8086 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
