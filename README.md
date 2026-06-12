# Votacao API

Projeto de teste técnico - API de votação

Requisitos mínimos:
- Java 21
- Spring Boot 3
- PostgreSQL + Flyway

Como rodar:

1. Inicie o PostgreSQL via Docker Compose:

   docker compose up -d

2. Build e execute a aplicação (ou rode via IDE):

   mvn clean package
   java -jar target/votacao-0.0.1-SNAPSHOT.jar

3. Documentação Swagger disponível em:

   http://localhost:8081/swagger-ui.html

Observações:
- As migrations Flyway estão em `src/main/resources/db/migration`
- Variáveis de conexão em `src/main/resources/application.yml`
