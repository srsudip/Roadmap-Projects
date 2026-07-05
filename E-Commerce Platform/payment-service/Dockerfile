FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY pom.xml ./
COPY payment-service/pom.xml payment-service/
RUN mvn dependency:go-offline -pl payment-service -am -B || true
COPY payment-service/src payment-service/src
RUN mvn clean package -pl payment-service -am -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/payment-service/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
