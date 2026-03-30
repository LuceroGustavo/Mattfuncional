-- =============================================================================
-- 15 series plantilla (biblioteca) — ejercicios predeterminados del sistema
-- Ejecutar después de 01 (o sin 01; solo requiere profesor + ≥15 exercises).
-- Nombres exactos: Matt PF Serie 01 … 15 (usados por 03_rutinas)
-- Unidades: reps, min y seg (igual que crearSerie.html / MiGymvirtual).
-- =============================================================================

USE mattfuncional;

SET @profesor_id = COALESCE(
    (SELECT id FROM profesor WHERE correo = 'profesor@mattfuncional.com' LIMIT 1),
    (SELECT id FROM profesor ORDER BY id LIMIT 1)
);

SET @e1  = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 0),  (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 0));
SET @e2  = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 1),  (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 1));
SET @e3  = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 2),  (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 2));
SET @e4  = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 3),  (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 3));
SET @e5  = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 4),  (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 4));
SET @e6  = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 5),  (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 5));
SET @e7  = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 6),  (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 6));
SET @e8  = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 7),  (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 7));
SET @e9  = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 8),  (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 8));
SET @e10 = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 9),  (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 9));
SET @e11 = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 10), (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 10));
SET @e12 = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 11), (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 11));
SET @e13 = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 12), (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 12));
SET @e14 = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 13), (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 13));
SET @e15 = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 14), (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 14));
SET @e16 = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 15), (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 15));
SET @e17 = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 16), (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 16));
SET @e18 = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 17), (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 17));
SET @e19 = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 18), (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 18));
SET @e20 = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 19), (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 19));
SET @e21 = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 20), (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 20));
SET @e22 = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 21), (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 21));
SET @e23 = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 22), (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 22));
SET @e24 = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 23), (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 23));
SET @e25 = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 24), (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 24));
SET @e26 = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 25), (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 25));
SET @e27 = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 26), (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 26));
SET @e28 = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 27), (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 27));
SET @e29 = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 28), (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 28));
SET @e30 = COALESCE((SELECT id FROM exercise WHERE profesor_id IS NULL AND (es_predeterminado = 1 OR es_predeterminado IS NULL) ORDER BY id LIMIT 1 OFFSET 29), (SELECT id FROM exercise WHERE profesor_id IS NULL ORDER BY id LIMIT 1 OFFSET 29));

INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, rutina_id, profesor_id)
VALUES ('Matt PF Serie 01', 0, 'Pecho y tríceps — empuje principiantes.', 1, 'ADMIN', 2, NULL, @profesor_id);
SET @s1 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden) VALUES
(@s1, @e1, 12, 'reps', 10, 0), (@s1, @e2, 10, 'reps', 15, 1), (@s1, @e3, 15, 'reps', 5, 2);

INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, rutina_id, profesor_id)
VALUES ('Matt PF Serie 02', 0, 'Piernas — cuádriceps y glúteos.', 1, 'ADMIN', 1, NULL, @profesor_id);
SET @s2 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden) VALUES
(@s2, @e4, 10, 'reps', 20, 0), (@s2, @e5, 12, 'reps', 15, 1), (@s2, @e6, 15, 'reps', 10, 2), (@s2, @e7, 20, 'reps', NULL, 3);

INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, rutina_id, profesor_id)
VALUES ('Matt PF Serie 03', 0, 'Espalda y bíceps — tirón y curl.', 1, 'ADMIN', 2, NULL, @profesor_id);
SET @s3 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden) VALUES
(@s3, @e8, 8, 'reps', 25, 0), (@s3, @e9, 12, 'reps', 12, 1), (@s3, @e10, 45, 'seg', NULL, 2);

INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, rutina_id, profesor_id)
VALUES ('Matt PF Serie 04', 0, 'Full body — circuito corto.', 1, 'ADMIN', 3, NULL, @profesor_id);
SET @s4 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden) VALUES
(@s4, @e11, 30, 'seg', NULL, 0), (@s4, @e12, 12, 'reps', 8, 1), (@s4, @e13, 10, 'reps', 20, 2), (@s4, @e14, 15, 'reps', 5, 3);

INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, rutina_id, profesor_id)
VALUES ('Matt PF Serie 05', 0, 'Hombros — press y elevaciones.', 1, 'ADMIN', 1, NULL, @profesor_id);
SET @s5 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden) VALUES
(@s5, @e15, 10, 'reps', 12, 0), (@s5, @e16, 12, 'reps', 6, 1), (@s5, @e17, 15, 'reps', 4, 2);

INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, rutina_id, profesor_id)
VALUES ('Matt PF Serie 06', 0, 'Cardio y core.', 1, 'ADMIN', 2, NULL, @profesor_id);
SET @s6 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden) VALUES
( @s6, @e18, 5, 'min', NULL, 0), (@s6, @e19, 45, 'seg', NULL, 1), (@s6, @e20, 20, 'reps', NULL, 2), (@s6, @e1, 30, 'seg', NULL, 3);

INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, rutina_id, profesor_id)
VALUES ('Matt PF Serie 07', 0, 'Upper — empuje.', 1, 'ADMIN', 1, NULL, @profesor_id);
SET @s7 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden) VALUES
(@s7, @e2, 8, 'reps', 20, 0), (@s7, @e3, 10, 'reps', 10, 1), (@s7, @e4, 12, 'reps', 8, 2);

INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, rutina_id, profesor_id)
VALUES ('Matt PF Serie 08', 0, 'Lower — glúteos y piernas.', 1, 'ADMIN', 2, NULL, @profesor_id);
SET @s8 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden) VALUES
(@s8, @e5, 12, 'reps', 15, 0), (@s8, @e6, 15, 'reps', 12, 1), (@s8, @e7, 20, 'reps', NULL, 2), (@s8, @e8, 10, 'reps', 25, 3);

INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, rutina_id, profesor_id)
VALUES ('Matt PF Serie 09', 0, 'Elongación y movilidad.', 1, 'ADMIN', 1, NULL, @profesor_id);
SET @s9 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden) VALUES
(@s9, @e9, 60, 'seg', NULL, 0), (@s9, @e10, 45, 'seg', NULL, 1), (@s9, @e11, 90, 'seg', NULL, 2);

INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, rutina_id, profesor_id)
VALUES ('Matt PF Serie 10', 0, 'Mixto alta intensidad.', 1, 'ADMIN', 2, NULL, @profesor_id);
SET @s10 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden) VALUES
(@s10, @e12, 10, 'reps', 18, 0), (@s10, @e13, 12, 'reps', 15, 1), (@s10, @e14, 3, 'min', NULL, 2), (@s10, @e15, 15, 'reps', 8, 3);

INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, rutina_id, profesor_id)
VALUES ('Matt PF Serie 11', 0, 'Core — estabilidad.', 1, 'ADMIN', 2, NULL, @profesor_id);
SET @s11 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden) VALUES
(@s11, @e21, 40, 'seg', NULL, 0), (@s11, @e22, 30, 'seg', NULL, 1), (@s11, @e23, 20, 'seg', NULL, 2);

INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, rutina_id, profesor_id)
VALUES ('Matt PF Serie 12', 0, 'Prensa y gemelos.', 1, 'ADMIN', 1, NULL, @profesor_id);
SET @s12 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden) VALUES
(@s12, @e24, 12, 'reps', 40, 0), (@s12, @e25, 15, 'reps', 25, 1), (@s12, @e26, 20, 'reps', 20, 2);

INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, rutina_id, profesor_id)
VALUES ('Matt PF Serie 13', 0, 'Antebrazo y agarre.', 1, 'ADMIN', 2, NULL, @profesor_id);
SET @s13 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden) VALUES
(@s13, @e27, 15, 'reps', 12, 0), (@s13, @e28, 1, 'min', NULL, 1), (@s13, @e29, 12, 'reps', 10, 2);

INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, rutina_id, profesor_id)
VALUES ('Matt PF Serie 14', 0, 'HIIT tabata — intervalos.', 1, 'ADMIN', 4, NULL, @profesor_id);
SET @s14 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden) VALUES
(@s14, @e30, 1, 'min', NULL, 0), (@s14, @e1, 1, 'min', NULL, 1), (@s14, @e2, 1, 'min', NULL, 2), (@s14, @e3, 1, 'min', NULL, 3);

INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, rutina_id, profesor_id)
VALUES ('Matt PF Serie 15', 0, 'Movilidad de cadera.', 1, 'ADMIN', 1, NULL, @profesor_id);
SET @s15 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden) VALUES
(@s15, @e4, 60, 'seg', NULL, 0), (@s15, @e5, 45, 'seg', NULL, 1), (@s15, @e6, 15, 'reps', NULL, 2);

SELECT CONCAT('Series Matt PF en biblioteca: ', COUNT(*)) AS resultado
FROM serie WHERE nombre LIKE 'Matt PF Serie %' AND es_plantilla = 1 AND rutina_id IS NULL;
