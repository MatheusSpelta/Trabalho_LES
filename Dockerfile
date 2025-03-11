# Use uma imagem base do JDK
FROM openjdk:17-jdk-slim

# Adicione um volume apontando para /tmp
VOLUME /tmp

# Adicione o jar da aplicação
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# Exponha a porta que a aplicação irá rodar
EXPOSE 5020

# Execute a aplicação
ENTRYPOINT ["java", "-jar", "/app.jar"]