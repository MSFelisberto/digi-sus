-- Tabela de Pacientes
CREATE TABLE tb_pacientes
(
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    senha           VARCHAR(255) NOT NULL,
    nome_completo   VARCHAR(100) NOT NULL,
    cpf             VARCHAR(11)  NOT NULL UNIQUE,
    data_nascimento DATE         NOT NULL,
    telefone        VARCHAR(20)  NOT NULL,
    logradouro      VARCHAR(200) NOT NULL,
    numero          VARCHAR(10)  NOT NULL,
    complemento     VARCHAR(100),
    bairro          VARCHAR(100) NOT NULL,
    cidade          VARCHAR(100) NOT NULL,
    estado          VARCHAR(2)   NOT NULL,
    cep             VARCHAR(8)   NOT NULL,
    ativo           BOOLEAN      NOT NULL DEFAULT true,
    created_at      TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP             DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de Funcionários
CREATE TABLE tb_funcionarios
(
    id                   BIGSERIAL PRIMARY KEY,
    email                VARCHAR(255) NOT NULL UNIQUE,
    senha                VARCHAR(255) NOT NULL,
    tipo                 VARCHAR(20)  NOT NULL CHECK (tipo IN ('ADMIN', 'MEDICO', 'ENFERMEIRO')),
    nome_completo        VARCHAR(100) NOT NULL,
    cpf                  VARCHAR(11)  NOT NULL UNIQUE,
    crm                  VARCHAR(20) UNIQUE,
    coren                VARCHAR(20) UNIQUE,
    especialidade_nome   VARCHAR(100),
    especialidade_codigo VARCHAR(10),
    ativo                BOOLEAN      NOT NULL DEFAULT true,
    data_cadastro        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at           TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP             DEFAULT CURRENT_TIMESTAMP
);

-- Criar índices para melhor performance
CREATE INDEX idx_pacientes_email ON tb_pacientes (email);
CREATE INDEX idx_pacientes_cpf ON tb_pacientes (cpf);
CREATE INDEX idx_funcionarios_email ON tb_funcionarios (email);
CREATE INDEX idx_funcionarios_cpf ON tb_funcionarios (cpf);
CREATE INDEX idx_funcionarios_crm ON tb_funcionarios (crm);
CREATE INDEX idx_funcionarios_coren ON tb_funcionarios (coren);
CREATE INDEX idx_funcionarios_tipo ON tb_funcionarios (tipo);