WITH poa AS (
    SELECT id
    FROM city
    WHERE name = 'Porto Alegre' AND state = 'RS'
),
scenario_data AS (
    SELECT * FROM (VALUES
        ('1.1.3.2.1', 'Plano de resposta a deslizamentos em Porto Alegre'),
        ('1.2.3.0.0', 'Plano de contingencia para alagamentos urbanos de Porto Alegre'),
        ('1.3.2.1.3', 'Plano de prontidao para granizo severo em Porto Alegre')
    ) AS sd(code, origin)
),
scenarios_to_insert AS (
    SELECT sd.origin, cb.id AS cobrade_id, poa.id AS city_id
    FROM scenario_data sd
    JOIN cobrade cb ON cb.code = sd.code
    CROSS JOIN poa
    WHERE NOT EXISTS (
        SELECT 1
        FROM scenarios s
        WHERE s.city_id = poa.id
          AND s.cobrade_id = cb.id
    )
)
INSERT INTO scenarios (id, origin, city_id, cobrade_id)
SELECT gen_random_uuid(), origin, city_id, cobrade_id
FROM scenarios_to_insert;

WITH poa AS (
    SELECT id
    FROM city
    WHERE name = 'Porto Alegre' AND state = 'RS'
),
scenario_data AS (
    SELECT * FROM (VALUES
        ('1.1.3.2.1', 'Plano de resposta a deslizamentos em Porto Alegre'),
        ('1.2.3.0.0', 'Plano de contingencia para alagamentos urbanos de Porto Alegre'),
        ('1.3.2.1.3', 'Plano de prontidao para granizo severo em Porto Alegre')
    ) AS sd(code, origin)
),
scenario_ids AS (
    SELECT sd.code, s.id AS scenario_id
    FROM scenario_data sd
    JOIN cobrade cb ON cb.code = sd.code
    JOIN scenarios s ON s.cobrade_id = cb.id
    JOIN poa ON s.city_id = poa.id
),
task_data AS (
    SELECT * FROM (VALUES
        ('1.1.3.2.1', 'Realizar vistorias semanais em encostas criticas', 'ANTES'),
        ('1.1.3.2.1', 'Acionar plano de evacuacao para areas de risco', 'DURANTE'),
        ('1.1.3.2.1', 'Remover detritos e restabelecer acessos', 'DEPOIS'),
        ('1.2.3.0.0', 'Inspecionar drenagens pluviais prioritarias', 'ANTES'),
        ('1.2.3.0.0', 'Operar barreiras de contencao em vias alagadas', 'DURANTE'),
        ('1.2.3.0.0', 'Executar higienizacao das areas afetadas', 'DEPOIS'),
        ('1.3.2.1.3', 'Alertar populacao sobre tempestade de granizo', 'ANTES'),
        ('1.3.2.1.3', 'Distribuir lonas para protecao imediata', 'DURANTE'),
        ('1.3.2.1.3', 'Registrar danos e acionar assistencia social', 'DEPOIS')
    ) AS td(code, description, phase)
),
tasks_to_insert AS (
    SELECT si.scenario_id, td.description, td.phase
    FROM task_data td
    JOIN scenario_ids si ON si.code = td.code
)
INSERT INTO tasks (id, description, phase, date, service_id, scenario_id)
SELECT gen_random_uuid(),
       tti.description,
       tti.phase,
       CURRENT_DATE,
       NULL,
       tti.scenario_id
FROM tasks_to_insert tti
WHERE NOT EXISTS (
    SELECT 1
    FROM tasks existing
    WHERE existing.scenario_id = tti.scenario_id
      AND existing.description = tti.description
);

WITH poa AS (
    SELECT id
    FROM city
    WHERE name = 'Porto Alegre' AND state = 'RS'
),
scenario_data AS (
    SELECT * FROM (VALUES
        ('1.1.3.2.1', 'Plano de resposta a deslizamentos em Porto Alegre'),
        ('1.2.3.0.0', 'Plano de contingencia para alagamentos urbanos de Porto Alegre'),
        ('1.3.2.1.3', 'Plano de prontidao para granizo severo em Porto Alegre')
    ) AS sd(code, origin)
),
scenario_ids AS (
    SELECT sd.code, s.id AS scenario_id
    FROM scenario_data sd
    JOIN cobrade cb ON cb.code = sd.code
    JOIN scenarios s ON s.cobrade_id = cb.id
    JOIN poa ON s.city_id = poa.id
),
parameter_data AS (
    SELECT * FROM (VALUES
        ('1.1.3.2.1', 'ANTES', 'Monitorar inclinacao das encostas sensiveis', 'Acionar equipe de geotecnicos para ajustes preventivos'),
        ('1.1.3.2.1', 'DURANTE', 'Conferir dados dos sensores de movimento', 'Isolar rapidamente as zonas em risco'),
        ('1.1.3.2.1', 'DEPOIS', 'Avaliar estabilidade apos o evento', 'Liberar acessos apenas com laudo tecnico'),
        ('1.2.3.0.0', 'ANTES', 'Verificar capacidade dos sistemas de drenagem', 'Limpar bocas de lobo estrategicas'),
        ('1.2.3.0.0', 'DURANTE', 'Mensurar nivel da agua nas principais bacias', 'Redirecionar trafego para rotas seguras'),
        ('1.2.3.0.0', 'DEPOIS', 'Apurar perdas materiais registradas', 'Orientar retorno seguro da populacao'),
        ('1.3.2.1.3', 'ANTES', 'Confirmar previsao com centro meteorologico', 'Divulgar comunicados pelas redes oficiais'),
        ('1.3.2.1.3', 'DURANTE', 'Acompanhar ocorrencias com defesa civil', 'Ativar pontos de abrigo temporario'),
        ('1.3.2.1.3', 'DEPOIS', 'Consolidar relatorio dos danos reportados', 'Solicitar apoio para reposicao de telhados')
    ) AS pd(code, phase, description, action)
),
parameters_to_insert AS (
    SELECT si.scenario_id, pd.phase, pd.description, pd.action
    FROM parameter_data pd
    JOIN scenario_ids si ON si.code = pd.code
)
INSERT INTO parameters (id, phase, description, action, scenario_id)
SELECT gen_random_uuid(),
       pti.phase,
       pti.description,
       pti.action,
       pti.scenario_id
FROM parameters_to_insert pti
WHERE NOT EXISTS (
    SELECT 1
    FROM parameters existing
    WHERE existing.scenario_id = pti.scenario_id
      AND existing.phase = pti.phase
);
