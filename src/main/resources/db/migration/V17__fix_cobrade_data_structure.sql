UPDATE cobrade 
SET subgroup = 'Seca',
    type = 'Incêndio florestal',
    subtype = 'Incêndios em parques, áreas de proteção ambiental e áreas de preservação permanente nacionais, estaduais ou municipais'
WHERE code = '1.4.1.3.1';

UPDATE cobrade 
SET subgroup = 'Seca',
    type = 'Incêndio florestal',
    subtype = 'Incêndios em áreas não protegidas, com reflexos na qualidade do ar'
WHERE code = '1.4.1.3.2';

UPDATE cobrade 
SET subgroup = 'Seca'
WHERE code = '1.4.1.4.0';

UPDATE cobrade 
SET type = 'Liberação de produtos químicos e contaminação como consequência de ações militares'
WHERE code = '2.2.3.1.0';

UPDATE cobrade 
SET group_name = 'Transporte de passageiros e cargas não perigosas'
WHERE code IN ('2.5.1.0.0', '2.5.2.0.0', '2.5.3.0.0', '2.5.4.0.0', '2.5.5.0.0')
  AND group_name = 'transporte de passageiros e cargas não perigosas';
