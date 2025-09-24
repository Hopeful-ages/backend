-- Dados de teste para integração
-- Baseado na estrutura final após todas as migrações

-- Inserir dados de teste para COBRADE (Códigos de Desastres)
INSERT INTO cobrade (id, code, description, subgroup, type, subtype) VALUES
    ('550e8400-e29b-41d4-a716-446655440020', '1.2.1.0.0', 'Submersão de áreas fora dos limites normais de um curso de água em zonas que normalmente não se encontram submersas. O transbordamento ocorre de modo gradual, geralmente ocasionado por chuvas prolongadas em áreas de planície.', 'Inundações', NULL, NULL),
    ('550e8400-e29b-41d4-a716-446655440021', '1.2.2.0.0', 'Escoamento superficial de alta velocidade e energia, provocado por chuvas intensas e concentradas, normalmente em pequenas bacias de relevo acidentado. Caracterizada pela elevação súbita das vazões de determinada drenagem e transbordamento brusco da calha fluvial. Apresenta grande poder destrutivo.', 'Enxurradas', NULL, NULL),
    ('550e8400-e29b-41d4-a716-446655440022', '1.2.3.0.0', 'Extrapolação da capacidade de escoamento de sistemas de drenagem urbana e consequente acúmulo de água em ruas, calçadas ou outras infraestruturas urbanas, em decorrência de precipitações intensas.', 'Alagamentos', NULL, NULL),
    ('550e8400-e29b-41d4-a716-446655440023', '1.3.2.1.5', 'Forte deslocamento de uma massa de ar em uma região.', 'Tempestades', 'Tempestade local/Convectiva', 'Vendaval'),
    ('550e8400-e29b-41d4-a716-446655440024', '1.4.1.3.2', 'Propagação de fogo sem controle em vegetação fora de áreas protegidas, afetando a qualidade do ar.', 'Incêndio florestal', 'Incêndios em áreas não protegidas', NULL);

-- Inserir mais cidades para testes
INSERT INTO city (id, name, state) VALUES
    ('550e8400-e29b-41d4-a716-446655440015', 'Florianópolis', 'Santa Catarina'),
    ('550e8400-e29b-41d4-a716-446655440016', 'Brasília', 'Distrito Federal'),
    ('550e8400-e29b-41d4-a716-446655440017', 'Salvador', 'Bahia'),
    ('550e8400-e29b-41d4-a716-446655440018', 'Fortaleza', 'Ceará'),
    ('550e8400-e29b-41d4-a716-446655440019', 'Belo Horizonte', 'Minas Gerais');

-- Inserir mais serviços para testes
INSERT INTO services (id, name) VALUES 
    ('550e8400-e29b-41d4-a716-446655440025', 'Polícia Militar'),
    ('550e8400-e29b-41d4-a716-446655440026', 'Vigilância Sanitária'),
    ('550e8400-e29b-41d4-a716-446655440027', 'Assistência Social'),
    ('550e8400-e29b-41d4-a716-446655440028', 'Secretaria de Obras'),
    ('550e8400-e29b-41d4-a716-446655440029', 'Meteorologia');

-- Inserir cenários de teste (usando cidades criadas nesta migração)
INSERT INTO scenarios (id, description, origin, city_id, cobrade_id) VALUES
    ('550e8400-e29b-41d4-a716-446655440030', 'Inundação em Florianópolis', 'Chuva intensa', '550e8400-e29b-41d4-a716-446655440015', '550e8400-e29b-41d4-a716-446655440020'),
    ('550e8400-e29b-41d4-a716-446655440031', 'Enxurrada em Brasília', 'Temporal urbano', '550e8400-e29b-41d4-a716-446655440016', '550e8400-e29b-41d4-a716-446655440021'),
    ('550e8400-e29b-41d4-a716-446655440032', 'Alagamento em Salvador', 'Sistema de drenagem sobrecarregado', '550e8400-e29b-41d4-a716-446655440017', '550e8400-e29b-41d4-a716-446655440022'),
    ('550e8400-e29b-41d4-a716-446655440033', 'Vendaval em Fortaleza', 'Frente fria intensa', '550e8400-e29b-41d4-a716-446655440018', '550e8400-e29b-41d4-a716-446655440023'),
    ('550e8400-e29b-41d4-a716-446655440034', 'Incêndio Florestal em Belo Horizonte', 'Período de seca', '550e8400-e29b-41d4-a716-446655440019', '550e8400-e29b-41d4-a716-446655440024');

-- Inserir tarefas de teste
INSERT INTO tasks (id, description, phase, service_id, scenario_id, date) VALUES
    ('550e8400-e29b-41d4-a716-446655440035', 'Evacuação de moradores', 'Resposta', '550e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440030', '2024-01-15'),
    ('550e8400-e29b-41d4-a716-446655440036', 'Resgate em área alagada', 'Resposta', '550e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440031', '2024-01-16'),
    ('550e8400-e29b-41d4-a716-446655440037', 'Atendimento médico emergencial', 'Resposta', '550e8400-e29b-41d4-a716-446655440007', '550e8400-e29b-41d4-a716-446655440032', '2024-01-17'),
    ('550e8400-e29b-41d4-a716-446655440038', 'Monitoramento de níveis', 'Preparação', '550e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440030', '2024-01-14'),
    ('550e8400-e29b-41d4-a716-446655440039', 'Abertura de abrigo temporário', 'Resposta', '550e8400-e29b-41d4-a716-446655440009', '550e8400-e29b-41d4-a716-446655440033', '2024-01-18'),
    ('550e8400-e29b-41d4-a716-446655440040', 'Combate ao incêndio', 'Resposta', '550e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440034', '2024-01-19'),
    ('550e8400-e29b-41d4-a716-446655440041', 'Limpeza de vias públicas', 'Recuperação', '550e8400-e29b-41d4-a716-446655440028', '550e8400-e29b-41d4-a716-446655440032', '2024-01-20');

