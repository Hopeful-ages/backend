-- Garantir coluna "published" exigida pela entidade Scenario
ALTER TABLE scenarios ADD COLUMN IF NOT EXISTS published BOOLEAN DEFAULT FALSE;
