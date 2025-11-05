CREATE TABLE papel (
    id UUID PRIMARY KEY,
    nome VARCHAR(10)
);

ALTER TABLE usuario ADD COLUMN papel_id UUID;

ALTER TABLE usuario
    ADD CONSTRAINT fk_usuario_papel FOREIGN KEY (papel_id) REFERENCES papel (id);