-- Remover coluna group_name
ALTER TABLE cobrade DROP COLUMN group_name;

ALTER TABLE cobrade ALTER COLUMN id SET DEFAULT gen_random_uuid();

ALTER TABLE cobrade ALTER COLUMN description TYPE VARCHAR(1000);

INSERT INTO cobrade (code, subgroup, type, subtype, description) VALUES
-- DESASTRES NATURAIS 
-- 1. GEOLÓGICO

-- Terremoto e Emanação vulcânica
('1.1.1.1.0', 'Terremoto', 'Tremor de terra', NULL, 'Vibrações do terreno que provocam oscilações verticais e horizontais na superfície da Terra (ondas sísmicas). Pode ser natural (tectônica) ou induzido (explosões, injeção profunda de líquidos e gás, extração de fluidos, alívio de carga de minas, enchimento de lagos artificiais).'),
('1.1.1.2.0', 'Terremoto', 'Tsunami', NULL, 'Série de ondas geradas por deslocamento de um grande volume de água causado geralmente por terremotos, erupções vulcânicas ou movimentos de massa.'),
('1.1.2.0.0', 'Emanação vulcânica', NULL, NULL, 'Produtos/materiais vulcânicos lançados na atmosfera a partir de erupções vulcânicas.'),


-- Movimento de massa
('1.1.3.1.1', 'Movimento de massa', 'Quedas, tombamentos e rolamentos', 'Blocos', 'As quedas de blocos são movimentos rápidos e acontecem quando materiais rochosos diversos e de volumes variáveis se destacam de encostas muito íngremes, num movimento tipo queda livre. Os tombamentos de blocos são movimentos de massa em que ocorre rotação de um bloco de solo ou rocha em torno de um ponto ou abaixo do centro de gravidade da massa desprendida. Rolamentos de blocos são movimentos de blocos rochosos ao longo de encostas, que ocorrem geralmente pela perda de apoio (descalçamento).'),
('1.1.3.1.2', 'Movimento de massa', 'Quedas, tombamentos e rolamentos', 'Lascas', 'As quedas de lascas são movimentos rápidos e acontecem quando fatias delgadas formadas pelos fragmentos de rochas se destacam de encostas muito íngremes, num movimento tipo queda livre.'),
('1.1.3.1.3', 'Movimento de massa', 'Quedas, tombamentos e rolamentos', 'Matacões', 'Os rolamentos de matacões são caracterizados por movimentos rápidos e acontecem quando materiais rochosos diversos e de volumes variáveis se destacam de encostas e movimentam-se num plano inclinado.'),
('1.1.3.1.4', 'Movimento de massa', 'Quedas, tombamentos e rolamentos', 'Lajes', 'As quedas de lajes são movimentos rápidos e acontecem quando fragmentos de rochas extensas de superfície mais ou menos plana e de pouca espessura se destacam de encostas muito íngremes, num movimento tipo queda livre.'),
('1.1.3.2.1', 'Movimento de massa', 'Deslizamentos', 'Deslizamentos de solo e/ou rocha', 'São movimentos rápidos de solo ou rocha, apresentando superfície de ruptura bem definida, de duração relativamente curta, de massas de terreno geralmente bem definidas quanto ao seu volume, cujo centro de gravidade se desloca para baixo e para fora do talude. Frequentemente, os primeiros sinais desses movimentos são a presença de fissuras.'),
('1.1.3.3.1', 'Movimento de massa', 'Corridas de massa', 'Solo/Lama', 'Ocorrem quando, por índices pluviométricos excepcionais, o solo/lama, misturado com a água, tem comportamento de líquido viscoso, de extenso raio de ação e alto poder destrutivo.'),
('1.1.3.3.2', 'Movimento de massa', 'Corridas de massa', 'Rocha/Detrito', 'Ocorrem quando, por índices pluviométricos excepcionais, rocha/detrito, misturado com a água, tem comportamento de líquido viscoso, de extenso raio de ação e alto poder destrutivo.'),
('1.1.3.4.0', 'Movimento de massa', 'Subsidências e colapsos', NULL, 'Afundamento rápido ou gradual do terreno devido ao colapso de cavidades, redução da porosidade do solo ou deformação de material argiloso.'),

