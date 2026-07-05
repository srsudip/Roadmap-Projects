FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY pom.xml ./
COPY product-service/pom.xml product-service/
RUN mvn dependency:go-offline -pl product-service -am -B || true
COPY product-service/src product-service/src
RUN mvn clean package -pl product-service -am -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/product-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
