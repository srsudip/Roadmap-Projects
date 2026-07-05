FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY pom.xml ./
COPY user-service/pom.xml user-service/
RUN mvn dependency:go-offline -pl user-service -am -B || true
COPY user-service/src user-service/src
RUN mvn clean package -pl user-service -am -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/user-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
