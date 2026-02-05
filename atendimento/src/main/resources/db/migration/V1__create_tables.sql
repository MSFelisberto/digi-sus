CREATE TABLE tb_atendimentos (
    id BIGSERIAL PRIMARY KEY,
    consulta_id BIGINT NOT NULL UNIQUE,
    paciente_id BIGINT NOT NULL,
    medico_id BIGINT NOT NULL,
    anamnese TEXT,
    conduta_medica TEXT,
    data_hora_inicio TIMESTAMP NOT NULL,
    data_hora_fim TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'EM_ANDAMENTO'
);

CREATE TABLE tb_exames_solicitados (
    id BIGSERIAL PRIMARY KEY,
    atendimento_id BIGINT NOT NULL REFERENCES tb_atendimentos(id),
    tipo_exame VARCHAR(100) NOT NULL,
    prioridade VARCHAR(20) NOT NULL,
    observacoes TEXT
);

CREATE INDEX idx_atendimentos_consulta ON tb_atendimentos(consulta_id);
CREATE INDEX idx_atendimentos_paciente ON tb_atendimentos(paciente_id);
CREATE INDEX idx_atendimentos_medico ON tb_atendimentos(medico_id);
CREATE INDEX idx_exames_atendimento ON tb_exames_solicitados(atendimento_id);
