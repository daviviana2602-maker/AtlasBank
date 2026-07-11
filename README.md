# AtlasBank API

API REST de uma plataforma bancária simulada desenvolvida com **Java e Spring Boot**, focada em segurança, gerenciamento de contas, autenticação e operações financeiras.

O projeto tem como objetivo aplicar conceitos de engenharia de software em um sistema com regras de negócio próximas de aplicações financeiras reais, incluindo autenticação segura, controle de acesso, movimentações financeiras, Pix interno e rastreamento de transações.

---

## Tecnologias utilizadas

### Backend

* Java 21
* Spring Boot
* Spring Data JPA
* Spring Security
* Hibernate

### Banco de Dados

* PostgreSQL
* Flyway (versionamento de migrations)

### Segurança

* JWT (Access Token e Refresh Token)
* RBAC (Role-Based Access Control)
* Validação de ownership
* Hash de senhas (SHA256 e Bcrypt)
* Controle de autenticação e autorização

### Infraestrutura e ferramentas

* Docker
* Docker Compose
* Swagger/OpenAPI

### Integrações externas

* Resend API para envio de emails transacionais personalizados

---

# Funcionalidades

## Autenticação e usuários

* Cadastro de usuários
* Verificação de email através de token
* Login com JWT
* Access Token e Refresh Token
* Logout com invalidação de refresh token
* Logout automático após alterações sensíveis de segurança
* Alteração de senha do usuário
* Alteração de nome e email com confirmação
* Controle de permissões

---

# Gerenciamento de conta

* Criação e gerenciamento de contas bancárias
* Consulta de saldo
* Senha específica para operações financeiras
* Alteração da senha da conta
* Alteração de nome e email
* Validação de identidade utilizando claims do JWT
* Controle de acesso baseado no usuário autenticado

---

# Operações financeiras

## Depósitos

Implementação de depósitos com registro das movimentações financeiras.

## Pix interno

Sistema de Pix simulado entre contas da aplicação.

Inclui:

* Transferência entre contas
* Validação de saldo
* Validação de contas existentes
* Registro das operações
* Rastreamento através de ledger financeiro

---

# Ledger Financeiro

O sistema possui uma estrutura de ledger para registrar movimentações financeiras.

Cada operação gera registros que permitem acompanhar:

* Tipo da operação
* Valor movimentado
* Conta relacionada
* Horário realizado

O objetivo é manter histórico e rastreabilidade das operações realizadas.

---

# Segurança

O projeto aplica práticas de segurança como:

* Senhas armazenadas utilizando hash
* Separação entre senha de perfil e senha de transações
* Autorização baseada em roles (USER e ADMIN)
* Identidade do usuário obtida através dos claims do JWT
* Expiração e invalidação de tokens
* Controle de acesso a recursos privados

---

# Emails transacionais

Integração com a API do Resend para envio de emails personalizados.

Fluxos implementados:

* Verificação de conta
* Alteração de email
* Alteração de senha do perfil
* Alteração de senha da conta


Cada fluxo possui templates próprios e tokens com expiração.

---

# Banco de Dados

O banco é versionado utilizando Flyway.

Principais entidades:

* Users
* Account
* Refresh Tokens
* Pix
* Ledger Entries

---

# Arquitetura

O projeto utiliza organização baseada em features, separando os módulos por domínio:

```
src/main/java/org/atlas

├── auth
├── account
├── common
├── security
├── email
├── transaction
└── user

```

Cada módulo possui suas próprias responsabilidades, como:

* Controllers
* Services
* Repositories
* DTOs
* Entities
* enums (caso necessário)


---

# Rotinas automáticas

Implementação de schedulers para manutenção automática do sistema:

* Limpeza de refresh tokens expirados
* Remoção de dados temporários inválidos
* Limpeza de tokens de confirmação expirados

---

# Executando o projeto

## Pré-requisitos

* Java 21
* Docker
* Docker Compose

---

## Subindo os serviços

```bash
docker compose up -d
```

---

## Executando a aplicação

```bash
./mvnw spring-boot:run
```

---

# Documentação da API

Após iniciar a aplicação:

```
http://localhost:8080/swagger-ui/index.html
```

---

# Objetivos do projeto

O AtlasBank foi desenvolvido como projeto de estudo para aplicar conceitos de desenvolvimento backend profissional, incluindo:

* APIs REST
* Segurança de aplicações
* Persistência relacional
* Modelagem de domínio
* Integrações externas
* Boas práticas de engenharia de software

---

## Autor

Davi Fonseca Viana

Backend Developer focado em Java e Spring Boot.
