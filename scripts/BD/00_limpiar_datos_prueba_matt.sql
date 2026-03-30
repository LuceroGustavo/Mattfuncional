-- =============================================================================
-- Opcional: eliminar únicamente los datos insertados por 01 / 02 / 03
-- (no borra ejercicios predeterminados, ni el admin/developer, ni configuración)
-- =============================================================================

USE mattfuncional;

-- Series copiadas dentro de rutinas de prueba
DELETE se FROM serie_ejercicio se
INNER JOIN serie s ON se.serie_id = s.id
INNER JOIN rutina r ON s.rutina_id = r.id
WHERE r.nombre LIKE 'Matt PF Rutina %';

DELETE s FROM serie s
INNER JOIN rutina r ON s.rutina_id = r.id
WHERE r.nombre LIKE 'Matt PF Rutina %';

DELETE rc FROM rutina_categoria rc
INNER JOIN rutina r ON rc.rutina_id = r.id
WHERE r.nombre LIKE 'Matt PF Rutina %';

DELETE FROM rutina WHERE nombre LIKE 'Matt PF Rutina %';

-- Series plantilla sueltas (biblioteca)
DELETE se FROM serie_ejercicio se
INNER JOIN serie s ON se.serie_id = s.id
WHERE s.nombre LIKE 'Matt PF Serie %' AND s.rutina_id IS NULL AND s.es_plantilla = 1;

DELETE FROM serie
WHERE nombre LIKE 'Matt PF Serie %' AND rutina_id IS NULL AND es_plantilla = 1;

-- Alumnos de prueba
DELETE FROM usuario WHERE correo LIKE 'test_matt_pf_%@mattfuncional.test';

SELECT 'Limpieza de datos Matt PF completada.' AS resultado;