-- Inserir criticidades de teste
INSERT INTO criticalities (id, parameter, action, scenario_id) VALUES
    ('550e8400-e29b-41d4-a716-446655440042', 'Nível da água > 2m', 'Evacuação imediata', '550e8400-e29b-41d4-a716-446655440030'),
    ('550e8400-e29b-41d4-a716-446655440043', 'Velocidade do vento > 80km/h', 'Suspender operações externas', '550e8400-e29b-41d4-a716-446655440033'),
    ('550e8400-e29b-41d4-a716-446655440044', 'Área queimada > 100 hectares', 'Acionar reforços', '550e8400-e29b-41d4-a716-446655440034'),
    ('550e8400-e29b-41d4-a716-446655440045', 'Precipitação > 50mm/h', 'Alerta máximo', '550e8400-e29b-41d4-a716-446655440031'),
    ('550e8400-e29b-41d4-a716-446655440046', 'Número de desabrigados > 500', 'Ativar plano de contingência', '550e8400-e29b-41d4-a716-446655440032');

-- Inserir usuários de teste adicionais
INSERT INTO users (id, name, cpf, email, phone, password, service_id, city_id, role_id, account_status) VALUES 
    -- Usuários ADMIN
    ('550e8400-e29b-41d4-a716-446655440047', 'Ana Coordenadora', '111.222.333-44', 'ana.coord@defesacivil.gov.br', '51999887766', '$2a$10$cfFYnikRgfxgkq6I44oxeORd6Ud0PbK79OkLY2gv6URUEAdudGA9.', '550e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440015', '550e8400-e29b-41d4-a716-446655440004', true),
    ('550e8400-e29b-41d4-a716-446655440048', 'Carlos Supervisor', '222.333.444-55', 'carlos.super@bombeiros.gov.br', '21998776655', '$2a$10$cfFYnikRgfxgkq6I44oxeORd6Ud0PbK79OkLY2gv6URUEAdudGA9.', '550e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440016', '550e8400-e29b-41d4-a716-446655440004', true),
    
    -- Usuários USER
    ('550e8400-e29b-41d4-a716-446655440049', 'Maria Operadora', '333.444.555-66', 'maria.op@samu.gov.br', '11987665544', '$2a$10$cfFYnikRgfxgkq6I44oxeORd6Ud0PbK79OkLY2gv6URUEAdudGA9.', '550e8400-e29b-41d4-a716-446655440007', '550e8400-e29b-41d4-a716-446655440017', '550e8400-e29b-41d4-a716-446655440003', true),
    ('550e8400-e29b-41d4-a716-446655440050', 'João Técnico', '444.555.666-77', 'joao.tec@hidrolog.gov.br', '48976543210', '$2a$10$cfFYnikRgfxgkq6I44oxeORd6Ud0PbK79OkLY2gv6URUEAdudGA9.', '550e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440015', '550e8400-e29b-41d4-a716-446655440003', true),
    ('550e8400-e29b-41d4-a716-446655440051', 'Laura Assistente', '555.666.777-88', 'laura.assist@abrigos.gov.br', '61965432109', '$2a$10$cfFYnikRgfxgkq6I44oxeORd6Ud0PbK79OkLY2gv6URUEAdudGA9.', '550e8400-e29b-41d4-a716-446655440009', '550e8400-e29b-41d4-a716-446655440016', '550e8400-e29b-41d4-a716-446655440003', true),
    ('550e8400-e29b-41d4-a716-446655440052', 'Pedro Analista', '666.777.888-99', 'pedro.analista@polmil.gov.br', '71954321098', '$2a$10$cfFYnikRgfxgkq6I44oxeORd6Ud0PbK79OkLY2gv6URUEAdudGA9.', '550e8400-e29b-41d4-a716-446655440025', '550e8400-e29b-41d4-a716-446655440017', '550e8400-e29b-41d4-a716-446655440003', true),
    
    -- Usuário inativo para testes
    ('550e8400-e29b-41d4-a716-446655440053', 'Roberto Inativo', '777.888.999-00', 'roberto.inativo@teste.com', '85943210987', '$2a$10$cfFYnikRgfxgkq6I44oxeORd6Ud0PbK79OkLY2gv6URUEAdudGA9.', '550e8400-e29b-41d4-a716-446655440026', '550e8400-e29b-41d4-a716-446655440018', '550e8400-e29b-41d4-a716-446655440003', false),
    
    -- Usuário sem serviço/cidade para testes edge case
    ('550e8400-e29b-41d4-a716-446655440054', 'Sofia Pendente', '888.999.000-11', 'sofia.pendente@teste.com', '31932109876', '$2a$10$cfFYnikRgfxgkq6I44oxeORd6Ud0PbK79OkLY2gv6URUEAdudGA9.', NULL, NULL, '550e8400-e29b-41d4-a716-446655440003', true);