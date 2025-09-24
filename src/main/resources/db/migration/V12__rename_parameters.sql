ALTER TABLE criticalities RENAME TO parameters;
ALTER TABLE parameters RENAME COLUMN parameter to description;
ALTER TABLE parameters ADD COLUMN phase varchar(10);