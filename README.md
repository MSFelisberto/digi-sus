# Digi-SUS - Sistema Digital do SUS

Plataforma de servicos de saude do SUS (Sistema Unico de Saude) construida em arquitetura de microsservicos com Java 21, Spring Boot 3.5.5 e Spring Cloud 2025.0.0.

---

## Sumario

1. [Visao Geral da Arquitetura](#1-visao-geral-da-arquitetura)
2. [Tecnologias e Pre-requisitos](#2-tecnologias-e-pre-requisitos)
3. [Configuracao e Execucao](#3-configuracao-e-execucao)
4. [Infraestrutura](#4-infraestrutura)
5. [Atores e Papeis](#5-atores-e-papeis)
6. [Fluxo de Autenticacao](#6-fluxo-de-autenticacao)
7. [Cadastro de Usuarios](#7-cadastro-de-usuarios)
8. [Agendamento Tradicional de Consultas](#8-agendamento-tradicional-de-consultas)
9. [Agenda Medica e Auto-Agendamento](#9-agenda-medica-e-auto-agendamento)
10. [Triagem e Consulta de Urgencia](#10-triagem-e-consulta-de-urgencia)
11. [Atendimento Medico](#11-atendimento-medico)
12. [Agendamento de Exames](#12-agendamento-de-exames)
13. [Historico Medico (GraphQL)](#13-historico-medico-graphql)
14. [Notificacoes](#14-notificacoes)
15. [Comunicacao Assincrona (RabbitMQ)](#15-comunicacao-assincrona-rabbitmq)
16. [Banco de Dados](#16-banco-de-dados)
17. [Mapa Completo de Endpoints](#17-mapa-completo-de-endpoints)
18. [Regras de Negocio](#18-regras-de-negocio)
19. [Decisoes Arquiteturais](#19-decisoes-arquiteturais)
20. [Testes com Postman](#20-testes-com-postman)

---

## 1. Visao Geral da Arquitetura

```
                          +--------------------+
                          |   Cliente (App)    |
                          +--------+-----------+
                                   | HTTP
                                   v
                          +--------------------+
                          |   API Gateway      |
                          |   (porta 8080)     |
                          |                    |
                          |  - Valida JWT      |
                          |  - Injeta headers  |
                          |  - Roteia via LB   |
                          +--------+-----------+
                                   |
            +----------+-----------+-----------+----------+----------+
            |          |           |           |          |          |
     +------+----+ +---+------+ +-+--------+ ++---------++  +------+-----+
     |Autenticacao| |Agendamento| | Triagem  | |Atendimento|  |   Exames   |
     |            | |           | |          | |           |  |            |
     | - Login    | | - Consultas| |- Sinais | |- Iniciar  |  |- Tipos    |
     | - Cadastro | | - Agenda  | |  vitais  | |- Finalizar|  |- Solicitar|
     | - JWT      | | - Horarios| |- Classif.| |- Exames   |  |- Agendar  |
     +------------+ +-----+-----+ +----+-----+ +-----+-----+  +-----+-----+
                           |            |             |              |
                    +------+------------+-------------+--------------+---+
                    |                    RabbitMQ                         |
                    |           Exchange: "notificacoes"                  |
                    +------+-----------------------------+---------------+
                           |                             |
                    +------+------+              +-------+--------+
                    |  Historico   |              |  Notificacoes  |
                    |              |              |                |
                    |  GraphQL     |              |  Email simulado|
                    |  Consultas   |              |  Stateless     |
                    |  Triagens    |              +----------------+
                    +--------------+
```

### Microsservicos

| Servico | Porta | Banco | Funcao |
|---------|-------|-------|--------|
| **server** | 8761 | - | Eureka Service Registry - descoberta de servicos |
| **gateway** | 8080 | - | API Gateway - roteamento, validacao JWT, injecao de headers |
| **autenticacao** | dinamica | db_autenticacao | Cadastro de pacientes/funcionarios, emissao JWT |
| **agendamento** | dinamica | db_agendamento | Consultas, agendas medicas, horarios disponiveis |
| **triagem** | dinamica | - | Registro de sinais vitais, classificacao de prioridade (stateless) |
| **atendimento** | dinamica | db_atendimento | Atendimento medico, anamnese, conduta medica |
| **exames** | dinamica | db_exames | Tipos de exame, solicitacoes, agendas e agendamentos de exame |
| **historico** | dinamica | db_historico | Historico medico via GraphQL, consome eventos RabbitMQ |
| **notificacoes** | dinamica | - | Consome eventos e simula envio de emails (stateless) |
| **commons** | - | - | Biblioteca compartilhada (DTOs, configuracao RabbitMQ) |

Todos os servicos de negocio usam portas dinamicas (`server.port=0`) e se registram no Eureka. O Gateway descobre os servicos via Eureka usando load balancing (`lb://nome-servico`).

---

## 2. Tecnologias e Pre-requisitos

### Stack Tecnologica

| Tecnologia | Versao | Uso |
|-----------|--------|-----|
| Java | 21 | Linguagem principal |
| Spring Boot | 3.5.5 | Framework principal (historico usa 3.3.1) |
| Spring Cloud | 2025.0.0 | Service discovery, gateway (historico usa 2023.0.2) |
| PostgreSQL | 13 | Banco de dados relacional |
| RabbitMQ | 3.10 | Message broker (comunicacao assincrona) |
| Flyway | - | Migrations de banco de dados |
| JJWT | 0.11.5 | Geracao e validacao de tokens JWT |
| Lombok | 1.18.32 | Reducao de boilerplate |
| Maven | 3.9+ | Build e gerenciamento de dependencias |
| Docker & Docker Compose | - | Containerizacao |

### Pre-requisitos para Desenvolvimento Local

- **Java 21** (JDK)
- **Maven 3.9+**
- **PostgreSQL 13+**
- **RabbitMQ 3.10+**
- **Docker & Docker Compose** (para execucao via containers)

---

## 3. Configuracao e Execucao

### 3.1 Execucao com Docker Compose (Recomendado)

A forma mais simples de rodar todo o ambiente:

```bash
# 1. Clonar o repositorio
git clone <url-do-repositorio>
cd digi-sus

# 2. Criar o arquivo .env na raiz (se nao existir)
cat > .env << 'EOF'
AGENDAMENTO_SECRET=agendamento-super-secret-2026-dgs
HISTORICO_SECRET=historico-ultra-secret-2026-dgs
NOTIFICACOES_SECRET=notificacoes-mega-secret-2026-dgs
TRIAGEM_SECRET=triagem-super-secret-2026-dgs
ATENDIMENTO_SECRET=atendimento-super-secret-2026-dgs
EXAMES_SECRET=exames-super-secret-2026-dgs
EOF

# 3. Subir todos os servicos
docker-compose up --build

# 4. Verificar se todos os servicos subiram
# Acesse o Eureka Dashboard: http://localhost:8761
# Todos os servicos devem aparecer com status UP
```

**Portas expostas:**

| Servico | Porta Local | Descricao |
|---------|-------------|-----------|
| Gateway | 8080 | Ponto de entrada da API |
| Eureka | 8761 | Dashboard de servicos |
| PostgreSQL | 5432 | Banco de dados |
| RabbitMQ AMQP | 5672 | Message broker |
| RabbitMQ Management | 15672 | UI de gerenciamento (guest/guest) |

**Ordem de inicializacao (automatica via depends_on):**
1. Eureka Server (healthcheck: HTTP /eureka)
2. PostgreSQL (healthcheck: pg_isready) + RabbitMQ
3. Gateway
4. Servicos de negocio (autenticacao, agendamento, triagem, atendimento, exames, historico, notificacoes)

**Comandos uteis:**

```bash
# Subir em background
docker-compose up -d --build

# Ver logs de um servico especifico
docker-compose logs -f triagem

# Rebuild de um unico servico
docker-compose up --build triagem

# Parar tudo
docker-compose down

# Parar e remover volumes (reset do banco)
docker-compose down -v
```

### 3.2 Execucao Local (Desenvolvimento)

Para rodar localmente sem Docker, voce precisa de PostgreSQL e RabbitMQ instalados na maquina.

```bash
# 1. Instalar e iniciar PostgreSQL
# Criar os bancos de dados:
psql -U admin -c "CREATE DATABASE db_autenticacao;"
psql -U admin -c "CREATE DATABASE db_agendamento;"
psql -U admin -c "CREATE DATABASE db_historico;"
psql -U admin -c "CREATE DATABASE db_notificacoes;"
psql -U admin -c "CREATE DATABASE db_atendimento;"
psql -U admin -c "CREATE DATABASE db_exames;"

# 2. Instalar e iniciar RabbitMQ
# MacOS: brew install rabbitmq && brew services start rabbitmq
# Linux: sudo systemctl start rabbitmq-server

# 3. Definir variaveis de ambiente (ou usar defaults do application.properties)
export AGENDAMENTO_SECRET=dev-agendamento-secret-123
export HISTORICO_SECRET=dev-historico-secret-456
export NOTIFICACOES_SECRET=dev-notificacoes-secret-789
export TRIAGEM_SECRET=dev-triagem-secret-303
export ATENDIMENTO_SECRET=dev-atendimento-secret-101
export EXAMES_SECRET=dev-exames-secret-202

# 4. Build de todos os modulos
mvn clean install -DskipTests

# 5. Iniciar servicos na ordem correta (cada um em um terminal separado):

# Terminal 1 - Eureka Server
cd server && mvn spring-boot:run

# Terminal 2 - Gateway (aguardar Eureka estar UP)
cd gateway && mvn spring-boot:run

# Terminal 3 - Autenticacao
cd autenticacao && mvn spring-boot:run

# Terminal 4 - Agendamento
cd agendamento && mvn spring-boot:run

# Terminal 5 - Triagem
cd triagem && mvn spring-boot:run

# Terminal 6 - Atendimento
cd atendimento && mvn spring-boot:run

# Terminal 7 - Exames
cd exames && mvn spring-boot:run

# Terminal 8 - Historico
cd historico && mvn spring-boot:run

# Terminal 9 - Notificacoes
cd notificacoes && mvn spring-boot:run
```

**Configuracoes locais (application.properties):**

| Servico | PostgreSQL | RabbitMQ | Eureka |
|---------|-----------|----------|--------|
| Todos | localhost:5432 | localhost:5672 (guest/guest) | localhost:8761 |

### 3.3 Build e Testes

```bash
# Build completo (todos os modulos)
mvn clean install -DskipTests

# Build de um modulo especifico
mvn clean install -DskipTests -pl agendamento -am

# Executar todos os testes
mvn test

# Testes de um modulo especifico
mvn test -pl agendamento

# Testar uma classe especifica
mvn test -pl agendamento -Dtest=AgendamentoUseCaseImplTest
```

---

## 4. Infraestrutura

### 4.1 Docker Compose

O `docker-compose.yml` define 11 servicos conectados via rede bridge `dgs-network`:

```
                    dgs-network (bridge)
                          |
    +------+--------+----+----+--------+------+
    |      |        |         |        |      |
 server postgres rabbitmq gateway  autenticacao ...
 (8761) (5432)  (5672/15672) (8080)  (dinamica)
```

**Volumes:**
- `./postgres-data` - Persistencia dos dados do PostgreSQL
- `./init.sql` - Script de inicializacao que cria os 6 bancos de dados

**Imagem base dos Dockerfiles:**
Todos os microsservicos usam build multi-stage:
1. `maven:3.9-eclipse-temurin-21` - Build com Maven
2. `eclipse-temurin:21-jre-jammy` - Runtime otimizado

### 4.2 Profiles Spring

| Profile | Uso | URLs |
|---------|-----|------|
| default | Desenvolvimento local | localhost para Postgres, RabbitMQ, Eureka |
| docker | Docker Compose | Nomes DNS dos containers (postgres, rabbitmq, server) |

O Docker Compose define `SPRING_PROFILES_ACTIVE=docker` em todos os servicos.

### 4.3 Service Discovery (Eureka)

- **Dashboard:** http://localhost:8761
- Todos os servicos se registram com `spring.application.name`
- Gateway usa `lb://nome-servico` para roteamento com load balancing
- Servicos se comunicam entre si usando nomes Eureka (ex: `http://autenticacao`)

### 4.4 API Gateway

O Gateway (porta 8080) eh o unico ponto de entrada para clientes externos.

**Rotas configuradas:**

| Prefixo | Servico Destino | Exemplo |
|---------|-----------------|---------|
| `/autenticacao/**` | lb://autenticacao | `POST /autenticacao/auth/login` |
| `/agendamento/**` | lb://agendamento | `POST /agendamento/agenda` |
| `/triagem/**` | lb://triagem | `POST /triagem/triagem` |
| `/atendimento/**` | lb://atendimento | `POST /atendimento/atendimentos` |
| `/exames/**` | lb://exames | `POST /exames/solicitacoes` |
| `/historico/**` | lb://historico | `POST /historico/graphql` |
| `/notificacoes/**` | lb://notificacoes | - |

**Filtros aplicados em cada rota:**
1. `StripPrefix=1` - Remove o prefixo do servico (ex: `/agendamento/agenda` -> `/agenda`)
2. `AuthenticationFilter` - Valida JWT e injeta headers

**Headers injetados pelo Gateway:**
- `X-User-ID` - ID do usuario extraido do token
- `X-User-Email` - Email do usuario
- `X-User-Roles` - Roles separadas por virgula (ex: `ROLE_MEDICO`)

**Rotas abertas (sem JWT):**
- `/auth/login` - Login de usuario
- `/auth/service/**` - Autenticacao service-to-service
- `/internal/**` - Endpoints internos entre servicos

---

## 5. Atores e Papeis

| Ator | Role JWT | Descricao |
|------|----------|-----------|
| **Paciente** | `ROLE_PACIENTE` | Cidadao que busca atendimento no SUS. Pode auto-agendar consultas e exames, visualizar seu proprio historico |
| **Medico** | `ROLE_MEDICO` | Profissional medico. Agenda/reagenda/cancela consultas, realiza atendimentos, solicita exames, define agenda. Requer CRM e Especialidade |
| **Enfermeiro** | `ROLE_ENFERMEIRO` | Profissional de enfermagem. Agenda/reagenda/cancela consultas, realiza triagem. Requer COREN |
| **Administrador** | `ROLE_ADMIN` | Gestor do sistema. Cadastra usuarios, cria tipos de exame, gerencia agendas |
| **Atendente** | `ROLE_ATENDENTE` | Recepcionista. Auxilia pacientes no agendamento de consultas e exames |
| **Tecnico de Laboratorio** | `ROLE_TECNICO_LABORATORIO` | Profissional de laboratorio |
| **Sistema** | `ROLE_SISTEMA` | Comunicacao interna entre microsservicos (service-to-service) |

### Validacoes por Tipo de Funcionario

| Tipo | CRM | COREN | Especialidade |
|------|-----|-------|---------------|
| MEDICO | Obrigatorio | Proibido | Obrigatoria |
| ENFERMEIRO | Proibido | Obrigatorio | Proibida |
| ADMIN | Proibido | Proibido | Proibida |
| TECNICO_LABORATORIO | Proibido | Proibido | Proibida |
| ATENDENTE | Proibido | Proibido | Proibida |

### Usuarios Iniciais (via migration)

| Email | Senha | Tipo | Role |
|-------|-------|------|------|
| admin@email.com | admin123 | ADMIN | ROLE_ADMIN |
| medico@email.com | medico123 | MEDICO | ROLE_MEDICO |
| enfermeiro@email.com | enfermeiro123 | ENFERMEIRO | ROLE_ENFERMEIRO |
| paciente@email.com | paciente123 | PACIENTE | ROLE_PACIENTE |

---

## 6. Fluxo de Autenticacao

### 6.1 Login de Usuario

```
Cliente                    Gateway                  Autenticacao
  |                          |                          |
  |  POST /autenticacao/     |                          |
  |       auth/login         |                          |
  |  { email, senha }        |                          |
  |------------------------->|                          |
  |                          |  (rota aberta, sem JWT)  |
  |                          |  POST /auth/login        |
  |                          |------------------------->|
  |                          |                          | Busca Paciente ou
  |                          |                          | Funcionario por email
  |                          |                          | Valida senha (BCrypt)
  |                          |                          | Gera JWT com:
  |                          |                          |  - userId
  |                          |                          |  - email
  |                          |                          |  - roles[]
  |                          |  { token, type, expires, |
  |                          |    userType }            |
  |                          |<-------------------------|
  |  { token, "Bearer",     |                          |
  |    86400000, "MEDICO" }  |                          |
  |<-------------------------|                          |
```

**Response de login:**
```json
{
  "token": "eyJhbGciOiJIUzI1...",
  "type": "Bearer",
  "expiresIn": 86400000,
  "userType": "MEDICO"
}
```

### 6.2 Requisicao Autenticada (fluxo do Gateway)

```
Cliente                    Gateway                  Microsservico
  |                          |                          |
  |  GET /agendamento/...    |                          |
  |  Authorization: Bearer X |                          |
  |------------------------->|                          |
  |                          | 1. RouterValidator:      |
  |                          |    rota eh protegida?    |
  |                          |                          |
  |                          | 2. Extrai token do       |
  |                          |    header Authorization  |
  |                          |                          |
  |                          | 3. Valida JWT:           |
  |                          |    - Assinatura (HMAC)   |
  |                          |    - Expiracao           |
  |                          |                          |
  |                          | 4. Extrai claims:        |
  |                          |    - userId, email, roles|
  |                          |                          |
  |                          | 5. Injeta headers:       |
  |                          |    X-User-ID: 1          |
  |                          |    X-User-Email: a@b.com |
  |                          |    X-User-Roles: ROLE_X  |
  |                          |                          |
  |                          |  Requisicao + headers    |
  |                          |------------------------->|
  |                          |                          | SecurityFilter le
  |                          |                          | headers X-User-*
  |                          |                          | Verifica @PreAuthorize
  |                          |  Response               |
  |                          |<-------------------------|
  |  Response                |                          |
  |<-------------------------|                          |
```

### 6.3 Autenticacao entre Servicos (Service-to-Service)

Microsservicos que precisam validar dados em outro servico (ex: Agendamento valida se paciente existe no Autenticacao):

1. Servico envia `POST /auth/service/token` com `serviceId` e `serviceSecret`
2. Autenticacao valida as credenciais e retorna JWT com `ROLE_SISTEMA` (expiracao: 1 hora)
3. Servico usa esse token para chamar endpoints `/internal/**`

**Servicos registrados:**

| Service ID | Modulo |
|-----------|--------|
| agendamento-service | Agendamento |
| historico-service | Historico |
| notificacoes-service | Notificacoes |
| triagem-service | Triagem |
| atendimento-service | Atendimento |
| exames-service | Exames |

---

## 7. Cadastro de Usuarios

### 7.1 Cadastrar Paciente

**Endpoint:** `POST /autenticacao/pacientes` (requer `ROLE_ADMIN`)

```json
{
  "nomeCompleto": "Joao da Silva",
  "email": "joao@email.com",
  "senha": "senha123",
  "cpf": "12345678901",
  "dataNascimento": "1990-05-15",
  "telefone": "11999998888",
  "endereco": {
    "logradouro": "Rua das Flores",
    "numero": "123",
    "complemento": "Apto 4",
    "bairro": "Centro",
    "cidade": "Sao Paulo",
    "estado": "SP",
    "cep": "01001000"
  }
}
```

**Validacoes de dominio:**
- Email: formato valido, unico no sistema
- Senha: minimo 6 caracteres (armazenada com BCrypt)
- CPF: exatamente 11 digitos
- Idade: entre 0 e 120 anos (calculada a partir da data de nascimento)
- CEP: exatamente 8 digitos
- Todos os campos do endereco obrigatorios (exceto complemento)

### 7.2 Cadastrar Funcionario

**Endpoint:** `POST /autenticacao/funcionarios` (requer `ROLE_ADMIN`)

```json
{
  "nomeCompleto": "Dra. Maria Santos",
  "email": "maria@hospital.com",
  "senha": "senha123",
  "cpf": "98765432101",
  "tipo": "MEDICO",
  "crm": "123456",
  "especialidade": {
    "nome": "Cardiologia",
    "codigo": "CARDIO"
  }
}
```

---

## 8. Agendamento Tradicional de Consultas

Fluxo onde o profissional de saude (medico/enfermeiro) agenda diretamente uma consulta para o paciente.

### 8.1 Agendar Consulta

**Endpoint:** `POST /agendamento/agendamento` (requer `ROLE_MEDICO` ou `ROLE_ENFERMEIRO`)

```json
{
  "pacienteId": 1,
  "medicoId": 2,
  "dataHora": "2026-03-15T10:00:00",
  "especialidade": "CARDIOLOGIA"
}
```

**Response:**
```json
{
  "id": 1,
  "pacienteId": 1,
  "medicoId": 2,
  "dataHora": "2026-03-15T10:00:00",
  "especialidade": "CARDIOLOGIA",
  "status": "AGENDADA",
  "tipoConsulta": "REGULAR",
  "prioridade": null,
  "triagemId": null
}
```

**Fluxo interno:**
1. Valida paciente existe (chama Autenticacao via service-to-service)
2. Valida medico existe e esta ativo
3. Valida dataHora eh futura
4. Cria Consulta com status `AGENDADA` e tipo `REGULAR`
5. Publica eventos RabbitMQ: `notificacao.agendar` + `notificacao.historico`

### 8.2 Reagendar Consulta

**Endpoint:** `PUT /agendamento/agendamento/{id}` (requer `ROLE_MEDICO` ou `ROLE_ENFERMEIRO`)

```json
{
  "medicoId": 2,
  "dataHora": "2026-03-20T10:00:00",
  "especialidade": "ORTOPEDIA"
}
```

**Regras:** Nao permite reagendar consulta `CANCELADA`. Nova data deve ser futura. Permite trocar medico e especialidade.

### 8.3 Cancelar Consulta

**Endpoint:** `DELETE /agendamento/agendamento/{id}` (requer `ROLE_MEDICO` ou `ROLE_ENFERMEIRO`)

**Regras:**
- Nao permite cancelar consulta ja cancelada
- **Minimo 24 horas de antecedencia** (exceto consultas de tipo `ENCAIXE`)
- Se a consulta foi criada via auto-agendamento, o slot eh liberado automaticamente

### 8.4 Listar Consultas do Paciente

**Endpoint:** `GET /agendamento/agendamento/paciente/{pacienteId}` (requer `ROLE_MEDICO`, `ROLE_ENFERMEIRO` ou `ROLE_PACIENTE`)

**Regra:** Paciente so ve suas proprias consultas (o sistema compara userId do token com pacienteId).

### 8.5 Listar Consultas Futuras

**Endpoint:** `GET /agendamento/agendamento/consultas` (requer autenticacao)

Retorna consultas futuras com base no papel do usuario autenticado.

---

## 9. Agenda Medica e Auto-Agendamento

Feature de inovacao: medicos definem horarios de atendimento e pacientes podem se auto-agendar nos slots disponiveis.

### 9.1 Criar Agenda do Medico

**Endpoint:** `POST /agendamento/agenda` (requer `ROLE_MEDICO` ou `ROLE_ADMIN`)

```json
{
  "medicoId": 1,
  "diaSemana": "MONDAY",
  "horaInicio": "08:00",
  "horaFim": "12:00",
  "duracaoSlotMinutos": 30,
  "especialidade": "CARDIOLOGIA"
}
```

Define que o medico atende toda segunda-feira das 8h as 12h em slots de 30 minutos.

### 9.2 Gerar Horarios Disponiveis

**Endpoint:** `POST /agendamento/agenda/{id}/gerar-horarios` (requer `ROLE_MEDICO` ou `ROLE_ADMIN`)

```json
{
  "dataInicio": "2026-02-03",
  "dataFim": "2026-03-01"
}
```

Gera slots persistidos em `tb_horarios_disponiveis`. Exemplo: para agenda de segunda 8h-12h com slots de 30min, cada segunda gera 8 slots (08:00, 08:30, 09:00, ..., 11:30).

### 9.3 Buscar Horarios Disponiveis

**Endpoint:** `GET /agendamento/horarios/disponiveis?especialidade=CARDIOLOGIA&dataInicio=2026-02-03&dataFim=2026-02-28` (requer autenticacao)

```json
[
  { "id": 1, "medicoId": 1, "dataHora": "2026-02-03T08:00:00", "especialidade": "CARDIOLOGIA" },
  { "id": 2, "medicoId": 1, "dataHora": "2026-02-03T08:30:00", "especialidade": "CARDIOLOGIA" }
]
```

### 9.4 Auto-Agendamento (Paciente)

**Endpoint:** `POST /agendamento/horarios/autoagendamento` (requer `ROLE_PACIENTE` ou `ROLE_ATENDENTE`)

```json
{ "horarioDisponivelId": 1 }
```

**Fluxo com lock pessimista:**
1. `SELECT FOR UPDATE` no slot (previne double-booking)
2. Verifica `ocupado=false`
3. Valida paciente existe
4. Cria Consulta com dados do slot (medicoId, especialidade, dataHora)
5. Marca slot: `ocupado=true`, `consultaId=nova ID`
6. Publica eventos RabbitMQ

### 9.5 Desativar Agenda

**Endpoint:** `DELETE /agendamento/agenda/{id}` (requer `ROLE_MEDICO` ou `ROLE_ADMIN`)

### 9.6 Listar Agendas do Medico

**Endpoint:** `GET /agendamento/agenda/medico/{medicoId}` (requer `ROLE_MEDICO` ou `ROLE_ADMIN`)

---

## 10. Triagem e Consulta de Urgencia

A triagem registra sinais vitais do paciente, classifica a prioridade automaticamente e pode criar uma consulta de urgencia no Agendamento via RabbitMQ.

### 10.1 Realizar Triagem

**Endpoint:** `POST /triagem/triagem` (requer autenticacao, tipicamente `ROLE_ENFERMEIRO`)

```json
{
  "pacienteId": 1,
  "funcionarioId": 3,
  "pressaoArterial": "120/80",
  "temperatura": 36.5,
  "batimentoCardiaco": 72,
  "conduta": "Paciente estavel, encaminhar para consulta",
  "especialidade": "CLINICA GERAL"
}
```

**Response:**
```json
{
  "id": "uuid-gerado",
  "pacienteId": 1,
  "funcionarioId": 3,
  "pressaoArterial": "120/80",
  "temperatura": 36.5,
  "batimentoCardiaco": 72,
  "conduta": "Paciente estavel, encaminhar para consulta",
  "especialidade": "CLINICA GERAL",
  "prioridade": "NAO_URGENTE",
  "mensagem": "Triagem registrada com sucesso"
}
```

### 10.2 Classificacao Automatica de Prioridade

A prioridade eh calculada automaticamente com base nos sinais vitais:

| Prioridade | Temperatura | Batimento Cardiaco |
|-----------|-------------|-------------------|
| **EMERGENCIA** | >= 40°C ou <= 34°C | >= 150 bpm ou <= 40 bpm |
| **URGENTE** | >= 39°C ou <= 35°C | >= 120 bpm ou <= 50 bpm |
| **POUCO_URGENTE** | >= 38°C | >= 100 bpm ou <= 55 bpm |
| **NAO_URGENTE** | Demais casos | Demais casos |

A regra de maior gravidade prevalece (ex: temperatura de 40°C classifica como EMERGENCIA mesmo com batimento normal).

### 10.3 Integracao Triagem -> Agendamento (Consulta de Urgencia)

Apos a triagem, dois eventos sao publicados no RabbitMQ:
1. `triagem.atendimento` -> Consumido pelo **Agendamento** para criar consulta automatica
2. `triagem.historico` -> Consumido pelo **Historico** para persistencia

**Fluxo de criacao automatica de consulta:**

```
Triagem                   RabbitMQ                 Agendamento
  |                          |                          |
  | Publica TriagemDTO       |                          |
  | routing: triagem.        |                          |
  |   atendimento            |                          |
  |------------------------->|                          |
  |                          | Consumer recebe          |
  |                          |------------------------->|
  |                          |                          |
  |                          |                          | 1. Idempotencia:
  |                          |                          |    ja existe consulta
  |                          |                          |    com este triagemId?
  |                          |                          |
  |                          |                          | 2. Busca slot livre
  |                          |                          |    HOJE para a
  |                          |                          |    especialidade
  |                          |                          |    (SELECT FOR UPDATE)
  |                          |                          |
  |                          |                          | 3a. Se encontrou slot:
  |                          |                          |     Cria consulta REGULAR
  |                          |                          |     Reserva o slot
  |                          |                          |
  |                          |                          | 3b. Se NAO encontrou:
  |                          |                          |     Busca medico da
  |                          |                          |     especialidade
  |                          |                          |     Cria consulta ENCAIXE
  |                          |                          |     (sem slot reservado)
  |                          |                          |
  |                          |                          | 4. Salva consulta com:
  |                          |                          |    tipoConsulta, prioridade,
  |                          |                          |    triagemId
  |                          |                          |
  |                          |                          | 5. Publica notificacao
```

**Tipos de consulta criada:**
- `REGULAR` - Quando ha slot disponivel hoje para a especialidade. O slot eh reservado normalmente.
- `ENCAIXE` - Quando nao ha slots disponiveis. A consulta eh criada para agora, sem reservar slot. Regra de cancelamento de 24h nao se aplica.

---

## 11. Atendimento Medico

O modulo de atendimento gerencia a consulta medica propriamente dita, desde o inicio ate a finalizacao com anamnese e conduta.

### 11.1 Iniciar Atendimento

**Endpoint:** `POST /atendimento/atendimentos` (requer `ROLE_MEDICO`)

```json
{ "consultaId": 1 }
```

**Response:**
```json
{
  "id": 1,
  "consultaId": 1,
  "pacienteId": 1,
  "medicoId": 2,
  "status": "EM_ANDAMENTO",
  "dataHoraInicio": "2026-02-08T10:00:00",
  "dataHoraFim": null,
  "anamnese": null,
  "condutaMedica": null
}
```

**Fluxo:**
1. Busca consulta no Agendamento via REST (`/internal/consultas/{id}`)
2. Valida que a consulta existe e esta com status AGENDADA
3. Cria atendimento com status `EM_ANDAMENTO`
4. Marca consulta como `REALIZADA` no Agendamento (`/internal/consultas/{id}/realizada`)

### 11.2 Finalizar Atendimento

**Endpoint:** `PATCH /atendimento/atendimentos/{id}/finalizar` (requer `ROLE_MEDICO`)

```json
{
  "anamnese": "Paciente relata dores no peito ha 3 dias, sem historico familiar de cardiopatias.",
  "condutaMedica": "Solicitar ECG e Hemograma. Retorno em 15 dias."
}
```

**Fluxo:**
1. Valida que atendimento esta `EM_ANDAMENTO`
2. Registra anamnese e conduta medica
3. Marca como `FINALIZADO` com dataHoraFim
4. Publica evento `atendimento.finalizado` no RabbitMQ

### 11.3 Solicitar Exame durante Atendimento

**Endpoint:** `POST /atendimento/atendimentos/{id}/exames` (requer `ROLE_MEDICO`)

```json
{
  "tipoExame": "HEMOGRAMA",
  "prioridade": "NORMAL",
  "observacoes": "Verificar niveis de hemoglobina"
}
```

Publica evento `atendimento.exame.solicitar` no RabbitMQ, consumido pelo modulo Exames.

### 11.4 Consultar Atendimento

**Endpoints:**
- `GET /atendimento/atendimentos/{id}` (requer `ROLE_MEDICO` ou `ROLE_ENFERMEIRO`)
- `GET /atendimento/atendimentos/consulta/{consultaId}` (requer `ROLE_MEDICO` ou `ROLE_ENFERMEIRO`)

---

## 12. Agendamento de Exames

O modulo de exames gerencia todo o ciclo de vida: tipos de exame, solicitacoes medicas, agendas e agendamentos.

### 12.1 Cadastrar Tipo de Exame

**Endpoint:** `POST /exames/tipos` (requer `ROLE_ADMIN`)

```json
{
  "nome": "Hemograma Completo",
  "codigo": "HEMO",
  "descricao": "Exame de sangue completo",
  "preparacao": "Jejum de 8 horas"
}
```

**Tipos pre-cadastrados via migration:** Hemograma, Raio-X, Ultrassom, ECG, Glicemia.

**Consultar tipos:**
- `GET /exames/tipos` (autenticado) - Listar todos
- `GET /exames/tipos/{id}` (autenticado) - Buscar por ID

### 12.2 Medico Solicita Exame

**Endpoint:** `POST /exames/solicitacoes` (requer `ROLE_MEDICO` ou `ROLE_SISTEMA`)

```json
{
  "pacienteId": 1,
  "medicoId": 2,
  "tipoExameId": 1,
  "atendimentoId": 1,
  "consultaId": 1,
  "prioridade": "NORMAL",
  "observacoes": "Verificar niveis de hemoglobina"
}
```

**Status da solicitacao:** `PENDENTE` -> `AGENDADA` -> `REALIZADA` ou `CANCELADA`

### 12.3 Admin Cria Agenda de Exame

**Endpoint:** `POST /exames/agendamentos/agenda` (requer `ROLE_ADMIN`)

```json
{
  "tipoExameId": 1,
  "diaSemana": "TUESDAY",
  "horaInicio": "07:00",
  "horaFim": "12:00",
  "duracaoSlotMinutos": 20,
  "vagasPorSlot": 3
}
```

Diferente da agenda medica, a agenda de exame tem **multiplas vagas por slot** (`vagasPorSlot`). Ate 3 pacientes podem agendar no mesmo horario.

### 12.4 Buscar Vagas de Exame

**Endpoint:** `GET /exames/agendamentos/vagas?tipoExameId=1&dataInicio=2026-02-03&dataFim=2026-02-28` (autenticado)

```json
[
  { "dataHora": "2026-02-04T07:00:00", "vagasRestantes": 3, "tipoExameId": 1 },
  { "dataHora": "2026-02-04T07:20:00", "vagasRestantes": 2, "tipoExameId": 1 }
]
```

**Vagas computadas dinamicamente** (nao persistidas) — menor contencao que slots de consulta.

### 12.5 Agendar Exame

**Endpoint:** `POST /exames/agendamentos` (requer `ROLE_PACIENTE`, `ROLE_ATENDENTE` ou `ROLE_ADMIN`)

```json
{
  "solicitacaoExameId": 1,
  "dataHora": "2026-02-04T07:00:00"
}
```

### 12.6 Cancelar Agendamento de Exame

**Endpoint:** `DELETE /exames/agendamentos/{id}` (requer `ROLE_PACIENTE` ou `ROLE_ADMIN`)

Retorna a solicitacao para status `PENDENTE` (permite re-agendamento). A vaga eh automaticamente liberada.

### 12.7 Listar e Cancelar Solicitacoes

- `GET /exames/solicitacoes/paciente/{pacienteId}` (requer `ROLE_MEDICO`, `ROLE_PACIENTE` ou `ROLE_ADMIN`)
- `GET /exames/solicitacoes/atendimento/{atendimentoId}` (requer `ROLE_MEDICO`, `ROLE_ENFERMEIRO` ou `ROLE_ADMIN`)
- `DELETE /exames/solicitacoes/{id}` (requer `ROLE_MEDICO` ou `ROLE_ADMIN`)

---

## 13. Historico Medico (GraphQL)

O servico de historico consome eventos RabbitMQ e disponibiliza dados via **GraphQL**.

### 13.1 Schema GraphQL

```graphql
type Query {
  historicoPorPaciente(pacienteId: ID!): [HistoricoConsulta!]!
  historicoPorConsulta(consultaId: ID!): HistoricoConsulta
}

type HistoricoConsulta {
  id: ID!
  consultaId: ID!
  pacienteId: ID!
  medicoId: ID!
  dataHora: String!
  especialidade: String!
  status: String!
  observacoes: String
  dataCriacao: String!
  dataAtualizacao: String!
}
```

**Acesso:** `POST /historico/graphql` (requer `ROLE_MEDICO`, `ROLE_ENFERMEIRO` ou `ROLE_PACIENTE`)

**Interface grafica:** GraphiQL disponivel em `/historico/graphiql`

**Exemplo de query:**
```graphql
query {
  historicoPorPaciente(pacienteId: 1) {
    id
    consultaId
    dataHora
    especialidade
    status
    observacoes
  }
}
```

### 13.2 Como os Dados Chegam

O historico **nunca eh chamado diretamente** pelos servicos. Os dados chegam exclusivamente via RabbitMQ:

| Evento | Acao no Historico |
|--------|-------------------|
| Consulta AGENDADA | Cria `HistoricoConsulta` com status `AGENDADA` |
| Consulta CANCELADA | Atualiza status para `CANCELADA` e adiciona motivo |
| Consulta REAGENDADA | Atualiza dataHora, medico, especialidade |
| Triagem realizada | Persiste `HistoricoTriagem` com sinais vitais, especialidade e prioridade |

---

## 14. Notificacoes

Servico **stateless** (sem banco de dados) que consome eventos do RabbitMQ e simula o envio de emails/notificacoes.

| Fila | Acao |
|------|------|
| `notificacao.agendar.queue` | Log: "Notificacao de agendamento para paciente X" |
| `notificacao.cancelar.queue` | Log: "Notificacao de cancelamento para paciente X" |
| `notificacao.reagendar.queue` | Log: "Notificacao de reagendamento para paciente X" |
| `notificacao.exame.queue` | Log: "Notificacao de solicitacao de exame para paciente X" |
| `notificacao.exame.agendar` | Log: "Notificacao de exame agendado para paciente X" |
| `notificacao.exame.cancelar` | Log: "Notificacao de exame cancelado para paciente X" |

Em producao, esses logs seriam substituidos por chamadas reais a servicos de email/SMS.

---

## 15. Comunicacao Assincrona (RabbitMQ)

### Topologia

```
Exchange: "notificacoes" (Topic Exchange)
|
|-- Routing: notificacao.agendar        --> Queue: notificacao.agendar.queue        --> Notificacoes
|-- Routing: notificacao.cancelar       --> Queue: notificacao.cancelar.queue       --> Notificacoes
|-- Routing: notificacao.reagendar      --> Queue: notificacao.reagendar.queue      --> Notificacoes
|-- Routing: notificacao.historico      --> Queue: notificacao.historico.queue      --> Historico
|
|-- Routing: notificacao.exame.solicitar --> Queue: notificacao.exame.queue         --> Notificacoes
|-- Routing: notificacao.exame.agendar   --> Queue: notificacao.exame.agendar      --> Notificacoes
|-- Routing: notificacao.exame.cancelar  --> Queue: notificacao.exame.cancelar     --> Notificacoes
|
|-- Routing: triagem.atendimento        --> Queue: triagem.atendimento.queue       --> Agendamento
|-- Routing: triagem.historico          --> Queue: triagem.historico.queue          --> Historico
|
|-- Routing: atendimento.finalizado     --> Queue: atendimento.finalizado.queue    --> (listener)
|-- Routing: atendimento.exame.solicitar --> Queue: atendimento.exame.solicitar.queue --> Exames
```

### DTOs de Mensagem

| DTO | Campos | Produtor | Consumidor |
|-----|--------|----------|------------|
| **ConsultaDTO** | pacienteId, medicoId, dataHora, especialidade | Agendamento | Notificacoes |
| **HistoricoEventDTO** | consultaId, pacienteId, medicoId, dataHora, especialidade, tipoEvento | Agendamento | Historico |
| **ExameEventDTO** | solicitacaoExameId, pacienteId, medicoId, tipoExameNome, prioridade, dataHora, tipoEvento | Exames | Notificacoes |
| **TriagemAtendimentoDTO** | pacienteId, triagemId, dadosClinicos, conduta, especialidade, prioridade | Triagem | Agendamento |
| **TriagemHistoricoDTO** | triagemId, pacienteId, funcionarioId, pressaoArterial, temperatura, batimentoCardiaco, conduta, especialidade, prioridade | Triagem | Historico |

---

## 16. Banco de Dados

### 16.1 Visao Geral

PostgreSQL 13 com Flyway para migrations automaticas. Cada servico possui seu proprio banco (Database per Service pattern).

```
PostgreSQL (porta 5432, user: admin, password: admin)
|
|-- db_autenticacao    (Autenticacao)
|-- db_agendamento     (Agendamento)
|-- db_atendimento     (Atendimento)
|-- db_exames          (Exames)
|-- db_historico       (Historico)
|-- db_notificacoes    (Notificacoes - sem tabelas)
```

### 16.2 db_autenticacao

**tb_pacientes**

| Coluna | Tipo | Descricao |
|--------|------|-----------|
| id | BIGSERIAL PK | ID do paciente |
| nome_completo | VARCHAR | Nome completo |
| email | VARCHAR UNIQUE | Email (login) |
| senha | VARCHAR | Senha (BCrypt) |
| cpf | VARCHAR(11) | CPF |
| data_nascimento | DATE | Data de nascimento |
| telefone | VARCHAR | Telefone |
| logradouro, numero, complemento, bairro, cidade, estado, cep | VARCHAR | Endereco |
| ativo | BOOLEAN | Status ativo/inativo |

**tb_funcionarios**

| Coluna | Tipo | Descricao |
|--------|------|-----------|
| id | BIGSERIAL PK | ID do funcionario |
| nome_completo | VARCHAR | Nome completo |
| email | VARCHAR UNIQUE | Email (login) |
| senha | VARCHAR | Senha (BCrypt) |
| cpf | VARCHAR(11) | CPF |
| tipo | VARCHAR | ADMIN, MEDICO, ENFERMEIRO, TECNICO_LABORATORIO, ATENDENTE |
| crm | VARCHAR | CRM (obrigatorio para MEDICO) |
| coren | VARCHAR | COREN (obrigatorio para ENFERMEIRO) |
| especialidade_nome | VARCHAR | Nome da especialidade (MEDICO) |
| especialidade_codigo | VARCHAR | Codigo da especialidade (MEDICO) |
| ativo | BOOLEAN | Status |
| data_cadastro | TIMESTAMP | Data de criacao |

### 16.3 db_agendamento

**tb_consultas**

| Coluna | Tipo | Descricao |
|--------|------|-----------|
| id | BIGSERIAL PK | ID da consulta |
| paciente_id | BIGINT | Referencia ao paciente |
| medico_id | BIGINT | Referencia ao medico |
| data_hora | TIMESTAMP | Data e hora da consulta |
| especialidade | VARCHAR | Especialidade medica |
| status | VARCHAR | AGENDADA, CANCELADA, REALIZADA |
| tipo_consulta | VARCHAR(20) | REGULAR, ENCAIXE (default: REGULAR) |
| prioridade | VARCHAR(20) | EMERGENCIA, URGENTE, POUCO_URGENTE, NAO_URGENTE (nullable) |
| triagem_id | BIGINT | ID da triagem que originou (nullable) |

**tb_agendas**

| Coluna | Tipo | Descricao |
|--------|------|-----------|
| id | BIGSERIAL PK | ID da agenda |
| medico_id | BIGINT | ID do medico |
| dia_semana | VARCHAR | MONDAY, TUESDAY, ... |
| hora_inicio | TIME | Inicio do expediente |
| hora_fim | TIME | Fim do expediente |
| duracao_slot_minutos | INT | Duracao de cada slot |
| especialidade | VARCHAR | Especialidade |
| ativa | BOOLEAN | Agenda ativa/inativa |

**tb_horarios_disponiveis**

| Coluna | Tipo | Descricao |
|--------|------|-----------|
| id | BIGSERIAL PK | ID do horario |
| agenda_id | BIGINT FK | Referencia a agenda |
| medico_id | BIGINT | ID do medico |
| data_hora | TIMESTAMP UNIQUE(medico) | Data/hora do slot |
| especialidade | VARCHAR | Especialidade |
| ocupado | BOOLEAN | Slot esta reservado |
| consulta_id | BIGINT | Consulta vinculada (nullable) |

### 16.4 db_atendimento

**tb_atendimentos**

| Coluna | Tipo | Descricao |
|--------|------|-----------|
| id | BIGSERIAL PK | ID do atendimento |
| consulta_id | BIGINT UNIQUE | Consulta atendida |
| paciente_id | BIGINT | Paciente |
| medico_id | BIGINT | Medico responsavel |
| anamnese | TEXT | Relato do paciente (nullable) |
| conduta_medica | TEXT | Decisao medica (nullable) |
| data_hora_inicio | TIMESTAMP | Inicio do atendimento |
| data_hora_fim | TIMESTAMP | Fim do atendimento (nullable) |
| status | VARCHAR | EM_ANDAMENTO, FINALIZADO |

### 16.5 db_exames

**tb_tipos_exame**

| Coluna | Tipo | Descricao |
|--------|------|-----------|
| id | BIGSERIAL PK | ID do tipo |
| nome | VARCHAR | Nome do exame (ex: Hemograma) |
| codigo | VARCHAR UNIQUE | Codigo unico (ex: HEMO) |
| descricao | TEXT | Descricao detalhada |
| preparacao | TEXT | Instrucoes de preparo |
| ativo | BOOLEAN | Status |

**tb_solicitacoes_exame**

| Coluna | Tipo | Descricao |
|--------|------|-----------|
| id | BIGSERIAL PK | ID da solicitacao |
| paciente_id | BIGINT | Paciente |
| medico_id | BIGINT | Medico solicitante |
| tipo_exame_id | BIGINT FK | Tipo de exame |
| atendimento_id | BIGINT | Atendimento origem (nullable) |
| consulta_id | BIGINT | Consulta origem (nullable) |
| prioridade | VARCHAR | NORMAL, URGENTE |
| observacoes | TEXT | Observacoes medicas |
| status | VARCHAR | PENDENTE, AGENDADA, REALIZADA, CANCELADA |
| data_criacao | TIMESTAMP | Data de criacao |

**tb_agendas_exame**

| Coluna | Tipo | Descricao |
|--------|------|-----------|
| id | BIGSERIAL PK | ID da agenda |
| tipo_exame_id | BIGINT FK | Tipo de exame |
| dia_semana | VARCHAR | Dia da semana |
| hora_inicio | TIME | Inicio |
| hora_fim | TIME | Fim |
| duracao_slot_minutos | INT | Duracao do slot |
| vagas_por_slot | INT | Vagas simultaneas |
| ativa | BOOLEAN | Status |

**tb_agendamentos_exame**

| Coluna | Tipo | Descricao |
|--------|------|-----------|
| id | BIGSERIAL PK | ID do agendamento |
| solicitacao_exame_id | BIGINT FK | Solicitacao vinculada |
| tipo_exame_id | BIGINT FK | Tipo de exame |
| data_hora | TIMESTAMP | Data/hora agendada |
| status | VARCHAR | AGENDADO, CANCELADO, REALIZADO |
| data_criacao | TIMESTAMP | Data de criacao |

### 16.6 db_historico

**tb_historico_consultas**

| Coluna | Tipo | Descricao |
|--------|------|-----------|
| id | BIGSERIAL PK | ID |
| consulta_id | BIGINT UNIQUE | Consulta original |
| paciente_id | BIGINT | Paciente |
| medico_id | BIGINT | Medico |
| data_hora | TIMESTAMP | Data/hora |
| especialidade | VARCHAR | Especialidade |
| status | VARCHAR | AGENDADA, CANCELADA, REALIZADA |
| observacoes | TEXT | Observacoes (nullable) |
| data_criacao | TIMESTAMP | Criacao |
| data_atualizacao | TIMESTAMP | Ultima atualizacao |

**tb_historico_triagens**

| Coluna | Tipo | Descricao |
|--------|------|-----------|
| id | BIGSERIAL PK | ID |
| triagem_id | VARCHAR UNIQUE | ID da triagem (UUID) |
| paciente_id | BIGINT | Paciente |
| funcionario_id | BIGINT | Enfermeiro |
| pressao_arterial | VARCHAR | Pressao (ex: 120/80) |
| temperatura | DOUBLE | Temperatura corporal |
| batimento_cardiaco | INT | Batimentos por minuto |
| conduta | TEXT | Conduta de enfermagem |
| data_registro | TIMESTAMP | Data do registro |

### 16.7 Migrations

| Modulo | Migration | Descricao |
|--------|-----------|-----------|
| autenticacao | V1 | Cria tb_usuarios |
| autenticacao | V2 | Insere usuarios iniciais (admin, medico, enfermeiro, paciente) |
| autenticacao | V3 | Cria tb_pacientes e tb_funcionarios |
| autenticacao | V4 | Migra usuarios das tabelas antigas |
| autenticacao | V5 | Remove tb_usuarios |
| autenticacao | V6 | Adiciona tipos TECNICO_LABORATORIO e ATENDENTE |
| agendamento | V1 | Cria tb_consultas |
| agendamento | V2 | Cria tb_agendas e tb_horarios_disponiveis |
| agendamento | V3 | Cria tabelas de exame (movidas depois) |
| agendamento | V4 | Insere tipos de exame iniciais (movidos depois) |
| agendamento | V5 | Remove tabelas de exame (migradas para modulo exames) |
| agendamento | V6 | Adiciona tipo_consulta, prioridade, triagem_id em tb_consultas |
| atendimento | V1 | Cria tb_atendimentos e tb_exames_solicitados |
| atendimento | V2 | Remove tb_exames_solicitados (migrada para modulo exames) |
| exames | V1 | Cria tb_tipos_exame |
| exames | V2 | Cria tb_solicitacoes_exame |
| exames | V3 | Cria tb_agendas_exame |
| exames | V4 | Cria tb_agendamentos_exame |
| exames | V5 | Insere tipos de exame iniciais |
| historico | V1 | Cria tb_historico_consultas |
| historico | V2 | Cria tb_historico_triagens |

---

## 17. Mapa Completo de Endpoints

Todos os endpoints sao acessados via Gateway na porta **8080** com o prefixo do servico.

### Autenticacao (`/autenticacao/...`)

| Metodo | Endpoint | Role | Descricao |
|--------|----------|------|-----------|
| POST | `/autenticacao/auth/login` | Publico | Login (retorna JWT) |
| POST | `/autenticacao/auth/service/token` | Publico | Login service-to-service |
| POST | `/autenticacao/pacientes` | ADMIN | Cadastrar paciente |
| POST | `/autenticacao/funcionarios` | ADMIN | Cadastrar funcionario |
| GET | `/autenticacao/funcionarios/{id}` | ADMIN | Buscar funcionario por ID |

**Endpoints internos (service-to-service):**

| Metodo | Endpoint | Role | Descricao |
|--------|----------|------|-----------|
| GET | `/internal/usuarios/pacientes/{id}/exists` | SISTEMA | Verificar se paciente existe |
| GET | `/internal/usuarios/funcionarios/{id}/exists` | SISTEMA | Verificar se funcionario existe |
| GET | `/internal/usuarios/funcionarios/{id}/is-medico` | SISTEMA | Verificar se eh medico |
| GET | `/internal/usuarios/funcionarios/{id}/is-enfermeiro` | SISTEMA | Verificar se eh enfermeiro |

### Agendamento - Consultas (`/agendamento/...`)

| Metodo | Endpoint | Role | Descricao |
|--------|----------|------|-----------|
| POST | `/agendamento/agendamento` | MEDICO, ENFERMEIRO | Agendar consulta |
| PUT | `/agendamento/agendamento/{id}` | MEDICO, ENFERMEIRO | Reagendar consulta |
| DELETE | `/agendamento/agendamento/{id}` | MEDICO, ENFERMEIRO | Cancelar consulta |
| GET | `/agendamento/agendamento/paciente/{pacienteId}` | MEDICO, ENFERMEIRO, PACIENTE | Listar consultas do paciente |
| GET | `/agendamento/agendamento/consultas` | Autenticado | Listar consultas futuras |

### Agendamento - Agenda Medica (`/agendamento/agenda/...`)

| Metodo | Endpoint | Role | Descricao |
|--------|----------|------|-----------|
| POST | `/agendamento/agenda` | MEDICO, ADMIN | Criar agenda |
| DELETE | `/agendamento/agenda/{id}` | MEDICO, ADMIN | Desativar agenda |
| GET | `/agendamento/agenda/medico/{medicoId}` | MEDICO, ADMIN | Listar agendas do medico |
| POST | `/agendamento/agenda/{id}/gerar-horarios` | MEDICO, ADMIN | Gerar horarios disponiveis |

### Agendamento - Horarios (`/agendamento/horarios/...`)

| Metodo | Endpoint | Role | Descricao |
|--------|----------|------|-----------|
| GET | `/agendamento/horarios/disponiveis` | Autenticado | Buscar horarios (params: especialidade, dataInicio, dataFim) |
| POST | `/agendamento/horarios/autoagendamento` | PACIENTE, ATENDENTE | Auto-agendar em slot disponivel |

**Endpoints internos (Agendamento):**

| Metodo | Endpoint | Role | Descricao |
|--------|----------|------|-----------|
| GET | `/internal/consultas/{id}` | - | Buscar consulta por ID |
| PATCH | `/internal/consultas/{id}/realizada` | - | Marcar consulta como realizada |

### Triagem (`/triagem/...`)

| Metodo | Endpoint | Role | Descricao |
|--------|----------|------|-----------|
| POST | `/triagem/triagem` | Autenticado | Realizar triagem com sinais vitais e especialidade |

### Atendimento (`/atendimento/...`)

| Metodo | Endpoint | Role | Descricao |
|--------|----------|------|-----------|
| POST | `/atendimento/atendimentos` | MEDICO | Iniciar atendimento |
| PATCH | `/atendimento/atendimentos/{id}/finalizar` | MEDICO | Finalizar atendimento |
| POST | `/atendimento/atendimentos/{id}/exames` | MEDICO | Solicitar exame durante atendimento |
| GET | `/atendimento/atendimentos/{id}` | MEDICO, ENFERMEIRO | Buscar atendimento por ID |
| GET | `/atendimento/atendimentos/consulta/{consultaId}` | MEDICO, ENFERMEIRO | Buscar atendimento por consulta |

### Exames (`/exames/...`)

| Metodo | Endpoint | Role | Descricao |
|--------|----------|------|-----------|
| POST | `/exames/tipos` | ADMIN | Criar tipo de exame |
| GET | `/exames/tipos` | Autenticado | Listar tipos de exame |
| GET | `/exames/tipos/{id}` | Autenticado | Buscar tipo por ID |
| POST | `/exames/solicitacoes` | MEDICO, SISTEMA | Solicitar exame |
| GET | `/exames/solicitacoes/paciente/{pacienteId}` | MEDICO, PACIENTE, ADMIN | Listar solicitacoes do paciente |
| GET | `/exames/solicitacoes/atendimento/{atendimentoId}` | MEDICO, ENFERMEIRO, ADMIN | Listar solicitacoes por atendimento |
| DELETE | `/exames/solicitacoes/{id}` | MEDICO, ADMIN | Cancelar solicitacao |
| POST | `/exames/agendamentos/agenda` | ADMIN | Criar agenda de exame |
| GET | `/exames/agendamentos/vagas` | Autenticado | Buscar vagas (params: tipoExameId, dataInicio, dataFim) |
| POST | `/exames/agendamentos` | PACIENTE, ATENDENTE, ADMIN | Agendar exame |
| DELETE | `/exames/agendamentos/{id}` | PACIENTE, ADMIN | Cancelar agendamento de exame |

### Historico (`/historico/...`)

| Metodo | Endpoint | Role | Descricao |
|--------|----------|------|-----------|
| POST | `/historico/graphql` | MEDICO, ENFERMEIRO, PACIENTE | Consultar historico via GraphQL |
| GET | `/historico/graphiql` | - | Interface grafica GraphiQL |

---

## 18. Regras de Negocio

### Consultas

| Regra | Descricao |
|-------|-----------|
| Data futura | Toda consulta regular deve ter data/hora no futuro |
| Cancelamento 24h | Nao permite cancelar com menos de 24 horas de antecedencia |
| Excecao encaixe | Consultas de tipo ENCAIXE nao se aplicam a regra de 24h |
| Consulta cancelada | Nao permite reagendar consulta ja cancelada |
| Acesso paciente | Paciente so ve suas proprias consultas |
| Slot unico | Um slot so pode ser reservado por um paciente (lock pessimista) |
| Liberacao automatica | Cancelar consulta libera o slot vinculado |
| Idempotencia triagem | Se ja existe consulta com mesmo triagemId, retorna a existente |

### Exames

| Regra | Descricao |
|-------|-----------|
| Solicitacao medica | Apenas medico (ou sistema) pode solicitar exame |
| Agendamento sobre pendente | So permite agendar exame com solicitacao PENDENTE |
| Vagas por slot | Multiplos pacientes podem agendar no mesmo horario (ate `vagasPorSlot`) |
| Cancelamento retorna pendente | Cancelar agendamento retorna solicitacao para PENDENTE |
| Data futura | Agendamento de exame deve ser em data futura |

### Triagem

| Regra | Descricao |
|-------|-----------|
| Temperatura | Entre 30.0°C e 45.0°C |
| Batimento cardiaco | Entre 30 e 250 bpm |
| Classificacao automatica | Prioridade calculada com base nos sinais vitais |
| Especialidade obrigatoria | Campo especialidade eh obrigatorio |
| Funcionario validado | Funcionario deve existir e ser enfermeiro |

### Usuarios

| Regra | Descricao |
|-------|-----------|
| Email unico | Nao permite dois usuarios com mesmo email |
| CPF 11 digitos | CPF deve ter exatamente 11 digitos |
| Senha minima | Minimo 6 caracteres |
| Idade valida | Entre 0 e 120 anos |
| CEP 8 digitos | CEP deve ter exatamente 8 digitos |
| Tipo determina campos | CRM/COREN/Especialidade validados conforme tipo de funcionario |

---

## 19. Decisoes Arquiteturais

### Clean Architecture (Hexagonal)

Cada microsservico segue rigorosamente:

```
domain/          Entidades, value objects, excecoes - Java puro, ZERO imports de framework
    |
application/     Use cases, ports (interfaces), DTOs - depende apenas do domain
    |
infrastructure/  Controllers, JPA, security, messaging, config - implementa os ports
```

- Entidades de dominio sao separadas das JPA entities
- Mapping domain <-> JPA entity acontece exclusivamente nos adapters
- Beans wired via `BeansConfiguration` (nao via `@Service` nas classes de application)

### Database per Service

Cada microsservico possui seu proprio banco de dados. Nao ha queries cross-database. A consistencia eventual eh garantida via eventos RabbitMQ.

### Slots Persistidos vs Vagas Computadas

| Aspecto | Consultas (slots) | Exames (vagas) |
|---------|-------------------|----------------|
| Armazenamento | Persistidos em `tb_horarios_disponiveis` | Computados em tempo real |
| Concorrencia | `SELECT FOR UPDATE` (lock pessimista) | N vagas por slot, menor contencao |
| Razao | 1 vaga por slot = alta contencao, precisa lock | N vagas por slot = contencao diluida |

### Comunicacao

| Tipo | Uso |
|------|-----|
| Sincrona (REST) | Gateway -> servicos; Servico -> Autenticacao (validar usuario) |
| Assincrona (RabbitMQ) | Agendamento -> Historico/Notificacoes; Triagem -> Agendamento/Historico; Atendimento -> Exames |

A comunicacao assincrona garante que o servico produtor nao fica bloqueado esperando consumidores processarem. Se um consumidor estiver fora do ar, as mensagens ficam na fila e sao processadas quando voltar.

### Service-to-Service Authentication

Servicos se autenticam com `serviceId` + `serviceSecret` para obter JWT com `ROLE_SISTEMA`. Esse token eh usado para acessar endpoints `/internal/**`. As credenciais sao injetadas via variaveis de ambiente.

---

## 20. Testes com Postman

O projeto inclui uma collection Postman completa para testar todos os fluxos:

**Arquivo:** `Digi-SUS v3 - Testes Completos.postman_collection.json`

### Como Usar

1. Importe a collection no Postman
2. Certifique-se de que todos os servicos estao rodando (via Docker Compose)
3. Aguarde todos os servicos aparecerem como UP no Eureka (http://localhost:8761)
4. Execute as pastas na ordem numerica (0 a 12) - cada pasta depende de tokens e IDs gerados nas anteriores

### Pastas da Collection

| Pasta | Descricao |
|-------|-----------|
| 0. Login | Login dos 4 usuarios (admin, medico, enfermeiro, paciente) |
| 1. Cadastro | Cadastro de novo paciente e funcionarios |
| 2. Agendamento Tradicional | CRUD completo de consultas |
| 3. Agenda Medica | Criar agenda, gerar horarios, buscar disponiveis |
| 4. Auto-Agendamento | Paciente se auto-agenda em slot disponivel |
| 5. Cancelamento | Cancelamento de consulta com liberacao de slot |
| 6. Triagem - Classificacao | Testa as 4 classificacoes de prioridade |
| 7. Triagem - Integracao | Triagem cria consulta automatica no agendamento |
| 8. Exames | Fluxo completo de tipos, solicitacoes, agendas e agendamentos |
| 9. Atendimento | Iniciar e finalizar atendimento medico |
| 10. Historico | Consultar historico via GraphQL |
| 11. Testes Negativos | Validacoes de erro e acesso negado |
| 12. Consultas Futuras | Listar consultas futuras por role |

**Variavel base:** `{{baseUrl}}` = `http://localhost:8080`