-- Erosão
('1.1.4.1.0', 'Erosão', 'Erosão costeira/Marinha', NULL, 'Processo de desgaste (mecânico ou químico) que ocorre ao longo da linha da costa (rochosa ou praia) e se deve à ação das ondas, correntes marinhas e marés.'),
('1.1.4.2.0', 'Erosão', 'Erosão de margem fluvial', NULL, 'Desgaste das encostas dos rios que provoca desmoronamento de barrancos.'),
('1.1.4.3.1', 'Erosão', 'Erosão continental', 'Laminar', 'Remoção de uma camada delgada e uniforme do solo superficial provocada por fluxo hídrico não concentrado.'),
('1.1.4.3.2', 'Erosão', 'Erosão continental', 'Ravinas', 'Evolução, em tamanho e profundidade, da desagregação e remoção das partículas do solo de sulcos provocada por escoamento hídrico superficial concentrado.'),
('1.1.4.3.3', 'Erosão', 'Erosão continental', 'Boçorocas', 'Evolução do processo de ravinamento, em tamanho e profundidade, em que a desagregação e remoção das partículas do solo são provocadas por escoamento hídrico superficial e subsuperficial (escoamento freático) concentrado.'),


-- 2. HIDROLÓGICO
('1.2.1.0.0', 'Inundações', NULL, NULL, 'Submersão de áreas fora dos limites normais de um curso de água em zonas que normalmente não se encontram submersas. O transbordamento ocorre de modo gradual, geralmente ocasionado por chuvas prolongadas em áreas de planície.'),
('1.2.2.0.0', 'Enxurradas', NULL, NULL, 'Escoamento superficial de alta velocidade e energia, provocado por chuvas intensas e concentradas, normalmente em pequenas bacias de relevo acidentado. Caracterizada pela elevação súbita das vazões de determinada drenagem e transbordamento brusco da calha fluvial. Apresenta grande poder destrutivo.'),
('1.2.3.0.0', 'Alagamentos', NULL, NULL, 'Extrapolação da capacidade de escoamento de sistemas de drenagem urbana e consequente acúmulo de água em ruas, calçadas ou outras infraestruturas urbanas, em decorrência de precipitações intensas.'),


-- 3. METEOROLÓGICO
('1.3.1.1.1', 'Sistemas de grande escala/Escala regional', 'Ciclones', 'Ventos costeiros (mobilidade de dunas)', 'Intensificação dos ventos nas regiões litorâneas, movimentando dunas de areia sobre construções na orla.'),
('1.3.1.1.2', 'Sistemas de grande escala/Escala regional', 'Ciclones', 'Marés de tempestade (ressaca)', 'Ondas violentas geradas por ventos fortes que elevam o nível do oceano, causando inundação costeira, aumento das ondas e destruição de infraestruturas litorâneas. Essa intensificação das correntes marítimas carrega uma enorme quantidade de água em direção ao litoral. Em consequência, as praias inundam, as ondas se tornam maiores e a orla pode ser devastada alagando ruas e destruindo edificações.'),
('1.3.1.2.0', 'Sistemas de grande escala/Escala regional', 'Frentes frias/Zonas de convergência', NULL, 'Fenômenos atmosféricos que provocam queda de temperatura, chuvas intensas, vendavais e até granizo.'),
('1.3.2.1.1', 'Tempestades', 'Tempestade local/Convectiva', 'Tornados', 'Coluna de ar que gira de forma violenta e muito perigosa, estando em contato com a terra e a base de uma nuvem de grande desenvolvimento vertical. Essa coluna de ar pode percorrer vários quilômetros e deixa um rastro de destruição pelo caminho percorrido.'),
('1.3.2.1.2', 'Tempestades', 'Tempestade local/Convectiva', 'Tempestade de raios', 'Tempestade com intensa atividade elétrica no interior das nuvens, com grande desenvolvimento vertical.'),
('1.3.2.1.3', 'Tempestades', 'Tempestade local/Convectiva', 'Granizo', 'Precipitação de pedaços irregulares de gelo.'),
('1.3.2.1.4', 'Tempestades', 'Tempestade local/Convectiva', 'Chuvas intensas', 'São chuvas que ocorrem com acumulados significativos, causando múltiplos desastres (ex.: inundações, movimentos de massa, enxurradas, etc.).'),
('1.3.2.1.5', 'Tempestades', 'Tempestade local/Convectiva', 'Vendaval', 'Forte deslocamento de uma massa de ar em uma região.'),
('1.3.3.1.0', 'Temperaturas extremas', 'Onda de calor', NULL, 'Período prolongado de calor excessivo, com temperaturas 5°C acima da média por pelo menos três dias.'),
('1.3.3.2.1', 'Temperaturas extremas', 'Onda de frio', 'Friagem', 'Período de três a quatro dias de temperaturas abaixo do esperado para a região.'),
('1.3.3.2.2', 'Temperaturas extremas', 'Onda de frio', 'Geadas', 'Formação de camada de cristais de gelo na superfície ou vegetação exposta.'),


