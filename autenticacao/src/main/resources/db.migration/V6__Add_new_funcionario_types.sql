ALTER TABLE tb_funcionarios DROP CONSTRAINT IF EXISTS tb_funcionarios_tipo_check;
ALTER TABLE tb_funcionarios ADD CONSTRAINT tb_funcionarios_tipo_check
      CHECK ( tipo IN ('ADMIN', 'MEDICO', 'ENFERMEIRO', 'TECNICO_LABORATORIO', 'ATENDENTE') );