-- Cria a tabela "parameters" exigida pela entidade ages.hopeful.modules.scenarios.model.Parameter
-- Necessária para os testes de integração (H2)

CREATE TABLE IF NOT EXISTS parameters (
    id UUID PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    action VARCHAR(255) NOT NULL,
    phase VARCHAR(255) NOT NULL,
    scenario_id UUID NOT NULL,
    CONSTRAINT fk_parameters_scenarios FOREIGN KEY (scenario_id) REFERENCES scenarios (id)
);

-- Índice auxiliar para melhorar buscas por cenário
CREATE INDEX IF NOT EXISTS idx_parameters_scenario ON parameters (scenario_id);
