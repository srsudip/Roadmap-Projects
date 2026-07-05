FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY pom.xml ./
COPY cart-service/pom.xml cart-service/
RUN mvn dependency:go-offline -pl cart-service -am -B || true
COPY cart-service/src cart-service/src
RUN mvn clean package -pl cart-service -am -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/cart-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
