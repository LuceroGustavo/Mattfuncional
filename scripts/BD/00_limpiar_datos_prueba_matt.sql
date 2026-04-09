-- =============================================================================
-- Opcional: eliminar únicamente los datos insertados por 01 / 02 / 03
-- (no borra ejercicios predeterminados, ni el admin/developer, ni configuración)
-- =============================================================================

USE mattfuncional;

SET @OLD_SAFE_UPDATES := @@SQL_SAFE_UPDATES;
SET SQL_SAFE_UPDATES = 0;

-- ORDEN CORRECTO DE BORRADO: respetando constraints FK
-- 1. Borrar serie_ejercicio (referencia a serie) - TODAS las filas relacionadas con Matt PF
DELETE se FROM serie_ejercicio se
INNER JOIN serie s ON se.serie_id = s.id
WHERE s.nombre LIKE 'Matt PF Serie %' AND s.es_plantilla = 1;

-- 2. Borrar rutina_categoria (referencia a rutina y categoria)
DELETE rc FROM rutina_categoria rc
INNER JOIN rutina r ON rc.rutina_id = r.id
WHERE r.nombre LIKE 'Matt PF Rutina %';

-- 3. Borrar seria (referencias a rutina y ejercicio ya borrados)
DELETE s FROM serie s
WHERE s.nombre LIKE 'Matt PF Serie %' AND s.es_plantilla = 1;

-- 4. Finalmente borrar rutina
DELETE FROM rutina WHERE nombre LIKE 'Matt PF Rutina %';

-- Horarios de asistencia de alumnos de prueba (FK hacia usuario)
DELETE FROM usuario_dias_horarios_asistencia
WHERE usuario_id IN (
    SELECT id FROM (SELECT id FROM usuario WHERE correo LIKE 'test_matt_pf_%@mattfuncional.test') AS u
);

-- Alumnos de prueba
DELETE FROM usuario WHERE correo LIKE 'test_matt_pf_%@mattfuncional.test';

SET SQL_SAFE_UPDATES = @OLD_SAFE_UPDATES;

SELECT 'Limpieza de datos Matt PF completada.' AS resultado;
