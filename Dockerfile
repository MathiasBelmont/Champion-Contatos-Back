# Estágio 1: Build do pacote .jar usando o Gradle
FROM gradle:8.5-jdk17-alpine AS build
WORKDIR /app
COPY . .
# O --no-daemon ajuda a economizar memória durante o build no Docker
RUN ./gradlew bootJar --no-daemon

# Estágio 2: Execução da aplicação
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]