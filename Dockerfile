# Build da aplicação usando Maven e Amazon Corretto 21 Alpine
FROM maven:3.9.9-amazoncorretto-21-alpine AS builder
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM amazoncorretto:21-alpine3.21
WORKDIR /app

# Copia o JAR gerado (geralmente tem o nome do projeto ou versão)
COPY --from=builder /app/target/*.jar ./app.jar

RUN addgroup -S LDGroup && adduser -S HopefulUser -G LDGroup
USER HopefulUser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]