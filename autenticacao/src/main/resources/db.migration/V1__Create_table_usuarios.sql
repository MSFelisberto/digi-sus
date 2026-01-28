CREATE TABLE tb_usuarios (
                             id BIGSERIAL PRIMARY KEY,
                             email VARCHAR(255) NOT NULL UNIQUE,
                             senha VARCHAR(255) NOT NULL,
                             perfil VARCHAR(50) NOT NULL
);