<div align="center">

# 🏦 AtlasBank API

### API REST de uma plataforma bancária simulada

Desenvolvida com **Java 21 + Spring Boot**, aplicando conceitos de backend profissional em um sistema com regras de negócio inspiradas em aplicações financeiras reais.

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)](https://www.rabbitmq.com/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)

</div>

---

## 📖 Sobre o projeto

O **AtlasBank** é uma API REST bancária simulada que aborda **autenticação, segurança, operações financeiras, concorrência, mensageria assíncrona, resiliência e rastreabilidade** — construída como um estudo profundo de arquitetura backend aplicada a um domínio financeiro.

O projeto simula um sistema de **Pix interno**, com validações de saldo, credenciais e contas, controle de concorrência via locks pessimistas, processamento assíncrono de emails transacionais com recovery automático de falhas, e um ledger completo para histórico de movimentações.

---

## 📑 Sumário

- [Stack](#-stack)
- [Principais funcionalidades](#️-principais-funcionalidades)
- [Ledger financeiro](#-ledger-financeiro)
- [Mensageria e processamento assíncrono](#-mensageria-e-processamento-assíncrono)
- [Retry e Backoff](#-retry-e-backoff)
- [Dead Letter Queue](#️-dead-letter-queue)
- [Recovery de mensagens](#-recovery-de-mensagens)
- [Email transacional](#-email-transacional)
- [Rotinas automáticas — Schedulers](#️-rotinas-automáticas--schedulers)
- [Segurança](#-segurança)
- [Banco de dados](#️-banco-de-dados)
- [Arquitetura](#-arquitetura)
- [Testes e concorrência](#-testes-e-concorrência)
- [Infraestrutura](#-infraestrutura)
- [Executando o projeto](#-executando-o-projeto)
- [Objetivos](#-objetivos)
- [Autor](#-autor)

---

## 🛠 Stack

<table>
<tr>
<td valign="top" width="25%">

**Backend**
- Java 21
- Spring Boot
- Spring Data JPA / Hibernate
- Spring Security
- Maven

</td>
<td valign="top" width="25%">

**Dados**
- PostgreSQL
- Flyway

</td>
<td valign="top" width="25%">

**Mensageria**
- RabbitMQ
- Spring AMQP
- Jackson JSON Message Converter

</td>
<td valign="top" width="25%">

**Segurança**
- JWT
- Access Token + Refresh Token
- RBAC (`USER` / `ADMIN`)
- BCrypt
- Ownership validation
- Cookies HttpOnly + SameSite

</td>
</tr>
</table>

<table>
<tr>
<td valign="top" width="50%">

**Infraestrutura**
- Docker / Docker Compose
- Swagger / OpenAPI
- Testcontainers
- JUnit 5 / Mockito

</td>
<td valign="top" width="50%">

**Integrações**
- Resend API para emails transacionais

</td>
</tr>
</table>

---

## ⚙️ Principais funcionalidades

### 🔐 Autenticação e usuários

| Funcionalidade | Descrição |
|---|---|
| Cadastro de usuários | Criação de conta com validação de dados |
| Verificação de email | Confirmação por token enviado ao usuário |
| Login com JWT | Autenticação via Access Token e Refresh Token, protegidos em Cookies HttpOnly + SameSite |
| Logout | Invalidação do Refresh Token |
| Alteração de senha / email / dados | Atualizações protegidas de conta |
| Controle de acesso por roles | RBAC (`USER` / `ADMIN`) |
| Invalidação de sessões | Após alterações sensíveis na conta |

### 💰 Operações financeiras

**Depósitos**
Registro de depósitos e movimentações financeiras vinculadas às contas.

**Pix interno**
Sistema de transferência entre contas da própria aplicação, incluindo:

- ✅ Validação de saldo
- ✅ Validação de credenciais
- ✅ Validação de contas
- ✅ Prevenção de transferência para a própria conta
- ✅ Locks pessimistas no PostgreSQL
- ✅ Ordenação de locks para prevenção de deadlocks
- ✅ Testes de concorrência com múltiplas threads
- ✅ Testes de integração com Testcontainers
- ✅ Registro das operações no ledger

---

## 📒 Ledger financeiro

O AtlasBank utiliza um **ledger financeiro** para manter rastreabilidade completa das movimentações. Cada entrada registra:

- Tipo da operação
- Valor
- Conta relacionada
- Data e hora
- Referência da operação

> O ledger permite acompanhar o histórico das movimentações sem depender apenas do saldo atual da conta.

---

## 📬 Mensageria e processamento assíncrono

O AtlasBank utiliza **RabbitMQ + Spring AMQP** para desacoplar operações que não precisam ser executadas dentro do fluxo principal da requisição — como o processamento de **emails transacionais** — através de queues, exchanges, routing keys e bindings.

```text
Application
    │
    │ Publish Event
    ▼
┌────────────────┐
│    Exchange     │
│ atlas.exchange  │
└───────┬─────────┘
        │ routing key
        ▼
┌────────────────────────┐
│ user.registered.queue   │
└───────────┬─────────────┘
            │
            ▼
       EmailConsumer
            │
            ▼
        Resend API
```

O cadastro do usuário **não depende diretamente** do envio do email — o sistema publica um `UserRegisteredEvent` contendo os dados necessários para o processamento posterior.

---

## 🔁 Retry e Backoff

Falhas temporárias no processamento de mensagens são tratadas através de **retry automático**.

```yaml
rabbitmq:
  listener:
    simple:
      retry:
        enabled: true
        max-attempts: ${MAX_ATTEMPTS}
        initial-interval: ${INITIAL_INTERVAL}
        multiplier: 1
```

```env
MAX_ATTEMPTS=4
INITIAL_INTERVAL=900000
```

Isso permite até **4 tentativas**, mantendo intervalo configurável entre elas (`multiplier: 1` mantém o intervalo constante).

Para testes de falha, o intervalo foi reduzido para 5 segundos, permitindo observar o ciclo:

```text
Attempt 1 → 5s → Attempt 2 → 5s → Attempt 3 → 5s → Attempt 4 → DLQ
```

---

## ⚰️ Dead Letter Queue

Após o esgotamento das tentativas, a mensagem é rejeitada sem *requeue* e encaminhada para uma **Dead Letter Queue (DLQ)**.

```text
user.registered.queue
          │
          │ retry exhausted
          ▼
   atlas.exchange
          │
          │ user.registered.dlq
          ▼
   user.registered.dlq
```

A fila principal possui configuração de:

- Dead Letter Exchange
- Dead Letter Routing Key
- Fila durável
- Mensagens persistentes

> Isso permite preservar mensagens que não puderam ser processadas após todas as tentativas, garantindo reprocessamento após falhas prolongadas do serviço externo (Resend).

---

## ♻️ Recovery de mensagens

Mensagens presentes na DLQ podem ser recuperadas automaticamente por um **scheduler**.

```text
DLQ
 │
 │ Recovery Scheduler
 ▼
Exchange
 │
 │ user.registered
 ▼
Main Queue
 │
 ▼
Consumer
 │
 ├── sucesso → ACK
 │
 └── falha → Retry → DLQ
```

O recovery utiliza `basicGet` para obter mensagens da DLQ sem ACK automático. A mensagem só é removida da DLQ **após a publicação bem-sucedida novamente no exchange**:

```text
basicGet() → basicPublish() → basicAck()
```

Caso a publicação falhe, a mensagem não é confirmada e permanece disponível para recuperação posterior. O scheduler utiliza `fixedDelay`, permitindo executar o processo periodicamente sem bloquear o fluxo principal da aplicação.

---

## ✉️ Email transacional

O envio de emails é realizado através da **Resend API**, desacoplado da criação do usuário através de RabbitMQ.

Fluxos implementados:

- Verificação de conta
- Alteração de email
- Alteração de senha do usuário
- Alteração de senha da conta

> O token de verificação possui expiração e é gerenciado pelo consumidor do evento, permitindo que uma mensagem recuperada da DLQ gere um novo token caso o original já tenha expirado.

---

## ⏱️ Rotinas automáticas — Schedulers

Schedulers responsáveis pela manutenção e recuperação automática do sistema:

| Scheduler | Descrição |
|---|---|
| 🔁 Recovery de mensagens da DLQ | Reprocessamento automático de mensagens que falharam após esgotarem os retries, permitindo nova tentativa quando o serviço externo se recupera |
| 🔑 Limpeza de tokens expirados | Remoção automática de tokens de autenticação e confirmação que já não são válidos |
| 👤 Limpeza de usuários e contas não verificados | Remoção de registros temporários que não concluíram a verificação dentro do período definido |
| 🔒 Limpeza de senhas não verificadas | Remoção automática de alterações de senha pendentes que não foram confirmadas dentro do prazo |

---

## 🔒 Segurança

- JWT (Access Token e Refresh Token)
- Cookies HttpOnly + SameSite para proteção dos tokens
- RBAC (`USER` / `ADMIN`)
- BCrypt
- Ownership validation
- Expiração de tokens
- Invalidação de Refresh Tokens após alterações sensíveis
- Rate limiting
- Separação entre senha de usuário e senha de operações financeiras

---

## 🗄️ Banco de dados

O **PostgreSQL** é utilizado como banco principal e suas alterações de schema são controladas pelo **Flyway**.

Principais entidades:

```text
User
Account
RefreshToken
Pix
LedgerEntry
```

---

## 🏗️ Arquitetura

O projeto utiliza uma organização **feature-based**, mantendo responsabilidades relacionadas ao mesmo domínio próximas umas das outras.

```text
src/main/java/org/atlas
├── auth
├── account
├── common
├── email
├── security
├── transaction
└── user
```

Responsabilidades compartilhadas ficam concentradas em `common`:

```text
common/
├── exception
├── messaging
├── normalize
├── scheduler
└── seeder
```

---

## 🧪 Testes e concorrência

O projeto possui testes unitários e de integração utilizando:

- JUnit 5
- Mockito
- Testcontainers
- PostgreSQL real em container

> Os testes de concorrência simulam múltiplas operações financeiras simultâneas com múltiplas threads para validar o comportamento dos locks pessimistas e a consistência das transações.

---

## 🐳 Infraestrutura

O ambiente de desenvolvimento é containerizado utilizando **Docker Compose**.

```text
┌──────────────┐
│ Spring Boot  │
└──────┬───────┘
       │
 ┌─────┴──────┐
 ▼            ▼
Postgres   RabbitMQ
               │
               ▼
            Resend
```

---

## 🚀 Executando o projeto

### Pré-requisitos

- Java 21
- Docker
- Docker Compose

### Subir infraestrutura

```bash
docker compose up -d
```

### Executar aplicação

```bash
./mvnw spring-boot:run
```

### Swagger

Após iniciar a aplicação, acesse:

```text
http://localhost:8080/swagger-ui/index.html
```

---

## 🎯 Objetivos

O AtlasBank foi desenvolvido para aprofundar conhecimentos em:

- Desenvolvimento de APIs REST
- Java e Spring Boot
- Segurança de aplicações
- Persistência relacional
- Concorrência e transações
- Mensageria assíncrona
- Integração com serviços externos
- Testcontainers
- Arquitetura de software

---

## 👤 Autor

<div align="center">

**Davi Fonseca Viana**

Backend Developer focado em **Java, Spring Boot e sistemas backend distribuídos**.

[GitHub](https://github.com/daviviana2602-maker/AtlasBank)

</div>

