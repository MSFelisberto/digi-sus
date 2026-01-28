CREATE TABLE tb_historico_consultas
(
    id               BIGSERIAL PRIMARY KEY,
    consulta_id      BIGINT       NOT NULL UNIQUE,
    paciente_id      BIGINT       NOT NULL,
    medico_id        BIGINT       NOT NULL,
    data_hora        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    especialidade    VARCHAR(100) NOT NULL,
    status           VARCHAR(20)  NOT NULL,
    observacoes      TEXT,
    data_criacao     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    data_atualizacao TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT chk_status CHECK (status IN ('AGENDADA', 'CANCELADA', 'REALIZADA'))
);


CREATE INDEX idx_historico_consulta_id ON tb_historico_consultas(consulta_id);
CREATE INDEX idx_historico_paciente_id ON tb_historico_consultas(paciente_id);
CREATE INDEX idx_historico_medico_id ON tb_historico_consultas(medico_id);
CREATE INDEX idx_historico_data_hora ON tb_historico_consultas(data_hora);
CREATE INDEX idx_historico_status ON tb_historico_consultas(status);