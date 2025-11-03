-- Garantir colunas compatíveis com a entidade Cobrade
-- Em vez de remover, garantimos que exista a coluna group_name (compatível com @Column(name="group_name"))
ALTER TABLE cobrade ADD COLUMN IF NOT EXISTS group_name VARCHAR(255);

-- A entidade também possui a coluna "origin"; garantir que exista para evitar erros de SELECT
ALTER TABLE cobrade ADD COLUMN IF NOT EXISTS origin VARCHAR(255);

ALTER TABLE cobrade ALTER COLUMN id SET DEFAULT RANDOM_UUID();

ALTER TABLE cobrade ALTER COLUMN description TYPE VARCHAR(1000);
