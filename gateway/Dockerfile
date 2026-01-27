FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY . .

RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/gateway/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
