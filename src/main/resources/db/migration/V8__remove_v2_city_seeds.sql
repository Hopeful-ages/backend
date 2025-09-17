-- Reaponta usuários para as cidades da V7 (IBGE) e remove os 5 seeds da V2.
DO $$
DECLARE
    fk_col text;

    -- IDs seeds da V2
    id_porto  uuid := '550e8400-e29b-41d4-a716-446655440010';
    id_rio    uuid := '550e8400-e29b-41d4-a716-446655440011';
    id_sp     uuid := '550e8400-e29b-41d4-a716-446655440012';
    id_manaus uuid := '550e8400-e29b-41d4-a716-446655440013';
    id_recife uuid := '550e8400-e29b-41d4-a716-446655440014';

    -- IDs alvo (IBGE) a partir de name + UF
    tgt_porto  uuid;
    tgt_rio    uuid;
    tgt_sp     uuid;
    tgt_manaus uuid;
    tgt_recife uuid;

    has_city boolean := EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_schema='public' AND table_name='city'
    );
BEGIN
    -- Qual é a coluna FK em users? city_id ou cidade_id?
    SELECT column_name INTO fk_col
    FROM information_schema.columns
    WHERE table_schema='public' AND table_name='users'
      AND column_name IN ('city_id','cidade_id')
    LIMIT 1;

    -- Descobre os IDs corretos nas tabelas atuais (city/cidade)
    IF has_city THEN
        SELECT id INTO tgt_porto  FROM public.city   WHERE name='Porto Alegre'   AND state='RS' LIMIT 1;
        SELECT id INTO tgt_rio    FROM public.city   WHERE name='Rio de Janeiro' AND state='RJ' LIMIT 1;
        SELECT id INTO tgt_sp     FROM public.city   WHERE name='São Paulo'      AND state='SP' LIMIT 1;
        SELECT id INTO tgt_manaus FROM public.city   WHERE name='Manaus'         AND state='AM' LIMIT 1;
        SELECT id INTO tgt_recife FROM public.city   WHERE name='Recife'         AND state='PE' LIMIT 1;
    ELSE
        SELECT id INTO tgt_porto  FROM public.cidade WHERE nome='Porto Alegre'   AND estado='RS' LIMIT 1;
        SELECT id INTO tgt_rio    FROM public.cidade WHERE nome='Rio de Janeiro' AND estado='RJ' LIMIT 1;
        SELECT id INTO tgt_sp     FROM public.cidade WHERE nome='São Paulo'      AND estado='SP' LIMIT 1;
        SELECT id INTO tgt_manaus FROM public.cidade WHERE nome='Manaus'         AND estado='AM' LIMIT 1;
        SELECT id INTO tgt_recife FROM public.cidade WHERE nome='Recife'         AND estado='PE' LIMIT 1;
    END IF;

    -- Reaponta users -> novas cidades (se a coluna existir)
    IF fk_col IS NOT NULL THEN
        IF tgt_porto IS NOT NULL  THEN EXECUTE format('UPDATE public.users SET %I = $1 WHERE %I = $2', fk_col, fk_col) USING tgt_porto,  id_porto;  END IF;
        IF tgt_rio IS NOT NULL    THEN EXECUTE format('UPDATE public.users SET %I = $1 WHERE %I = $2', fk_col, fk_col) USING tgt_rio,    id_rio;    END IF;
        IF tgt_sp IS NOT NULL     THEN EXECUTE format('UPDATE public.users SET %I = $1 WHERE %I = $2', fk_col, fk_col) USING tgt_sp,     id_sp;     END IF;
        IF tgt_manaus IS NOT NULL THEN EXECUTE format('UPDATE public.users SET %I = $1 WHERE %I = $2', fk_col, fk_col) USING tgt_manaus, id_manaus; END IF;
        IF tgt_recife IS NOT NULL THEN EXECUTE format('UPDATE public.users SET %I = $1 WHERE %I = $2', fk_col, fk_col) USING tgt_recife, id_recife; END IF;
    END IF;

    -- Agora pode remover os seeds da V2
    IF has_city THEN
        DELETE FROM public.city
        WHERE id IN (id_porto, id_rio, id_sp, id_manaus, id_recife);
    ELSE
        DELETE FROM public.cidade
        WHERE id IN (id_porto, id_rio, id_sp, id_manaus, id_recife);
    END IF;
END $$;
