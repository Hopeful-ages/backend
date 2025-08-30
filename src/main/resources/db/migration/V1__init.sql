CREATE TABLE cidade (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    estado VARCHAR(100) NOT NULL
);

CREATE TABLE cobrade (
    id UUID PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL,
    descricao VARCHAR(255),
    grupo VARCHAR(100),
    subgrupo VARCHAR(100),
    tipo VARCHAR(100),
    subtipo VARCHAR(100)
);

CREATE TABLE servicos (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL
);

CREATE TABLE cenarios (
    id UUID PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    origem VARCHAR(100),
    cidade_id UUID,
    cobrade_id UUID,
    CONSTRAINT fk_cidade FOREIGN KEY (cidade_id) REFERENCES cidade (id),
    CONSTRAINT fk_cobrade FOREIGN KEY (cobrade_id) REFERENCES cobrade (id)
);

CREATE TABLE tarefas (
    id UUID PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    fase VARCHAR(100),
    servico_id UUID,
    cenario_id UUID,
    data DATE,
    CONSTRAINT fk_servico FOREIGN KEY (servico_id) REFERENCES servicos (id),
    CONSTRAINT fk_cenario FOREIGN KEY (cenario_id) REFERENCES cenarios (id)
);

CREATE TABLE criticidades (
    id UUID PRIMARY KEY,
    parametro VARCHAR(100) NOT NULL,
    acao VARCHAR(255),
    cenario_id UUID,
    CONSTRAINT fk_cenario_criticidade FOREIGN KEY (cenario_id) REFERENCES cenarios (id)
);

CREATE TABLE funcao (
    id UUID PRIMARY KEY,
    nome VARCHAR(10)
);

CREATE TABLE usuario (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    telefone VARCHAR(20),
    senha VARCHAR(255) NOT NULL,
    servico_id UUID,
    cidade_id UUID,
    funcao_id UUID,
    CONSTRAINT fk_usuario_servico FOREIGN KEY (servico_id) REFERENCES servicos (id),
    CONSTRAINT fk_usuario_cidade FOREIGN KEY (cidade_id) REFERENCES cidade (id),
    CONSTRAINT fk_usuario_funcao FOREIGN KEY (funcao_id) REFERENCES funcao (id)
);
-- Inserir cidades
INSERT INTO cidade (id, nome, estado)
VALUES 
    ('550e8400-e29b-41d4-a716-446655440002', 'Cidade Exemplo', 'Estado Exemplo');

INSERT INTO funcao (id, nome)
VALUES
    ('550e8400-e29b-41d4-a716-446655440003', 'USER'),
    ('550e8400-e29b-41d4-a716-446655440004', 'ADMIN');

-- Inserir serviços
INSERT INTO servicos (id, nome)
VALUES 
    ('550e8400-e29b-41d4-a716-446655440001', 'Serviço Exemplo');

-- Agora insere o usuário
INSERT INTO usuario (id, nome, cpf, email, telefone, senha, servico_id, cidade_id, funcao_id)
VALUES (
    '550e8400-e29b-41d4-a716-446655440000',    
    'Abner',
    '123.456.789-00',
    'abner@naoinfomado.com',
    '5511987654321',
    '$2a$10$cfFYnikRgfxgkq6I44oxeORd6Ud0PbK79OkLY2gv6URUEAdudGA9.', -- senha "Senha" com BCrypt
    '550e8400-e29b-41d4-a716-446655440001', -- servico_id já existe
    '550e8400-e29b-41d4-a716-446655440002',  -- cidade_id já existe
    '550e8400-e29b-41d4-a716-446655440004'
);