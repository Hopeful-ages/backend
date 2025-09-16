DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'criticality_phase') THEN
        CREATE TYPE criticality_phase AS ENUM ('antes', 'durante', 'depois');
    END IF;
END
$$;
