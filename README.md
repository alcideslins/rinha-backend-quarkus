# Rinha APP

Desenvolvida para participar da [Rinha de Backend - 2024/Q1](https://github.com/zanfranceschi/rinha-de-backend-2024-q1)

## Stack

- **Linguagem:** Java 21
- **Framework:** Quarkus com JDBC
- **Banco de dados:** PostgreSQL
- **Load Balancer:** NGINX

## Docker

### Construção da Imagem

Para construir a imagem Docker, utilize o seguinte comando:

```bash
./mvnw package -Dnative -DskipTests -Dquarkus.native.container-build=true
docker build -f src/main/docker/Dockerfile.native-micro -t rinha-app-quarkus .
```
