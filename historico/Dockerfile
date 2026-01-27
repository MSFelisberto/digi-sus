# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia toda a raiz do projeto
COPY . .

# Build de todos os módulos
RUN mvn clean install -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copia o JAR final do historico
COPY --from=build /app/historico/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
