# Votação API

API REST para gerenciamento de pautas, sessões de votação e apuração de resultados.

Projeto desenvolvido utilizando Java 21 e Spring Boot como solução para o desafio técnico da Sicredi.

---

# Tecnologias Utilizadas

- Java 21
- Spring Boot 3.2.2
- Spring Data JPA
- PostgreSQL
- Redis
- Flyway
- Maven
- Docker
- Swagger / OpenAPI
- Lombok

---

# Funcionalidades

## Pautas

- Criar pauta
- Consultar pauta

## Sessões

- Abrir sessão de votação
- Definir tempo customizado
- Utilizar tempo padrão de 1 minuto

## Votos

- Registrar voto SIM ou NÃO
- Validar CPF através de integração externa
- Impedir votos duplicados
- Impedir votação fora do período da sessão
- Fluxo de confirmação de voto

## Resultado

- Apuração dos votos
- Quantidade de votos SIM
- Quantidade de votos NÃO
- Resultado final da pauta

## Cache Redis

- Cache de sessão ativa
- Cache de resultado da votação

---

# Arquitetura

Estrutura baseada em camadas:

```text
controller
service
repository
entity
```

Organização do projeto:

```text
src/main/java/com/sicred/votacao

├── config
├── controller
├── dto
├── entity
├── exception
├── integration
├── repository
├── service

```

---

# Regras de Negócio

## Sessão

- Uma pauta pode possuir sessão de votação.
- A sessão deve estar ativa para receber votos.
- Sessões encerradas não aceitam novos votos.

## CPF

Validação realizada através de integração externa.

Possíveis retornos:

- ABLE_TO_VOTE
- UNABLE_TO_VOTE

CPF inválido gera erro de negócio.

## Votos

- Apenas SIM ou NÃO.
- Um associado pode votar apenas uma vez por pauta.
- Restrição garantida por regra de negócio e constraint no banco.

---

# Banco de Dados

## Flyway

As migrations são executadas automaticamente ao iniciar a aplicação.

Localização:

```text
src/main/resources/db/migration
```

## Tabelas

### pauta

```sql
id
titulo
```

### sessao_votacao

```sql
id
pauta_id
data_abertura
data_fechamento
```

### voto

```sql
id
pauta_id
identificador_associado
voto
data_voto
```

Constraint:

```sql
UNIQUE(pauta_id, identificador_associado)
```

Essa constraint garante que um associado vote apenas uma vez por pauta.

---

# Cache Redis

## Objetivo

Reduzir consultas repetidas ao banco em cenários de alta concorrência.

## Sessão Ativa

Chave:

```text
sessoes::{pautaId}
```

TTL:

```text
10 segundos
```

Método cacheado:

```java
buscarSessaoAtivaPorPauta()
```

## Resultado da Votação

Chave:

```text
resultados::{pautaId}
```

TTL:

```text
10 segundos
```

Método cacheado:

```java
buscarResultado()
```

## Estratégia

### Cacheable

```java
@Cacheable
```

Utilizado para:

- Sessão ativa
- Resultado

### CacheEvict

```java
@CacheEvict
```

Utilizado quando:

- Nova sessão é criada
- Resultado precisa ser recalculado

---

# Como Executar

## Pré-requisitos

- Java 21
- Maven
- Docker Desktop

---

# Docker

Subir PostgreSQL e Redis:

```bash
docker-compose up -d
```

Ou individualmente:

### PostgreSQL

```bash
docker run -d --name postgres-votacao \
-e POSTGRES_USER=postgres \
-e POSTGRES_PASSWORD=postgres \
-e POSTGRES_DB=votacao \
-p 5432:5432 \
postgres:16
```

### Redis

```bash
docker run -d --name redis-sicred -p 6379:6379 redis:7-alpine
```

---

# Configuração

Arquivo:

```text
src/main/resources/application.yml
```

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/votacao
    username: postgres
    password: postgres

  jpa:
    open-in-view: false
    hibernate:
      ddl-auto: none

  flyway:
    enabled: true

  data:
    redis:
      host: localhost
      port: 6379

  cache:
    type: redis

server:
  port: 8081
```

---

# Build

```bash
mvn clean package
```

---

# Executar

```bash
java -jar target/votacao-0.0.1-SNAPSHOT.jar
```

ou

```bash
mvn spring-boot:run
```

Aplicação disponível em:

```text
http://localhost:8081
```

---

# Swagger

Swagger UI:

```text
http://localhost:8081/swagger-ui.html
```

OpenAPI:

```text
http://localhost:8081/v3/api-docs
```

---

# Endpoints

## Criar Pauta

```http
POST /api/v1/pautas
```

Request:

```json
{
  "titulo": "Você aprova o projeto X1?"
}
```

---

## Abrir Sessão

```http
POST /api/v1/pautas/{id}/sessoes
```

Request:

```json
{
  "duracaoMinutos": 5
}
```

---

## Iniciar Fluxo de Voto

```http
POST /api/v1/pautas/{id}/votos
```

Request:

```json
{
  "identificadorAssociado": "45808438861",
  "voto": "SIM"
}
```

---

## Confirmar Voto

```http
POST /api/v1/pautas/{id}/votos/registrar
```

Request:

```json
{
  "identificadorAssociado": "45808438861",
  "voto": "SIM"
}
```

---

## Resultado

```http
GET /api/v1/pautas/{id}/resultado
```

Response:

```json
{
  "pautaId": 1,
  "titulo": "Você aprova o projeto X1?",
  "totalSim": 10,
  "totalNao": 5,
  "resultado": "APROVADA"
}
```

---

# Tratamento de Erros

## CPF inválido

```json
{
  "mensagem": "CPF inválido"
}
```

## Associado não habilitado

```json
{
  "mensagem": "Associado não habilitado para votar"
}
```

## Sessão encerrada

```json
{
  "mensagem": "Sessão encerrada"
}
```

## Voto duplicado

```json
{
  "mensagem": "Associado já votou nesta pauta"
}
```

Status HTTP:

```text
409 Conflict
```

---

# Testando o Cache Redis

Monitorar Redis:

```bash
docker exec -it redis-sicred redis-cli MONITOR
```

Executar consultas repetidas:

```bash
curl http://localhost:8081/api/v1/pautas/1/resultado
```

Primeira chamada:

```text
MISS
```

Próximas chamadas:

```text
HIT
```

---

# Comandos Úteis

Ver containers:

```bash
docker ps
```

Logs PostgreSQL:

```bash
docker logs -f postgres-votacao
```

Logs Redis:

```bash
docker logs -f redis-sicred
```

Entrar no Redis:

```bash
docker exec -it redis-sicred redis-cli
```

Limpar cache:

```bash
FLUSHALL
```

Parar containers:

```bash
docker-compose down
```

---

# Diferenciais Implementados

- Integração externa para validação de CPF
- Redis Cache
- Flyway
- Swagger/OpenAPI
- Tratamento global de exceções
- Constraint para voto único
- Fluxo de confirmação de voto
- Versionamento de API (/api/v1)
- Docker
- PostgreSQL
- Logs de negócio
- Cache de resultado
- Cache de sessão ativa

---

# Autor

Projeto desenvolvido como solução para o desafio técnico da Sicredi utilizando Java 21, Spring Boot, PostgreSQL e Redis.