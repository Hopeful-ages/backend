-- V8: Reaponta usuários para as cidades oficiais (V7/IBGE) e remove as 5 seeds da V2.
-- Compatível com H2 e Postgres. Sem DO $$, sem plpgsql.

-- Porto Alegre (RS)
UPDATE users u
SET city_id = (
  SELECT id
  FROM city
  WHERE name = 'Porto Alegre'
    AND state IN ('RS','Rio Grande do Sul')
  ORDER BY CASE WHEN state = 'RS' THEN 0 ELSE 1 END
  LIMIT 1
)
WHERE u.city_id = '550e8400-e29b-41d4-a716-446655440010'
  AND EXISTS (
    SELECT 1 FROM city
    WHERE name = 'Porto Alegre' AND state IN ('RS','Rio Grande do Sul')
  );

-- Rio de Janeiro (RJ)
UPDATE users u
SET city_id = (
  SELECT id
  FROM city
  WHERE name = 'Rio de Janeiro'
    AND state IN ('RJ','Rio de Janeiro')
  ORDER BY CASE WHEN state = 'RJ' THEN 0 ELSE 1 END
  LIMIT 1
)
WHERE u.city_id = '550e8400-e29b-41d4-a716-446655440011'
  AND EXISTS (
    SELECT 1 FROM city
    WHERE name = 'Rio de Janeiro' AND state IN ('RJ','Rio de Janeiro')
  );

-- São Paulo (SP)
UPDATE users u
SET city_id = (
  SELECT id
  FROM city
  WHERE name = 'São Paulo'
    AND state IN ('SP','São Paulo')
  ORDER BY CASE WHEN state = 'SP' THEN 0 ELSE 1 END
  LIMIT 1
)
WHERE u.city_id = '550e8400-e29b-41d4-a716-446655440012'
  AND EXISTS (
    SELECT 1 FROM city
    WHERE name = 'São Paulo' AND state IN ('SP','São Paulo')
  );

-- Manaus (AM)
UPDATE users u
SET city_id = (
  SELECT id
  FROM city
  WHERE name = 'Manaus'
    AND state IN ('AM','Amazonas')
  ORDER BY CASE WHEN state = 'AM' THEN 0 ELSE 1 END
  LIMIT 1
)
WHERE u.city_id = '550e8400-e29b-41d4-a716-446655440013'
  AND EXISTS (
    SELECT 1 FROM city
    WHERE name = 'Manaus' AND state IN ('AM','Amazonas')
  );

-- Recife (PE)
UPDATE users u
SET city_id = (
  SELECT id
  FROM city
  WHERE name = 'Recife'
    AND state IN ('PE','Pernambuco')
  ORDER BY CASE WHEN state = 'PE' THEN 0 ELSE 1 END
  LIMIT 1
)
WHERE u.city_id = '550e8400-e29b-41d4-a716-446655440014'
  AND EXISTS (
    SELECT 1 FROM city
    WHERE name = 'Recife' AND state IN ('PE','Pernambuco')
  );

-- Remoção das 5 seeds V2 somente se não houver FK em users
DELETE FROM city c
WHERE c.id IN (
  '550e8400-e29b-41d4-a716-446655440010',
  '550e8400-e29b-41d4-a716-446655440011',
  '550e8400-e29b-41d4-a716-446655440012',
  '550e8400-e29b-41d4-a716-446655440013',
  '550e8400-e29b-41d4-a716-446655440014'
)
AND NOT EXISTS (
  SELECT 1 FROM users u WHERE u.city_id = c.id
);
