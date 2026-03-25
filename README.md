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
- [Documentação Interativa](#documentação-interativa)
- [Docker](#docker)
- [Desenvolvimento](#desenvolvimento)
- [Contribuindo](#contribuindo)

---

## 🎨 Visão Geral

MyVisionBoard é uma plataforma que permite aos usuários:

- 📝 **Criar e gerenciar notas** — Escreva, edite e organize suas ideias com paginação e busca por título
- 🏷️ **Organizar com tags** — Categorize suas notas com etiquetas reutilizáveis
- 👤 **Autenticação segura** — Sistema de login/registro com JWT tokens stateless
- 🔐 **Spring Security** — Segurança em nível de aplicação com BCrypt
- 🔄 **Redis** — Infraestrutura de cache distribuído configurada e pronta
- 📖 **Swagger/OpenAPI** — Documentação interativa disponível em `/swagger-ui`

---

## 🛠️ Tecnologias

### Backend
| Tecnologia | Versão |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.3 |
| Maven | 3.9 |

### Banco de Dados e Cache
| Tecnologia | Versão | Uso |
|---|---|---|
| PostgreSQL | 16 | Armazenamento relacional principal |
| Redis | 7 | Cache distribuído |

### Segurança
- **Spring Security** — Autenticação e autorização
- **JJWT 0.12.6** — Geração e validação de tokens JWT
- **BCrypt** — Criptografia de senhas

### Documentação
- **SpringDoc OpenAPI 2.8.6** — Geração automática da especificação OpenAPI
- **Swagger UI** — Interface interativa de teste

### Utilitários
- **Lombok** — Redução de boilerplate
- **Spring Boot Validation** — Bean Validation (JSR-380)

---

## 📦 Pré-requisitos

- ✅ **Java 21** ou superior
- ✅ **Maven 3.9** ou superior
- ✅ **Docker** e **Docker Compose** (recomendado para PostgreSQL e Redis)
- ✅ **PostgreSQL 16** (local ou via Docker)
- ✅ **Redis 7** (local ou via Docker)

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
cd MyVisionBoard
```

### 2. Instalar Dependências

```bash
mvn clean install -DskipTests
```

---

## ⚙️ Configuração

### Arquivo: `src/main/resources/application.properties`

```properties
# Aplicação
spring.application.name=myvisionboard
server.port=8080
server.servlet.encoding.charset=UTF-8
server.servlet.encoding.enabled=true
server.servlet.encoding.force=true

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

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# JWT
jwt.secret=myvisionboard-secret-key-troque-por-uma-chave-segura
jwt.expiration=86400000   # 24 horas em milissegundos

# Swagger UI
springdoc.swagger-ui.path=/swagger-ui
springdoc.swagger-ui.tags-sorter=alpha
springdoc.writer-with-default-pretty-printer=true
springdoc.swagger-ui.disable-swagger-default-url=true
```

### ⚠️ Configurações Importantes para Produção

| Propriedade | Ação recomendada |
|---|---|
| `jwt.secret` | Substitua por uma chave forte e aleatória (mínimo 256 bits) |
| `spring.datasource.password` | Use credenciais seguras |
| `spring.jpa.hibernate.ddl-auto` | Altere para `validate` |
| `spring.jpa.show-sql` | Defina como `false` |
| `logging.level.org.springframework` | Reduza para `WARN` ou `ERROR` |

---

## 🚀 Como Executar

### Opção 1: Docker Compose (recomendado)

Suba o PostgreSQL e o Redis com um único comando:

```bash
docker-compose up -d
```

Em seguida, execute a aplicação:

```bash
mvn spring-boot:run
```

A aplicação estará disponível em: **http://localhost:8080**

### Opção 2: Serviços Locais

Instale e inicie PostgreSQL e Redis manualmente conforme seu sistema operacional, então:

```bash
mvn spring-boot:run
```

### Verificar Logs do Docker

```bash
docker-compose logs -f
```

### Parar os Serviços Docker

```bash
docker-compose down
```

---

## 🔌 Endpoints da API

> Todos os endpoints — exceto `/auth/**` — exigem autenticação via JWT no header:
> ```
> Authorization: Bearer {seu-token}
> ```
> O usuário autenticado é identificado **automaticamente pelo token**, sem necessidade de passar `userId` ou `email` como parâmetros.

---

### 🔐 Autenticação — `/auth`

#### `POST /auth/register` — Registrar novo usuário

```http
POST /auth/register
Content-Type: application/json

{
  "name": "João Silva",
  "email": "joao@example.com",
  "password": "SenhaSegura123"
}
```

> **Validações:** `name` obrigatório · `email` válido · `password` mínimo 6 caracteres

**Resposta `200 OK`:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "name": "João Silva",
  "email": "joao@example.com"
}
```

**Erros:**
- `400` — Dados inválidos
- `409` — E-mail já cadastrado

---

#### `POST /auth/login` — Fazer login

```http
POST /auth/login
Content-Type: application/json

{
  "email": "joao@example.com",
  "password": "SenhaSegura123"
}
```

**Resposta `200 OK`:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "name": "João Silva",
  "email": "joao@example.com"
}
```

**Erros:**
- `400` — Dados inválidos
- `401` — Credenciais incorretas

---

### 📝 Notas — `/notes`

#### `GET /notes` — Listar notas do usuário autenticado

Suporta paginação e filtragem por título.

```http
GET /notes?page=0&size=10&sort=createdAt,desc
Authorization: Bearer {token}

# Com filtro por título:
GET /notes?title=objetivo&page=0&size=10
Authorization: Bearer {token}
```

**Parâmetros de query:**
| Parâmetro | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `title` | string | não | Filtra notas cujo título contenha o valor (case-insensitive) |
| `page` | int | não | Número da página (padrão: 0) |
| `size` | int | não | Itens por página (padrão: 20) |
| `sort` | string | não | Campo e direção, ex: `createdAt,desc` |

**Resposta `200 OK`** (formato Spring `Page`):
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "title": "Meu Objetivo 2026",
      "content": "Descrição detalhada do meu objetivo...",
      "tags": [
        { "id": "6ba7b810-9dad-11d1-80b4-00c04fd430c8", "name": "Carreira" }
      ],
      "createdAt": "2026-03-25T10:30:00",
      "updatedAt": "2026-03-25T10:30:00"
    }
  ],
  "totalElements": 25,
  "totalPages": 3,
  "size": 10,
  "number": 0,
  "first": true,
  "last": false,
  "empty": false
}
```

---

#### `GET /notes/{id}` — Buscar nota por ID

```http
GET /notes/550e8400-e29b-41d4-a716-446655440000
Authorization: Bearer {token}
```

**Resposta `200 OK`:** objeto `Note` completo  
**Erro `404`:** nota não encontrada

---

#### `POST /notes` — Criar nova nota

O autor é definido automaticamente pelo usuário autenticado no token.

```http
POST /notes
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Nova Nota",
  "content": "Conteúdo da nota",
  "tagIds": ["6ba7b810-9dad-11d1-80b4-00c04fd430c8"]
}
```

> **Campos:** `title` obrigatório · `content` opcional · `tagIds` opcional (lista de IDs de tags existentes)

**Resposta `200 OK`:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Nova Nota",
  "content": "Conteúdo da nota",
  "tags": null,
  "createdAt": "2026-03-25T10:30:00",
  "updatedAt": "2026-03-25T10:30:00"
}
```

---

#### `PUT /notes/{id}` — Atualizar nota

```http
PUT /notes/550e8400-e29b-41d4-a716-446655440000
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Nota Atualizada",
  "content": "Novo conteúdo"
}
```

**Resposta `200 OK`:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Nota Atualizada",
  "content": "Novo conteúdo",
  "tags": [],
  "createdAt": "2026-03-25T10:30:00",
  "updatedAt": "2026-03-25T11:00:00"
}
```

**Erro `404`:** nota não encontrada

---

#### `DELETE /notes/{id}` — Deletar nota

```http
DELETE /notes/550e8400-e29b-41d4-a716-446655440000
Authorization: Bearer {token}
```

**Resposta `200 OK`:**
```json
{ "message": "Note deleted successfully." }
```

**Erro `404`:**
```json
{ "message": "Note not found." }
```

---

### 🏷️ Tags — `/tags`

#### `GET /tags` — Listar todas as tags

```http
GET /tags
Authorization: Bearer {token}
```

**Resposta `200 OK`:**
```json
[
  { "id": "6ba7b810-9dad-11d1-80b4-00c04fd430c8", "name": "Carreira" },
  { "id": "6ba7b811-9dad-11d1-80b4-00c04fd430c8", "name": "Saúde" }
]
```

---

#### `GET /tags/{id}` — Buscar tag por ID

```http
GET /tags/6ba7b810-9dad-11d1-80b4-00c04fd430c8
Authorization: Bearer {token}
```

**Resposta `200 OK`:**
```json
{ "id": "6ba7b810-9dad-11d1-80b4-00c04fd430c8", "name": "Carreira" }
```

**Erro `404`:** tag não encontrada

---

#### `POST /tags` — Criar nova tag

> Nomes de tag são **únicos** no sistema.

```http
POST /tags
Authorization: Bearer {token}
Content-Type: application/json

{ "name": "Educação" }
```

**Resposta `200 OK`:**
```json
{ "id": "uuid-gerado", "name": "Educação" }
```

**Erro `400`:** tag com esse nome já existe

---

#### `PUT /tags/{id}` — Atualizar tag

```http
PUT /tags/6ba7b810-9dad-11d1-80b4-00c04fd430c8
Authorization: Bearer {token}
Content-Type: application/json

{ "name": "Desenvolvimento Pessoal" }
```

**Resposta `200 OK`:** tag atualizada  
**Erro `404`:** tag não encontrada

---

#### `DELETE /tags/{id}` — Deletar tag

```http
DELETE /tags/6ba7b810-9dad-11d1-80b4-00c04fd430c8
Authorization: Bearer {token}
```

**Resposta `200 OK`:**
```json
{ "message": "Tag deleted successfully." }
```

**Erro `404`:**
```json
{ "message": "Tag not found." }
```

---

### 👤 Usuário — `/user`

> O usuário é identificado pelo token JWT. Não é necessário passar `email` ou `id` na URL.

#### `GET /user/me` — Obter perfil

```http
GET /user/me
Authorization: Bearer {token}
```

**Resposta `200 OK`:**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "João Silva",
  "email": "joao@example.com",
  "createdAt": "2026-03-25T10:30:00"
}
```

**Erro `404`:** usuário não encontrado

---

#### `PUT /user/me` — Atualizar nome

```http
PUT /user/me
Authorization: Bearer {token}
Content-Type: application/json

{ "name": "João Silva Atualizado" }
```

**Resposta `200 OK`:** perfil atualizado (mesmo formato do `GET /user/me`)  
**Erro `404`:** usuário não encontrado

---

#### `DELETE /user/me` — Excluir conta

```http
DELETE /user/me
Authorization: Bearer {token}
```

**Resposta `200 OK`:**
```json
{ "message": "Account deleted successfully" }
```

**Erros:**
- `401` — Não autenticado
- `404` — Usuário não encontrado

---

## 📐 Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/myvisionboard/app/
│   │   ├── Application.java                 # Ponto de entrada Spring Boot
│   │   ├── config/
│   │   │   ├── RateLimitConfig.java        # Rate limiting (atualmente desabilitado)
│   │   │   ├── RedisConfig.java            # Template Redis
│   │   │   ├── SwaggerConfig.java          # Configuração OpenAPI/Swagger
│   │   │   └── WebConfig.java              # CORS (permite localhost:3000)
│   │   ├── controller/
│   │   │   ├── AuthController.java         # POST /auth/register e /auth/login
│   │   │   ├── NoteController.java         # CRUD /notes
│   │   │   ├── TagController.java          # CRUD /tags
│   │   │   └── UserController.java         # Perfil /user/me
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   │   ├── LoginRequest.java       # email, password
│   │   │   │   ├── NoteRequest.java        # title, content, tagIds
│   │   │   │   ├── RegisterRequest.java    # name, email, password
│   │   │   │   ├── TagRequest.java         # name
│   │   │   │   └── UserRequest.java        # name
│   │   │   └── response/
│   │   │       ├── AuthResponse.java       # token, name, email
│   │   │       ├── MessageResponse.java    # message
│   │   │       ├── NoteResponse.java       # id, title, content, tags, createdAt, updatedAt
│   │   │       ├── TagResponse.java        # id, name
│   │   │       └── UserResponse.java       # id, name, email, createdAt
│   │   ├── model/
│   │   │   ├── Note.java                   # Entidade JPA — tabela notes
│   │   │   ├── Tag.java                    # Entidade JPA — tabela tags
│   │   │   └── User.java                   # Entidade JPA — tabela users
│   │   ├── repository/
│   │   │   ├── NoteRepository.java         # findByUserId, findByUserIdAndTitleContaining
│   │   │   ├── TagRepository.java
│   │   │   └── UserRepository.java
│   │   ├── security/
│   │   │   ├── CustomUserDetailsService.java
│   │   │   ├── JwtAuthFilter.java          # Filtro JWT por requisição
│   │   │   ├── JwtService.java             # Geração e validação de tokens
│   │   │   └── SecurityConfig.java         # Regras de autorização
│   │   └── service/
│   │       ├── AuthService.java
│   │       ├── NoteService.java
│   │       ├── TagService.java
│   │       └── UserService.java
│   └── resources/
│       └── application.properties
├── test/java/...
pom.xml
docker-compose.yaml
dockerfile
```

---

## 🗄️ Modelo de Dados

### Tabela: `users`
| Campo | Tipo | Descrição |
|---|---|---|
| id | UUID | Identificador único (auto-gerado) |
| name | VARCHAR(255) | Nome do usuário |
| email | VARCHAR(255) | E-mail único |
| password | VARCHAR(255) | Senha criptografada com BCrypt |
| created_at | TIMESTAMP | Data de criação |
| updated_at | TIMESTAMP | Data da última atualização |

### Tabela: `notes`
| Campo | Tipo | Descrição |
|---|---|---|
| id | UUID | Identificador único (auto-gerado) |
| title | VARCHAR(255) | Título da nota (obrigatório) |
| content | TEXT | Conteúdo da nota |
| user_id | UUID | FK → users.id |
| created_at | TIMESTAMP | Data de criação |
| updated_at | TIMESTAMP | Data da última atualização |

### Tabela: `tags`
| Campo | Tipo | Descrição |
|---|---|---|
| id | UUID | Identificador único (auto-gerado) |
| name | VARCHAR(255) | Nome único da tag |

### Tabela: `note_tags` (junção N:N)
| Campo | Tipo | Descrição |
|---|---|---|
| note_id | UUID | FK → notes.id |
| tag_id | UUID | FK → tags.id |

### Relacionamentos
```
User (1) ──── (N) Note
Note (N) ──── (N) Tag   [via note_tags]
```

---

## 🔐 Autenticação

### Fluxo JWT

```
1. Cliente envia credenciais  →  POST /auth/login ou /auth/register
2. Servidor valida e retorna  →  { token, name, email }
3. Cliente armazena o token
4. Em cada requisição        →  Authorization: Bearer {token}
5. JwtAuthFilter intercepta  →  valida assinatura + expiração
6. Usuário é identificado    →  via subject (email) do token
```

### Header de Autenticação

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Configuração do Token

| Propriedade | Valor padrão | Descrição |
|---|---|---|
| `jwt.secret` | `myvisionboard-secret-key-...` | Chave de assinatura HMAC-SHA |
| `jwt.expiration` | `86400000` | Expiração em ms (24 horas) |

### Rotas Públicas

As seguintes rotas **não requerem** autenticação:

```
POST  /auth/register
POST  /auth/login
GET   /swagger-ui/**
GET   /v3/api-docs/**
GET   /error
```

---

## 🌐 CORS

A aplicação está configurada para aceitar requisições cross-origin de:

```
http://localhost:3000
```

Métodos permitidos: `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`

Para adicionar outras origens, edite `WebConfig.java`:

```java
registry.addMapping("/**")
    .allowedOrigins("http://localhost:3000", "https://seu-frontend.com")
    ...
```

---

## 📖 Documentação Interativa

### Swagger UI

Acesse a interface interativa (sem autenticação necessária):

```
http://localhost:8080/swagger-ui
```

Para testar endpoints protegidos no Swagger UI:
1. Execute `POST /auth/login`
2. Copie o `token` da resposta
3. Clique em **Authorize** (🔓) no topo
4. Insira `Bearer {token}` e confirme

### OpenAPI JSON/YAML

```
http://localhost:8080/v3/api-docs
http://localhost:8080/v3/api-docs.yaml
```

---

## 🐳 Docker

### Dockerfile

O build utiliza multi-stage:

```dockerfile
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
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
    container_name: myvisionboard-postgres
    environment:
      POSTGRES_DB: myvisionboard
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:7
    container_name: myvisionboard-redis
    ports:
      - "6379:6379"

volumes:
  postgres_data:
```

### Comandos Docker

```bash
# Subir infraestrutura (PostgreSQL + Redis)
docker-compose up -d

# Ver logs
docker-compose logs -f

# Parar e remover containers
docker-compose down

# Parar e remover containers + volumes
docker-compose down -v

# Build manual da imagem da aplicação
docker build -t myvisionboard:latest .
```

---

## 🛠️ Desenvolvimento

### Build

```bash
# Compile e empacote
mvn clean package

# Sem rodar testes
mvn clean package -DskipTests

# Apenas compilar
mvn clean compile
```

### Executar Testes

```bash
# Todos os testes
mvn test

# Teste específico
mvn test -Dtest=ApplicationTests
```

### Dependências Principais

| Dependência | Versão | Finalidade |
|---|---|---|
| spring-boot-starter-web | 4.0.3 | REST API |
| spring-boot-starter-security | 4.0.3 | Segurança |
| spring-boot-starter-data-jpa | 4.0.3 | Persistência ORM |
| spring-boot-starter-data-redis | 4.0.3 | Cache Redis |
| spring-boot-starter-validation | 4.0.3 | Bean Validation |
| jjwt-api | 0.12.6 | JWT |
| postgresql | runtime | Driver JDBC |
| lombok | — | Boilerplate |
| springdoc-openapi-starter-webmvc-ui | 2.8.6 | Swagger/OpenAPI |

### IDEs Recomendadas

- **IntelliJ IDEA** (Community ou Ultimate) — melhor suporte a Spring
- **VS Code** com extensões: _Extension Pack for Java_, _Spring Boot Extension Pack_
- **Eclipse IDE for Enterprise Java Developers**

---

## 🔧 Troubleshooting

### Porta 8080 já em uso

```bash
# Descobrir o processo
lsof -i :8080

# Encerrar
kill -9 <PID>

# Ou alterar a porta em application.properties
server.port=8081
```

### Connection refused — PostgreSQL

```bash
# Verificar se o container está rodando
docker ps

# Restartar
docker-compose restart postgres
```

### Connection refused — Redis

```bash
docker-compose restart redis
```

### Token JWT inválido / 401 Unauthorized

- Verifique se o token não expirou (validade: 24 h)
- Certifique-se de incluir o prefixo `Bearer ` (com espaço)
- Confirme que `jwt.secret` é o mesmo usado na geração do token

### `spring.jpa.hibernate.ddl-auto=update` em produção

> ⚠️ Em produção, altere para `validate` e gerencie as migrações com **Flyway** ou **Liquibase**.

---

## 🤝 Contribuindo

1. **Fork** o repositório
2. **Crie** uma branch: `git checkout -b feature/MinhaFeature`
3. **Commit**: `git commit -m 'feat: adiciona MinhaFeature'`
4. **Push**: `git push origin feature/MinhaFeature`
5. **Abra** um Pull Request

### Padrões

- Siga as convenções Java (nomes em camelCase, classes em PascalCase)
- Adicione JavaDoc em métodos públicos de serviço
- Escreva testes para novas features
- Mantenha controllers finos — lógica de negócio nos services

---

## 📝 Licença

Este projeto está sob licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

---

## 📧 Suporte

- Abra uma **Issue** no repositório
- Consulte a **documentação Swagger** em `http://localhost:8080/swagger-ui`

---

## 📚 Referências

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [JJWT (io.jsonwebtoken)](https://github.com/jwtk/jjwt)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Redis Documentation](https://redis.io/docs/)
- [SpringDoc OpenAPI](https://springdoc.org/)

---

**Última Atualização:** 25 de Março de 2026
