-- Logros del blueprint. Ejecutar una vez en Neon (idempotente por code).
INSERT INTO achievements (id, code, name, description, type, points)
SELECT gen_random_uuid(), 'SENOR_DE_LAS_LATAS', 'Señor de las latas',
       'Terminaste todas las latas de la estanteria', 'completion', 100
WHERE NOT EXISTS (SELECT 1 FROM achievements WHERE code = 'SENOR_DE_LAS_LATAS');

INSERT INTO achievements (id, code, name, description, type, points)
SELECT gen_random_uuid(), 'SENOR_DEL_RECICLAJE', 'Señor del reciclaje',
       'Reiniciaste tu progreso y volviste a terminar todas las latas', 'completion', 200
WHERE NOT EXISTS (SELECT 1 FROM achievements WHERE code = 'SENOR_DEL_RECICLAJE');
