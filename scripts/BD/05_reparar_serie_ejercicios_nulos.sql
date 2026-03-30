-- =============================================================================
-- ⚠️ NO “repara” enlaces: BORRA filas de serie_ejercicio donde exercise_id IS NULL.
--
-- Si casi todo lo cargado tenía NULL (mal orden: 02 antes de tener ejercicios),
-- después de este script el panel “Mis series” mostrará 0 ejercicios en todas,
-- porque ya no quedan filas en serie_ejercicio. Es esperado.
--
-- Recuperación: asegurate `SELECT COUNT(*) FROM exercise;` > 0, luego ejecutá de
-- nuevo 02 (y 03 si usás rutinas Matt PF), o 00→02→03 desde un estado limpio.
--
-- Solo usá 05 para limpiar basura residual; no es el primer paso ante datos rotos.
-- =============================================================================

USE mattfuncional;

SET SQL_SAFE_UPDATES = 0;

SELECT COUNT(*) AS filas_que_se_van_a_eliminar FROM serie_ejercicio WHERE exercise_id IS NULL;

DELETE FROM serie_ejercicio WHERE exercise_id IS NULL;

SET SQL_SAFE_UPDATES = 1;

SELECT CONCAT('Eliminadas filas con exercise_id NULL. Comprobar conteo NULL restante: ',
       (SELECT COUNT(*) FROM serie_ejercicio WHERE exercise_id IS NULL)) AS resultado;
