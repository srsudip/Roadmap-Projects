FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY pom.xml ./
COPY order-service/pom.xml order-service/
RUN mvn dependency:go-offline -pl order-service -am -B || true
COPY order-service/src order-service/src
RUN mvn clean package -pl order-service -am -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/order-service/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
