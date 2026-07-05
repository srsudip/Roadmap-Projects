FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY pom.xml ./
COPY eureka-server/pom.xml eureka-server/
RUN mvn dependency:go-offline -pl eureka-server -am -B || true
COPY eureka-server/src eureka-server/src
RUN mvn clean package -pl eureka-server -am -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/eureka-server/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