-- 4. CLIMATOLÓGICO
('1.4.1.1.0', 'Seca', 'Estiagem', NULL, 'Período prolongado de baixa ou nenhuma pluviosidade, em que a perda de umidade do solo é superior à reposição.'),
('1.4.1.2.0', 'Seca', 'Seca', NULL, 'Estiagem prolongada suficiente para causar desequilíbrio hidrológico.'),
('1.4.1.3.1', 'Incêndio florestal', 'Incêndios em áreas protegidas', NULL, 'Propagação de fogo sem controle em áreas legalmente protegidas.'),
('1.4.1.3.2', 'Incêndio florestal', 'Incêndios em áreas não protegidas', NULL, 'Propagação de fogo sem controle em vegetação fora de áreas protegidas, afetando a qualidade do ar.'),
('1.4.1.4.0', 'Climatológico', 'Baixa umidade do ar', NULL, 'Taxa de vapor de água suspensa na atmosfera cai para níveis abaixo de 20%.'),


-- 5. BIOLÓGICO
('1.5.1.1.0', 'Epidemias', 'Doenças infecciosas virais', NULL, 'Aumento brusco, significativo e transitório da ocorrência de doenças infecciosas geradas por vírus.'),
('1.5.1.2.0', 'Epidemias', 'Doenças infecciosas bacterianas', NULL, 'Aumento brusco, significativo e transitório da ocorrência de doenças infecciosas geradas por bactérias.'),
('1.5.1.3.0', 'Epidemias', 'Doenças infecciosas parasíticas', NULL, 'Aumento brusco, significativo e transitório da ocorrência de doenças infecciosas geradas por parasitas.'),
('1.5.1.4.0', 'Epidemias', 'Doenças infecciosas fúngicas', NULL, 'Aumento brusco, significativo e transitório da ocorrência de doenças infecciosas geradas por fungos.'),
('1.5.2.1.0', 'Infestações/Pragas', 'Infestações de animais', NULL, 'Infestações por animais que alterem o equilíbrio ecológico de uma região.'),
('1.5.2.2.1', 'Infestações/Pragas', 'Infestações de algas', 'Marés vermelhas', 'Aglomeração de microalgas em água doce ou salgada suficiente para causar alterações físicas, químicas ou biológicas.'),
('1.5.2.2.2', 'Infestações/Pragas', 'Infestações de algas', 'Cianobactérias em reservatórios', 'Aglomeração de cianobactérias em reservatórios receptores de dejetos, provocando alterações nas propriedades da água.'),
('1.5.2.3.0', 'Infestações/Pragas', 'Outras infestações', NULL, 'Infestações que alterem o equilíbrio ecológico de uma região.');
