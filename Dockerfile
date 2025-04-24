# Use uma imagem base do JDK
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

# Copie o arquivo pom.xml e baixe as dependências
COPY pom.xml . 
RUN mvn dependency:go-offline -B

# Copie o código-fonte e construa o projeto
COPY src ./src
RUN mvn clean package -DskipTests

# Use uma imagem base do JDK para a execução
FROM openjdk:17-jdk-slim

# Exponha a porta que a aplicação irá rodar
EXPOSE 5020

# Copie o JAR gerado para o contêiner
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar

# Execute a aplicação
ENTRYPOINT ["java", "-jar", "/app.jar"]