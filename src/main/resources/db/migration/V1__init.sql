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

CREATE TABLE usuario (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    telefone VARCHAR(20),
    senha VARCHAR(255) NOT NULL,
    servico_id UUID,
    cidade_id UUID,
    CONSTRAINT fk_usuario_servico FOREIGN KEY (servico_id) REFERENCES servicos (id),
    CONSTRAINT fk_usuario_cidade FOREIGN KEY (cidade_id) REFERENCES cidade (id)
);
