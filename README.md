# 🎯 MyVisionBoard API

Uma API RESTful moderna para gerenciar seu quadro de visão pessoal, permitindo criar, organizar e categorizar notas com autenticação segura baseada em JWT.

**Versão:** 0.0.1-SNAPSHOT | **Java:** 21 | **Spring Boot:** 4.0.3

---

## 📋 Sumário

- [Visão Geral](#visão-geral)
- [Tecnologias](#tecnologias)
- [Pré-requisitos](#pré-requisitos)
- [Instalação](#instalação)
- [Configuração](#configuração)
- [Como Executar](#como-executar)
- [Endpoints da API](#endpoints-da-api)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Modelo de Dados](#modelo-de-dados)
- [Autenticação](#autenticação)
- [Rate Limiting](#rate-limiting)
- [Documentação Interativa](#documentação-interativa)
- [Docker](#docker)
- [Contribuindo](#contribuindo)

---

## 🎨 Visão Geral

MyVisionBoard é uma plataforma que permite aos usuários:

- 📝 **Criar e gerenciar notas** - Escreva, edite e organize suas ideias
- 🏷️ **Organizar com tags** - Categorize suas notas para melhor busca e recuperação
- 👤 **Autenticação segura** - Sistema de login/registro com JWT tokens
- ⚡ **Rate limiting** - Proteção contra abuso de API
- 🔄 **Caching Redis** - Melhor performance com cache distribuído
- 🔐 **Spring Security** - Segurança em nível de aplicação

---

## 🛠️ Tecnologias

### Backend
- **Framework:** Spring Boot 4.0.3
- **Linguagem:** Java 21
- **Build Tool:** Maven

### Banco de Dados
- **PostgreSQL 16** - Armazenamento relacional de dados
- **Redis 7** - Caching e performance

### Segurança
- **Spring Security** - Autenticação e autorização
- **JWT (JJWT 0.12.6)** - Token-based authentication
- **Lombok** - Redução de boilerplate code

### Documentação
- **SpringDoc OpenAPI 2.8.6** - Documentação Swagger/OpenAPI
- **Swagger UI** - Interface interativa

### Validação
- **Spring Boot Validation** - Bean validation com Hibernate Validator

---

## 📦 Pré-requisitos

Antes de começar, você precisa ter instalado em sua máquina:

- ✅ **Java 21** ou superior
- ✅ **Maven 3.9** ou superior
- ✅ **Docker** e **Docker Compose** (opcional, para ambiente containerizado)
- ✅ **PostgreSQL 16** (ou via Docker)
- ✅ **Redis 7** (ou via Docker)

### Verificar Instalações

```bash
# Verificar Java
java -version

# Verificar Maven
mvn -version
```

---

## 💻 Instalação

### 1. Clonar o Repositório

```bash
git clone <seu-repositorio>
cd myvisionboard
```

### 2. Instalar Dependências

```bash
mvn clean install
```

---

## ⚙️ Configuração

### Arquivo: `application.properties`

O arquivo de configuração está localizado em `src/main/resources/application.properties`:

```properties
# Aplicação
spring.application.name=myvisionboard
server.port=8080

# Banco de Dados PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/myvisionboard
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA e Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Redis Cache
spring.data.redis.host=localhost
spring.data.redis.port=6379

# JWT Token
jwt.secret=myvisionboard-secret-key-troque-por-uma-chave-segura
jwt.expiration=86400000  # 24 horas em ms

# Rate Limiting
rate.limit.requests=10
rate.limit.duration=60  # segundos

# Swagger UI
springdoc.swagger-ui.path=/swagger-ui
```

### ⚠️ Configurações Importantes

Antes de colocar em produção, altere:

1. **JWT Secret** - Use uma chave segura em produção
2. **Credenciais do Banco** - Altere username e password
3. **Rate Limiting** - Ajuste conforme necessário
4. **DDL Auto** - Mude para `validate` em produção

---

## 🚀 Como Executar

### Opção 1: Sem Docker (Local)

#### Passo 1: Iniciar PostgreSQL e Redis

```bash
# PostgreSQL (macOS com Homebrew)
brew install postgresql redis
brew services start postgresql
brew services start redis

# Ou instale manualmente conforme sua OS
```

#### Passo 2: Executar a Aplicação

```bash
# Via Maven
mvn spring-boot:run

# A aplicação estará disponível em: http://localhost:8080
```

### Opção 2: Com Docker Compose

#### Passo 1: Iniciar os Serviços

```bash
docker-compose up -d
```

Isso iniciará:
- PostgreSQL na porta 5432
- Redis na porta 6379

#### Passo 2: Build e Executar a Aplicação

```bash
# Build da aplicação
mvn clean package

# Executar com Java
java -jar target/myvisionboard-0.0.1-SNAPSHOT.jar
```

#### Verificar Logs

```bash
docker-compose logs -f
```

#### Parar os Serviços

```bash
docker-compose down
```

---

## 🔌 Endpoints da API

A API segue o padrão RESTful. Todos os endpoints (exceto autenticação) requerem autenticação via JWT.

### 🔐 Autenticação (Auth) - `/auth`

#### 1. Registrar Novo Usuário
```http
POST /auth/register
Content-Type: application/json

{
  "name": "João Silva",
  "email": "joao@example.com",
  "password": "SenhaSegura123!"
}
```

**Resposta (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "joao@example.com",
  "name": "João Silva"
}
```

#### 2. Fazer Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "joao@example.com",
  "password": "SenhaSegura123!"
}
```

**Resposta (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "joao@example.com",
  "name": "João Silva"
}
```

---

### 📝 Notas (Notes) - `/notes`

#### 1. Listar Notas com Paginação
```http
GET /notes?userId=123e4567-e89b-12d3-a456-426614174000&page=0&size=10
Authorization: Bearer {token}
```

**Resposta (200 OK):**
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "title": "Meu Objetivo 2026",
      "content": "Descrição detalhada do meu objetivo...",
      "tags": [
        {
          "id": "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
          "name": "Carreira"
        }
      ],
      "createdAt": "2026-03-18T10:30:00",
      "updatedAt": "2026-03-18T10:30:00"
    }
  ],
  "totalElements": 25,
  "totalPages": 3,
  "currentPage": 0
}
```

#### 2. Buscar Notas por Título
```http
GET /notes?userId=123e4567-e89b-12d3-a456-426614174000&title=objetivo
Authorization: Bearer {token}
```

#### 3. Obter Nota Específica
```http
GET /notes/{noteId}
Authorization: Bearer {token}
```

**Resposta (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Meu Objetivo 2026",
  "content": "Descrição detalhada...",
  "tags": [...],
  "createdAt": "2026-03-18T10:30:00",
  "updatedAt": "2026-03-18T10:30:00"
}
```

#### 4. Criar Nova Nota
```http
POST /notes
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Nova Nota",
  "content": "Conteúdo da nota",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "tags": [
    {
      "id": "6ba7b810-9dad-11d1-80b4-00c04fd430c8"
    }
  ]
}
```

**Resposta (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Nova Nota",
  "content": "Conteúdo da nota",
  "tags": [...],
  "createdAt": "2026-03-18T10:30:00",
  "updatedAt": "2026-03-18T10:30:00"
}
```

#### 5. Atualizar Nota
```http
PUT /notes/{noteId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Nota Atualizada",
  "content": "Novo conteúdo",
  "tags": [...]
}
```

**Resposta (200 OK):** Nota atualizada

#### 6. Deletar Nota
```http
DELETE /notes/{noteId}
Authorization: Bearer {token}
```

**Resposta (204 No Content)**

---

### 🏷️ Tags (Tags) - `/tags`

#### 1. Listar Todas as Tags
```http
GET /tags
Authorization: Bearer {token}
```

**Resposta (200 OK):**
```json
[
  {
    "id": "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
    "name": "Carreira",
    "notes": [...]
  },
  {
    "id": "6ba7b811-9dad-11d1-80b4-00c04fd430c8",
    "name": "Saúde",
    "notes": [...]
  }
]
```

#### 2. Obter Tag Específica
```http
GET /tags/{tagId}
Authorization: Bearer {token}
```

#### 3. Criar Nova Tag
```http
POST /tags
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Educação"
}
```

**Resposta (200 OK):** Tag criada

#### 4. Atualizar Tag
```http
PUT /tags/{tagId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Desenvolvimento"
}
```

#### 5. Deletar Tag
```http
DELETE /tags/{tagId}
Authorization: Bearer {token}
```

**Resposta (204 No Content)**

---

### 👤 Usuário (User) - `/user`

#### 1. Obter Perfil Autenticado
```http
GET /user/me?email=joao@example.com
Authorization: Bearer {token}
```

**Resposta (200 OK):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "João Silva",
  "email": "joao@example.com",
  "notes": [...],
  "createdAt": "2026-03-18T10:30:00",
  "updatedAt": "2026-03-18T10:30:00"
}
```

#### 2. Atualizar Perfil
```http
PUT /user/me?email=joao@example.com
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "João Silva Atualizado"
}
```

**Resposta (200 OK):** Usuário atualizado

#### 3. Deletar Conta
```http
DELETE /user/me?email=joao@example.com
Authorization: Bearer {token}
```

**Resposta (204 No Content)**

---

## 📐 Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/myvisionboard/app/
│   │   ├── Application.java                 # Classe principal Spring Boot
│   │   ├── config/                          # Configurações
│   │   │   ├── RateLimitConfig.java        # Limitação de taxa
│   │   │   ├── RedisConfig.java            # Configuração Redis
│   │   │   ├── SwaggerConfig.java          # Configuração OpenAPI
│   │   │   └── WebConfig.java              # Configuração Web
│   │   ├── controller/                      # REST Controllers
│   │   │   ├── AuthController.java         # Login e Registro
│   │   │   ├── NoteController.java         # Operações de Notas
│   │   │   ├── TagController.java          # Operações de Tags
│   │   │   └── UserController.java         # Perfil do Usuário
│   │   ├── dto/                             # Data Transfer Objects
│   │   │   ├── request/                    # DTOs de Requisição
│   │   │   └── response/                   # DTOs de Resposta
│   │   ├── model/                           # Entidades JPA
│   │   │   ├── Note.java                   # Modelo de Nota
│   │   │   ├── Tag.java                    # Modelo de Tag
│   │   │   └── User.java                   # Modelo de Usuário
│   │   ├── repository/                      # Spring Data Repositories
│   │   │   ├── NoteRepository.java
│   │   │   ├── TagRepository.java
│   │   │   └── UserRepository.java
│   │   ├── security/                        # Segurança e JWT
│   │   │   ├── CustomUserDetailsService.java
│   │   │   ├── JwtAuthFilter.java
│   │   │   ├── JwtService.java
│   │   │   └── SecurityConfig.java
│   │   └── service/                         # Lógica de Negócio
│   │       ├── AuthService.java
│   │       ├── NoteService.java
│   │       ├── TagService.java
│   │       └── UserService.java
│   └── resources/
│       └── application.properties           # Configurações
├── test/java/...                            # Testes Unitários
pom.xml                                      # Dependências Maven
docker-compose.yaml                          # Orquestração Docker
dockerfile                                   # Build Docker
```

---

## 🗄️ Modelo de Dados

### Tabela: `users`
| Campo | Tipo | Descrição |
|-------|------|-----------|
| id | UUID | Identificador único (gerado automaticamente) |
| name | VARCHAR(255) | Nome do usuário |
| email | VARCHAR(255) | Email único do usuário |
| password | VARCHAR(255) | Senha criptografada |
| created_at | TIMESTAMP | Data de criação |
| updated_at | TIMESTAMP | Data da última atualização |

### Tabela: `notes`
| Campo | Tipo | Descrição |
|-------|------|-----------|
| id | UUID | Identificador único |
| title | VARCHAR(255) | Título da nota |
| content | TEXT | Conteúdo da nota |
| user_id | UUID | Referência ao usuário |
| created_at | TIMESTAMP | Data de criação |
| updated_at | TIMESTAMP | Data da última atualização |

### Tabela: `tags`
| Campo | Tipo | Descrição |
|-------|------|-----------|
| id | UUID | Identificador único |
| name | VARCHAR(255) | Nome único da tag |

### Tabela: `note_tags` (Junção)
| Campo | Tipo | Descrição |
|-------|------|-----------|
| note_id | UUID | Referência à nota |
| tag_id | UUID | Referência à tag |

### Relacionamentos
```
User (1) ──── (N) Note
Tag  (N) ──── (N) Note (muitos para muitos)
```

---

## 🔐 Autenticação

### Sistema JWT (JSON Web Token)

A API utiliza JWT para autenticação stateless:

1. **Login/Registro:** Cliente envia credenciais
2. **Token Gerado:** Servidor retorna JWT assinado
3. **Requisições:** Cliente inclui token no header `Authorization: Bearer {token}`
4. **Validação:** Servidor valida assinatura e expiração

### Header de Autenticação

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

### Duração do Token

- **Expiração:** 24 horas (86400000 ms)
- Configurável em `jwt.expiration`

---

## ⚡ Rate Limiting

### Proteção contra Abuso

A API implementa rate limiting para proteger contra abuso:

- **Limite:** 10 requisições
- **Janela:** 60 segundos
- **Status:** 429 (Too Many Requests) quando excedido

### Configuração

```properties
rate.limit.requests=10
rate.limit.duration=60
```

---

## 📖 Documentação Interativa

### Swagger UI

Acesse a documentação interativa em:

```
http://localhost:8080/swagger-ui
```

### OpenAPI JSON

O arquivo OpenAPI está disponível em:

```
http://localhost:8080/v3/api-docs
```

Você pode usar ferramentas como Postman, Insomnia ou cURL para testar os endpoints.

---

## 🐳 Docker

### Dockerfile

```dockerfile
# Build Stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/myvisionboard-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose

```yaml
services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: myvisionboard
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"

  redis:
    image: redis:7
    ports:
      - "6379:6379"
```

### Build e Deploy Docker

```bash
# Build da imagem
docker build -t myvisionboard:latest .

# Executar container
docker run -d \
  --name myvisionboard \
  -p 8080:8080 \
  --link myvisionboard-postgres \
  --link myvisionboard-redis \
  myvisionboard:latest

# Com docker-compose
docker-compose up -d
```

---

## 🛠️ Desenvolvimento

### Dependências Principais

```xml
<!-- Spring Boot Web -->
<spring-boot-starter-web>

<!-- Spring Security -->
<spring-boot-starter-security>

<!-- Spring Data JPA -->
<spring-boot-starter-data-jpa>

<!-- Spring Data Redis -->
<spring-boot-starter-data-redis>

<!-- JWT -->
<jjwt>

<!-- PostgreSQL -->
<postgresql>

<!-- Lombok -->
<lombok>

<!-- SpringDoc OpenAPI -->
<springdoc-openapi-starter-webmvc-ui>
```

### Build da Aplicação

```bash
# Compile e empacote
mvn clean package

# Pular testes
mvn clean package -DskipTests

# Apenas compilar
mvn clean compile
```

### Executar Testes

```bash
# Todos os testes
mvn test

# Testes específicos
mvn test -Dtest=NoteServiceTest
```

### IDE Recomendadas

- **IntelliJ IDEA Community** (recomendado para Java/Spring)
- **Visual Studio Code** com extensões Java
- **Eclipse IDE**

---

## 🤝 Contribuindo

### Fluxo de Contribuição

1. **Fork** o repositório
2. **Crie** uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. **Commit** suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. **Push** para a branch (`git push origin feature/MinhaFeature`)
5. **Abra** um Pull Request

### Padrões de Código

- Siga as convenções Java
- Use nomes descritivos em português ou inglês
- Adicione JavaDoc para métodos públicos
- Escreva testes para novas features
- Mantenha as classes pequenas e focadas

---

## 📝 Licença

Este projeto está sob licença MIT. Veja o arquivo LICENSE para mais detalhes.

---

## 📧 Suporte

Para dúvidas ou problemas:

- Abra uma **Issue** no repositório
- Envie um **Email** para suporte@example.com
- Consulte a **Documentação Swagger** em `/swagger-ui`

---

## 🔧 Troubleshooting

### Problema: Connection refused para PostgreSQL

**Solução:**
```bash
# Verifique se PostgreSQL está rodando
brew services list

# Se não estiver, inicie
brew services start postgresql
```

### Problema: Redis Connection refused

**Solução:**
```bash
# Verifique e inicie Redis
brew services start redis
```

### Problema: Porta 8080 já em uso

**Solução:**
```bash
# Altere em application.properties
server.port=8081

# Ou mate o processo que está usando a porta
lsof -i :8080
kill -9 <PID>
```

### Problema: JWT Invalid

**Solução:**
- Verifique se o token não expirou (24 horas)
- Verifique se está incluindo `Bearer ` antes do token
- Verifique se a secret key está correta

---

## 📚 Referências

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [JWT Introduction](https://jwt.io)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Redis Documentation](https://redis.io/documentation)
- [OpenAPI/Swagger](https://swagger.io/)

---

**Última Atualização:** 18 de Março de 2026

**Desenvolvido com ❤️ usando Spring Boot e Java 21**

