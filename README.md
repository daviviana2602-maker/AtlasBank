# AtlasBank

AtlasBank é uma API REST de um sistema bancário desenvolvida com foco em arquitetura, escalabilidade e boas práticas utilizadas em aplicações backend modernas.

O objetivo do projeto não é apenas implementar funcionalidades bancárias, mas servir como um projeto completo de estudo e portfólio, simulando desafios encontrados em sistemas reais de instituições financeiras.

## Objetivos

* Construir uma arquitetura limpa e organizada.
* Aplicar conceitos utilizados em sistemas de alta escala.
* Desenvolver um backend seguro e resiliente.
* Demonstrar conhecimento em Java e Spring Boot através de um projeto de nível profissional.

## Tecnologias

* Java 21
* Spring Boot
* Spring Security
* Spring Data JPA
* PostgreSQL
* Redis
* Flyway
* JWT
* OpenAPI (Swagger)
* Maven
* Lombok

## Funcionalidades (Roadmap)

### Autenticação

* Cadastro de usuários
* Login com JWT
* Refresh Token
* Alteração de senha
* Recuperação de senha
* Verificação de e-mail

### Contas

* Criação de conta bancária
* Consulta de saldo
* Histórico da conta
* Bloqueio e desbloqueio

### Transações

* Depósitos
* Saques
* Transferências internas
* PIX
* Extrato

### Segurança

* Rate Limiting
* Cache com Redis
* Controle de acesso por Roles
* Auditoria
* Validação de requisições

### Observabilidade

* Logs estruturados
* Métricas
* Monitoramento

## Estrutura do Projeto

O projeto utiliza uma organização **Feature-Based**, onde cada domínio possui seus próprios componentes.

```text
src/main/java

auth/
account/
common/
config/
```

## Status

🚧 Projeto em desenvolvimento.