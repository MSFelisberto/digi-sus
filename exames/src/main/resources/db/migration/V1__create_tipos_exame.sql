CREATE TABLE tb_tipos_exame (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    descricao TEXT,
    preparacao TEXT,
    ativo BOOLEAN NOT NULL DEFAULT true
);
