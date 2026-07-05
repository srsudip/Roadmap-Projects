FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY pom.xml ./
COPY api-gateway/pom.xml api-gateway/
RUN mvn dependency:go-offline -pl api-gateway -am -B || true
COPY api-gateway/src api-gateway/src
RUN mvn clean package -pl api-gateway -am -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/api-gateway/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
