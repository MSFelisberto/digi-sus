CREATE TABLE tb_solicitacoes_exame (
    id BIGSERIAL PRIMARY KEY,
    paciente_id BIGINT NOT NULL,
    medico_id BIGINT NOT NULL,
    tipo_exame_id BIGINT NOT NULL REFERENCES tb_tipos_exame(id),
    atendimento_id BIGINT,
    consulta_id BIGINT,
    prioridade VARCHAR(20) NOT NULL,
    observacoes TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    data_criacao TIMESTAMP NOT NULL
);

CREATE INDEX idx_solicitacoes_paciente ON tb_solicitacoes_exame(paciente_id);
CREATE INDEX idx_solicitacoes_atendimento ON tb_solicitacoes_exame(atendimento_id);
CREATE INDEX idx_solicitacoes_tipo ON tb_solicitacoes_exame(tipo_exame_id);
