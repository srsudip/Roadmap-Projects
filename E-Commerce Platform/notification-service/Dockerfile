FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY pom.xml ./
COPY notification-service/pom.xml notification-service/
RUN mvn dependency:go-offline -pl notification-service -am -B || true
COPY notification-service/src notification-service/src
RUN mvn clean package -pl notification-service -am -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/notification-service/target/*.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]
