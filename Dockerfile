# Estágio de Build (Compilação)
FROM maven:3.9.6-eclipse-temurin-21-jammy AS build
COPY . .
RUN mvn clean package -DskipTests

# Estágio de Runtime (Execução)
FROM eclipse-temurin:21-jre-jammy
COPY --from=build /target/*.jar app.jar
EXPOSE 8520
ENTRYPOINT ["java", "-jar", "app.jar"]