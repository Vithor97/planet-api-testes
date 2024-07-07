
# SW Planet API

Este projeto é uma API para gerenciamento de planetas do universo de Star Wars, utilizando Spring Boot e MySQL.

## Pré-requisitos

- Docker
- Docker Compose

## Configuração do Banco de Dados

### Docker Compose

Para configurar o banco de dados MySQL utilizando Docker, certifique-se de que você possui os seguintes arquivos na raiz do projeto:

#### `docker-compose.yml`

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:latest
    container_name: mysql_container
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql

volumes:
  mysql_data:
```

#### `init.sql`

```sql
CREATE DATABASE IF NOT EXISTS starwars;
CREATE USER IF NOT EXISTS 'myuser'@'%' IDENTIFIED BY 'mypassword';
GRANT ALL PRIVILEGES ON starwars.* TO 'myuser'@'%';
FLUSH PRIVILEGES;
```

## Instruções para Configuração

### 1. Subir o Contêiner MySQL

Navegue até o diretório `docker` onde estão os arquivos `docker-compose.yml` e `init.sql` e execute o comando abaixo para subir o contêiner do MySQL:

```sh
docker-compose up -d
```

### 2. Verificar se o Contêiner Está Rodando

Verifique se o contêiner do MySQL está rodando com o comando:

```sh
docker ps
```

### 3. Verificar os Logs do Contêiner

Verifique os logs do contêiner para garantir que o script de inicialização foi executado corretamente:

```sh
docker logs mysql_container
```

Você deve ver uma mensagem indicando que o banco de dados foi criado e que as permissões foram concedidas.

### 4. **Atenção: O Banco de Dados Deve Ser Criado Primeiro**

Certifique-se de que o banco de dados `starwars` foi criado antes de iniciar a aplicação Spring Boot. O script `init.sql` providenciará a criação do banco de dados e do usuário com as permissões corretas.

## Acessar o Banco de Dados MySQL via Docker

Para acessar o banco de dados MySQL rodando no contêiner Docker, utilize o comando abaixo:

```sh
docker exec -it mysql_container mysql -u myuser -pmypassword
```

Depois de acessar o MySQL, você pode verificar os bancos de dados e usuários com os seguintes comandos:

```sql
SHOW DATABASES;
SELECT User, Host FROM mysql.user;
```

## Configuração do Spring Boot

### `application.properties`

Certifique-se de que o arquivo `application.properties` está configurado corretamente para se conectar ao banco de dados MySQL:

```properties
spring.application.name=sw-planet-api

# Configurações do MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/starwars?useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=myuser
spring.datasource.password=mypassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Configurações de JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

### Iniciar a Aplicação Spring Boot

Após garantir que o banco de dados está configurado corretamente e está rodando, inicie sua aplicação Spring Boot com o comando:

```sh
./mvnw spring-boot:run
```

O Hibernate criará as tabelas necessárias no banco de dados `starwars` se elas não existirem.

## Verificação
Verifique os logs da aplicação para confirmar que a conexão com o banco de dados foi estabelecida com sucesso e que as tabelas foram criadas.
