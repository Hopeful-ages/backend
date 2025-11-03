
WITH existing AS (
    SELECT id FROM services WHERE name = 'Defesa Civil'
), created AS (
    INSERT INTO services (id, name)
    SELECT gen_random_uuid(), 'Defesa Civil'
    WHERE NOT EXISTS (SELECT 1 FROM existing)
    RETURNING id
), chosen AS (
    SELECT id FROM existing
    UNION ALL
    SELECT id FROM created
)
UPDATE tasks t
SET service_id = (SELECT id FROM chosen LIMIT 1)
WHERE t.service_id IS NULL;

-- (Opcional) Registro de auditoria simples em comentário:
-- COUNT antes/depois (para referência manual):
-- SELECT COUNT(*) FROM tasks WHERE service_id IS NULL;
