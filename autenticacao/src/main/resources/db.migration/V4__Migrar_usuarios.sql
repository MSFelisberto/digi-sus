
-- Migrar pacientes da tabela antiga para a nova
INSERT INTO tb_pacientes (email, senha, nome_completo, cpf, data_nascimento, telefone,
                          logradouro, numero, bairro, cidade, estado, cep)
SELECT
    email,
    senha,
    'Paciente Teste',
    '11111111' || LPAD(id::text, 3, '0'),
    '1990-01-01',
    '(11) 99999-9999',
    'Rua Exemplo',
    '123',
    'Centro',
    'São Paulo',
    'SP',
    '01000000'
FROM tb_usuarios
WHERE perfil = 'PACIENTE';


-- Migrar funcionários da tabela antiga para a nova
INSERT INTO tb_funcionarios (email, senha, tipo, nome_completo, cpf, crm, coren,
                             especialidade_nome, especialidade_codigo)
SELECT
    email,
    senha,
    perfil::VARCHAR,
    CASE
        WHEN perfil = 'ADMIN' THEN 'Administrador Sistema'
        WHEN perfil = 'MEDICO' THEN 'Dr. Médico'
        WHEN perfil = 'ENFERMEIRO' THEN 'Enfermeiro(a)'
        ELSE 'Funcionário'
        END,
    CASE
        WHEN perfil = 'ADMIN' THEN '22222222' || LPAD(id::text, 3, '0')
        WHEN perfil = 'MEDICO' THEN '33333333' || LPAD(id::text, 3, '0')
        WHEN perfil = 'ENFERMEIRO' THEN '44444444' || LPAD(id::text, 3, '0')
        ELSE '55555555' || LPAD(id::text, 3, '0')
        END,
    CASE
        WHEN perfil = 'MEDICO' THEN 'CRM' || LPAD(id::text, 6, '0') || '-SP'
        ELSE NULL
        END,
    CASE
        WHEN perfil = 'ENFERMEIRO' THEN 'COREN' || LPAD(id::text, 6, '0') || '-SP'
        ELSE NULL
        END,
    CASE WHEN perfil = 'MEDICO' THEN 'CLINICA GERAL' ELSE NULL END,
    CASE WHEN perfil = 'MEDICO' THEN 'CG001' ELSE NULL END
FROM tb_usuarios
WHERE perfil IN ('ADMIN', 'MEDICO', 'ENFERMEIRO');


