CREATE TABLE tb_historico_triagens (
    id BIGSERIAL PRIMARY KEY,
    triagem_id BIGINT NOT NULL UNIQUE,
    paciente_id BIGINT NOT NULL,
    funcionario_id BIGINT NOT NULL,
    pressao_arterial VARCHAR(20) NOT NULL,
    temperatura DECIMAL(4,1) NOT NULL,
    batimento_cardiaco INTEGER NOT NULL,
    conduta TEXT NOT NULL,
    data_registro TIMESTAMP NOT NULL
);

CREATE INDEX idx_historico_triagens_paciente ON tb_historico_triagens(paciente_id);
CREATE INDEX idx_historico_triagens_funcionario ON tb_historico_triagens(funcionario_id);
CREATE INDEX idx_historico_triagens_data ON tb_historico_triagens(data_registro);
