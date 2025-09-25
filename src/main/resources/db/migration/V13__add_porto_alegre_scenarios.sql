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
    SELECT sd.origin, cb.id AS cobrade_id
    FROM scenario_data sd
    JOIN cobrade cb ON cb.code = sd.code
)
INSERT INTO scenarios (id, origin, city_id, cobrade_id)
SELECT gen_random_uuid(),
       sti.origin,
       poa.id,
       sti.cobrade_id
FROM scenarios_to_insert sti
CROSS JOIN poa
WHERE NOT EXISTS (
    SELECT 1
    FROM scenarios s
    WHERE s.city_id = poa.id
      AND s.cobrade_id = sti.cobrade_id
);
