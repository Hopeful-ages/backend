CREATE UNIQUE INDEX IF NOT EXISTS ux_city_name_state
  ON city (name, state);
