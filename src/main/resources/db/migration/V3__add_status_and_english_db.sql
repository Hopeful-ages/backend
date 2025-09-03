-- v3__translate_to_english_and_add_account_status.sql

ALTER TABLE cidade RENAME TO city;
ALTER TABLE servicos RENAME TO services;
ALTER TABLE cenarios RENAME TO scenarios;
ALTER TABLE tarefas RENAME TO tasks;
ALTER TABLE criticidades RENAME TO criticalities;
ALTER TABLE usuario RENAME TO users;

ALTER TABLE city RENAME COLUMN nome TO name;
ALTER TABLE city RENAME COLUMN estado TO state;

ALTER TABLE cobrade RENAME COLUMN codigo TO code;
ALTER TABLE cobrade RENAME COLUMN descricao TO description;
ALTER TABLE cobrade RENAME COLUMN grupo TO group_name;
ALTER TABLE cobrade RENAME COLUMN subgrupo TO subgroup;
ALTER TABLE cobrade RENAME COLUMN tipo TO type;
ALTER TABLE cobrade RENAME COLUMN subtipo TO subtype;

-- Rename columns in services
ALTER TABLE services RENAME COLUMN nome TO name;

-- Rename columns in scenarios
ALTER TABLE scenarios RENAME COLUMN descricao TO description;
ALTER TABLE scenarios RENAME COLUMN origem TO origin;
ALTER TABLE scenarios RENAME COLUMN cidade_id TO city_id;

-- Rename columns in tasks
ALTER TABLE tasks RENAME COLUMN descricao TO description;
ALTER TABLE tasks RENAME COLUMN fase TO phase;
ALTER TABLE tasks RENAME COLUMN servico_id TO service_id;
ALTER TABLE tasks RENAME COLUMN cenario_id TO scenario_id;
ALTER TABLE tasks RENAME COLUMN data TO date;

-- Rename columns in criticalities
ALTER TABLE criticalities RENAME COLUMN parametro TO parameter;
ALTER TABLE criticalities RENAME COLUMN acao TO action;
ALTER TABLE criticalities RENAME COLUMN cenario_id TO scenario_id;

-- Rename columns in users
ALTER TABLE users RENAME COLUMN nome TO name;
ALTER TABLE users RENAME COLUMN telefone TO phone;
ALTER TABLE users RENAME COLUMN senha TO password;
ALTER TABLE users RENAME COLUMN servico_id TO service_id;
ALTER TABLE users RENAME COLUMN cidade_id TO city_id;

-- Add new column for account status
ALTER TABLE users ADD COLUMN account_status BOOLEAN DEFAULT TRUE;