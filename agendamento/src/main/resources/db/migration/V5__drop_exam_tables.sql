-- Drop das tabelas de exames que foram movidas para o microsserviço exames
DROP TABLE IF EXISTS tb_agendamentos_exame CASCADE;
DROP TABLE IF EXISTS tb_agendas_exame CASCADE;
DROP TABLE IF EXISTS tb_solicitacoes_exame CASCADE;
DROP TABLE IF EXISTS tb_tipos_exame CASCADE;
