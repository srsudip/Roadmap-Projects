ARG SERVICE=eureka-server

# ── Build stage ──────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build
ARG SERVICE
WORKDIR /app

# Copy parent POM and all module POMs (required for multi-module resolution)
COPY pom.xml ./
COPY eureka-server/pom.xml eureka-server/
COPY api-gateway/pom.xml api-gateway/
COPY user-service/pom.xml user-service/
COPY product-service/pom.xml product-service/
COPY cart-service/pom.xml cart-service/
COPY order-service/pom.xml order-service/
COPY payment-service/pom.xml payment-service/
COPY notification-service/pom.xml notification-service/

# Download dependencies (cached unless POMs change)
RUN mvn dependency:go-offline -pl ${SERVICE} -am -B || true

# Copy service source and build
COPY ${SERVICE}/src ${SERVICE}/src
RUN mvn clean package -pl ${SERVICE} -am -DskipTests -B

# ── Runtime stage ────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
ARG SERVICE
WORKDIR /app
COPY --from=build /app/${SERVICE}/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
