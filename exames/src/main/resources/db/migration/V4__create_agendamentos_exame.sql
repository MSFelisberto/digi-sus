CREATE TABLE tb_agendamentos_exame (
    id BIGSERIAL PRIMARY KEY,
    solicitacao_exame_id BIGINT NOT NULL REFERENCES tb_solicitacoes_exame(id),
    tipo_exame_id BIGINT NOT NULL REFERENCES tb_tipos_exame(id),
    data_hora TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AGENDADO',
    data_criacao TIMESTAMP NOT NULL
);

CREATE INDEX idx_agendamentos_data ON tb_agendamentos_exame(data_hora);
CREATE INDEX idx_agendamentos_solicitacao ON tb_agendamentos_exame(solicitacao_exame_id);
