CREATE TABLE papel (
    id UUID PRIMARY KEY,
    nome VARCHAR(10)
);

--ALTER TABLE usuario ADD COLUMN papel_id UUID;

-- Criar FK
ALTER TABLE usuario
    ADD CONSTRAINT fk_usuario_papel FOREIGN KEY (papel_id) REFERENCES papel (id);

INSERT INTO papel (id, nome)
VALUES
    ('550e8400-e29b-41d4-a716-446655440003', 'USER'),
    ('550e8400-e29b-41d4-a716-446655440004', 'ADMIN');

INSERT INTO servicos (id, nome) VALUES 
    ('550e8400-e29b-41d4-a716-446655440005', 'Defesa Civil'),
    ('550e8400-e29b-41d4-a716-446655440006', 'Corpo de Bombeiros'),
    ('550e8400-e29b-41d4-a716-446655440007', 'SAMU - Emergência'),
    ('550e8400-e29b-41d4-a716-446655440008', 'Monitoramento Hidrológico'),
    ('550e8400-e29b-41d4-a716-446655440009', 'Gestão de Abrigos');

-- Cidades reais (Brasil)
INSERT INTO cidade (id, nome, estado) VALUES
    ('550e8400-e29b-41d4-a716-446655440010', 'Porto Alegre', 'Rio Grande do Sul'),
    ('550e8400-e29b-41d4-a716-446655440011', 'Rio de Janeiro', 'Rio de Janeiro'),
    ('550e8400-e29b-41d4-a716-446655440012', 'São Paulo', 'São Paulo'),
    ('550e8400-e29b-41d4-a716-446655440013', 'Manaus', 'Amazonas'),
    ('550e8400-e29b-41d4-a716-446655440014', 'Recife', 'Pernambuco');

INSERT INTO usuario (id, nome, cpf, email, telefone, senha, servico_id, cidade_id, papel_id) VALUES ('550e8400-e29b-41d4-a716-446655440000', 'Abner', '123.456.789-00', 'abner@naoinfomado.com', '5511987654321', '$2a$10$cfFYnikRgfxgkq6I44oxeORd6Ud0PbK79OkLY2gv6URUEAdudGA9.', NULL, NULL, '550e8400-e29b-41d4-a716-446655440004' );


INSERT INTO usuario (id, nome, cpf, email, telefone, senha, servico_id, cidade_id, papel_id)
VALUES (
    '550e8400-e29b-41d4-a716-446655440001',    
    'User Local',
    '123.456.789-10',
    'user@teste.com',
    '5511987654321',
    '$2a$10$cfFYnikRgfxgkq6I44oxeORd6Ud0PbK79OkLY2gv6URUEAdudGA9.', 
    '550e8400-e29b-41d4-a716-446655440005', 
    '550e8400-e29b-41d4-a716-446655440010', 
    '550e8400-e29b-41d4-a716-446655440003'  
    );