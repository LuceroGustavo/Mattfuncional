-- =============================================================================
-- 10 rutinas plantilla (copian series desde la biblioteca Matt PF Serie 01–15)
-- Ejecutar después de 02_series_prueba_15.sql
-- token_publico = MD5(...) 32 caracteres hex (único por rutina)
-- =============================================================================

USE mattfuncional;

SET @profesor_id = COALESCE(
    (SELECT id FROM profesor WHERE correo = 'profesor@mattfuncional.com' LIMIT 1),
    (SELECT id FROM profesor ORDER BY id LIMIT 1)
);

SET @cat_fuerza = (SELECT id FROM categoria WHERE nombre = 'FUERZA' AND profesor_id IS NULL LIMIT 1);
SET @cat_cardio = (SELECT id FROM categoria WHERE nombre = 'CARDIO' AND profesor_id IS NULL LIMIT 1);
SET @cat_flex = (SELECT id FROM categoria WHERE nombre = 'FLEXIBILIDAD' AND profesor_id IS NULL LIMIT 1);
SET @cat_func = (SELECT id FROM categoria WHERE nombre = 'FUNCIONAL' AND profesor_id IS NULL LIMIT 1);
SET @cat_hiit = (SELECT id FROM categoria WHERE nombre = 'HIIT' AND profesor_id IS NULL LIMIT 1);

SET @serie_01 = (SELECT id FROM serie WHERE nombre = 'Matt PF Serie 01' AND es_plantilla = 1 AND rutina_id IS NULL AND profesor_id = @profesor_id ORDER BY id DESC LIMIT 1);
SET @serie_02 = (SELECT id FROM serie WHERE nombre = 'Matt PF Serie 02' AND es_plantilla = 1 AND rutina_id IS NULL AND profesor_id = @profesor_id ORDER BY id DESC LIMIT 1);
SET @serie_03 = (SELECT id FROM serie WHERE nombre = 'Matt PF Serie 03' AND es_plantilla = 1 AND rutina_id IS NULL AND profesor_id = @profesor_id ORDER BY id DESC LIMIT 1);
SET @serie_04 = (SELECT id FROM serie WHERE nombre = 'Matt PF Serie 04' AND es_plantilla = 1 AND rutina_id IS NULL AND profesor_id = @profesor_id ORDER BY id DESC LIMIT 1);
SET @serie_05 = (SELECT id FROM serie WHERE nombre = 'Matt PF Serie 05' AND es_plantilla = 1 AND rutina_id IS NULL AND profesor_id = @profesor_id ORDER BY id DESC LIMIT 1);
SET @serie_06 = (SELECT id FROM serie WHERE nombre = 'Matt PF Serie 06' AND es_plantilla = 1 AND rutina_id IS NULL AND profesor_id = @profesor_id ORDER BY id DESC LIMIT 1);
SET @serie_07 = (SELECT id FROM serie WHERE nombre = 'Matt PF Serie 07' AND es_plantilla = 1 AND rutina_id IS NULL AND profesor_id = @profesor_id ORDER BY id DESC LIMIT 1);
SET @serie_08 = (SELECT id FROM serie WHERE nombre = 'Matt PF Serie 08' AND es_plantilla = 1 AND rutina_id IS NULL AND profesor_id = @profesor_id ORDER BY id DESC LIMIT 1);
SET @serie_09 = (SELECT id FROM serie WHERE nombre = 'Matt PF Serie 09' AND es_plantilla = 1 AND rutina_id IS NULL AND profesor_id = @profesor_id ORDER BY id DESC LIMIT 1);
SET @serie_10 = (SELECT id FROM serie WHERE nombre = 'Matt PF Serie 10' AND es_plantilla = 1 AND rutina_id IS NULL AND profesor_id = @profesor_id ORDER BY id DESC LIMIT 1);
SET @serie_11 = (SELECT id FROM serie WHERE nombre = 'Matt PF Serie 11' AND es_plantilla = 1 AND rutina_id IS NULL AND profesor_id = @profesor_id ORDER BY id DESC LIMIT 1);
SET @serie_12 = (SELECT id FROM serie WHERE nombre = 'Matt PF Serie 12' AND es_plantilla = 1 AND rutina_id IS NULL AND profesor_id = @profesor_id ORDER BY id DESC LIMIT 1);
SET @serie_13 = (SELECT id FROM serie WHERE nombre = 'Matt PF Serie 13' AND es_plantilla = 1 AND rutina_id IS NULL AND profesor_id = @profesor_id ORDER BY id DESC LIMIT 1);
SET @serie_14 = (SELECT id FROM serie WHERE nombre = 'Matt PF Serie 14' AND es_plantilla = 1 AND rutina_id IS NULL AND profesor_id = @profesor_id ORDER BY id DESC LIMIT 1);
SET @serie_15 = (SELECT id FROM serie WHERE nombre = 'Matt PF Serie 15' AND es_plantilla = 1 AND rutina_id IS NULL AND profesor_id = @profesor_id ORDER BY id DESC LIMIT 1);

-- Rutina 01: fuerza completa (series 1+2+3)
INSERT INTO rutina (nombre, descripcion, estado, fecha_creacion, fecha_modificacion, es_plantilla, creador, token_publico, usuario_id, profesor_id)
VALUES ('Matt PF Rutina 01', 'Tres bloques: pecho/tríceps, piernas, espalda/bíceps.', 'ACTIVA', NOW(), NOW(), 1, 'ADMIN', MD5('mattfuncional-pf-rutina-01'), NULL, @profesor_id);
SET @r1 = LAST_INSERT_ID();
INSERT INTO rutina_categoria (rutina_id, categoria_id) VALUES (@r1, @cat_fuerza);
INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, plantilla_id, rutina_id, profesor_id)
SELECT nombre, 0, descripcion, 0, creador, repeticiones_serie, id, @r1, @profesor_id FROM serie WHERE id = @serie_01;
SET @r1s1 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden)
SELECT @r1s1, exercise_id, valor, unidad, peso, orden FROM serie_ejercicio WHERE serie_id = @serie_01;
INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, plantilla_id, rutina_id, profesor_id)
SELECT nombre, 1, descripcion, 0, creador, repeticiones_serie, id, @r1, @profesor_id FROM serie WHERE id = @serie_02;
SET @r1s2 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden)
SELECT @r1s2, exercise_id, valor, unidad, peso, orden FROM serie_ejercicio WHERE serie_id = @serie_02;
INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, plantilla_id, rutina_id, profesor_id)
SELECT nombre, 2, descripcion, 0, creador, repeticiones_serie, id, @r1, @profesor_id FROM serie WHERE id = @serie_03;
SET @r1s3 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden)
SELECT @r1s3, exercise_id, valor, unidad, peso, orden FROM serie_ejercicio WHERE serie_id = @serie_03;

-- Rutina 02: full body + cardio (4+6)
INSERT INTO rutina (nombre, descripcion, estado, fecha_creacion, fecha_modificacion, es_plantilla, creador, token_publico, usuario_id, profesor_id)
VALUES ('Matt PF Rutina 02', 'Circuito completo y bloque cardio/core.', 'ACTIVA', NOW(), NOW(), 1, 'ADMIN', MD5('mattfuncional-pf-rutina-02'), NULL, @profesor_id);
SET @r2 = LAST_INSERT_ID();
INSERT INTO rutina_categoria (rutina_id, categoria_id) VALUES (@r2, @cat_fuerza), (@r2, @cat_cardio);
INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, plantilla_id, rutina_id, profesor_id)
SELECT nombre, 0, descripcion, 0, creador, repeticiones_serie, id, @r2, @profesor_id FROM serie WHERE id = @serie_04;
SET @r2s1 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden)
SELECT @r2s1, exercise_id, valor, unidad, peso, orden FROM serie_ejercicio WHERE serie_id = @serie_04;
INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, plantilla_id, rutina_id, profesor_id)
SELECT nombre, 1, descripcion, 0, creador, repeticiones_serie, id, @r2, @profesor_id FROM serie WHERE id = @serie_06;
SET @r2s2 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden)
SELECT @r2s2, exercise_id, valor, unidad, peso, orden FROM serie_ejercicio WHERE serie_id = @serie_06;

-- Rutina 03: upper + lower (7+8)
INSERT INTO rutina (nombre, descripcion, estado, fecha_creacion, fecha_modificacion, es_plantilla, creador, token_publico, usuario_id, profesor_id)
VALUES ('Matt PF Rutina 03', 'Split superior e inferior.', 'ACTIVA', NOW(), NOW(), 1, 'ADMIN', MD5('mattfuncional-pf-rutina-03'), NULL, @profesor_id);
SET @r3 = LAST_INSERT_ID();
INSERT INTO rutina_categoria (rutina_id, categoria_id) VALUES (@r3, @cat_fuerza);
INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, plantilla_id, rutina_id, profesor_id)
SELECT nombre, 0, descripcion, 0, creador, repeticiones_serie, id, @r3, @profesor_id FROM serie WHERE id = @serie_07;
SET @r3s1 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden)
SELECT @r3s1, exercise_id, valor, unidad, peso, orden FROM serie_ejercicio WHERE serie_id = @serie_07;
INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, plantilla_id, rutina_id, profesor_id)
SELECT nombre, 1, descripcion, 0, creador, repeticiones_serie, id, @r3, @profesor_id FROM serie WHERE id = @serie_08;
SET @r3s2 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden)
SELECT @r3s2, exercise_id, valor, unidad, peso, orden FROM serie_ejercicio WHERE serie_id = @serie_08;

-- Rutina 04: hombros + elongación (5+9)
INSERT INTO rutina (nombre, descripcion, estado, fecha_creacion, fecha_modificacion, es_plantilla, creador, token_publico, usuario_id, profesor_id)
VALUES ('Matt PF Rutina 04', 'Hombros y cierre con movilidad.', 'ACTIVA', NOW(), NOW(), 1, 'ADMIN', MD5('mattfuncional-pf-rutina-04'), NULL, @profesor_id);
SET @r4 = LAST_INSERT_ID();
INSERT INTO rutina_categoria (rutina_id, categoria_id) VALUES (@r4, @cat_fuerza), (@r4, @cat_flex);
INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, plantilla_id, rutina_id, profesor_id)
SELECT nombre, 0, descripcion, 0, creador, repeticiones_serie, id, @r4, @profesor_id FROM serie WHERE id = @serie_05;
SET @r4s1 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden)
SELECT @r4s1, exercise_id, valor, unidad, peso, orden FROM serie_ejercicio WHERE serie_id = @serie_05;
INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, plantilla_id, rutina_id, profesor_id)
SELECT nombre, 1, descripcion, 0, creador, repeticiones_serie, id, @r4, @profesor_id FROM serie WHERE id = @serie_09;
SET @r4s2 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden)
SELECT @r4s2, exercise_id, valor, unidad, peso, orden FROM serie_ejercicio WHERE serie_id = @serie_09;

-- Rutina 05: alta intensidad (10+1+3)
INSERT INTO rutina (nombre, descripcion, estado, fecha_creacion, fecha_modificacion, es_plantilla, creador, token_publico, usuario_id, profesor_id)
VALUES ('Matt PF Rutina 05', 'Mixto intenso más empuje y tirón.', 'ACTIVA', NOW(), NOW(), 1, 'ADMIN', MD5('mattfuncional-pf-rutina-05'), NULL, @profesor_id);
SET @r5 = LAST_INSERT_ID();
INSERT INTO rutina_categoria (rutina_id, categoria_id) VALUES (@r5, @cat_fuerza), (@r5, @cat_hiit);
INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, plantilla_id, rutina_id, profesor_id)
SELECT nombre, 0, descripcion, 0, creador, repeticiones_serie, id, @r5, @profesor_id FROM serie WHERE id = @serie_10;
SET @r5s1 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden)
SELECT @r5s1, exercise_id, valor, unidad, peso, orden FROM serie_ejercicio WHERE serie_id = @serie_10;
INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, plantilla_id, rutina_id, profesor_id)
SELECT nombre, 1, descripcion, 0, creador, repeticiones_serie, id, @r5, @profesor_id FROM serie WHERE id = @serie_01;
SET @r5s2 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden)
SELECT @r5s2, exercise_id, valor, unidad, peso, orden FROM serie_ejercicio WHERE serie_id = @serie_01;
INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, plantilla_id, rutina_id, profesor_id)
SELECT nombre, 2, descripcion, 0, creador, repeticiones_serie, id, @r5, @profesor_id FROM serie WHERE id = @serie_03;
SET @r5s3 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden)
SELECT @r5s3, exercise_id, valor, unidad, peso, orden FROM serie_ejercicio WHERE serie_id = @serie_03;

-- Rutina 06: core + cardio (11+6)
INSERT INTO rutina (nombre, descripcion, estado, fecha_creacion, fecha_modificacion, es_plantilla, creador, token_publico, usuario_id, profesor_id)
VALUES ('Matt PF Rutina 06', 'Abdominales y cardio express.', 'ACTIVA', NOW(), NOW(), 1, 'ADMIN', MD5('mattfuncional-pf-rutina-06'), NULL, @profesor_id);
SET @r6 = LAST_INSERT_ID();
INSERT INTO rutina_categoria (rutina_id, categoria_id) VALUES (@r6, @cat_func), (@r6, @cat_cardio);
INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, plantilla_id, rutina_id, profesor_id)
SELECT nombre, 0, descripcion, 0, creador, repeticiones_serie, id, @r6, @profesor_id FROM serie WHERE id = @serie_11;
SET @r6s1 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden)
SELECT @r6s1, exercise_id, valor, unidad, peso, orden FROM serie_ejercicio WHERE serie_id = @serie_11;
INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, plantilla_id, rutina_id, profesor_id)
SELECT nombre, 1, descripcion, 0, creador, repeticiones_serie, id, @r6, @profesor_id FROM serie WHERE id = @serie_06;
SET @r6s2 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden)
SELECT @r6s2, exercise_id, valor, unidad, peso, orden FROM serie_ejercicio WHERE serie_id = @serie_06;

-- Rutina 07: HIIT + movilidad (14+15)
INSERT INTO rutina (nombre, descripcion, estado, fecha_creacion, fecha_modificacion, es_plantilla, creador, token_publico, usuario_id, profesor_id)
VALUES ('Matt PF Rutina 07', 'Intervalos y cadera.', 'ACTIVA', NOW(), NOW(), 1, 'ADMIN', MD5('mattfuncional-pf-rutina-07'), NULL, @profesor_id);
SET @r7 = LAST_INSERT_ID();
INSERT INTO rutina_categoria (rutina_id, categoria_id) VALUES (@r7, @cat_hiit), (@r7, @cat_flex);
INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, plantilla_id, rutina_id, profesor_id)
SELECT nombre, 0, descripcion, 0, creador, repeticiones_serie, id, @r7, @profesor_id FROM serie WHERE id = @serie_14;
SET @r7s1 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden)
SELECT @r7s1, exercise_id, valor, unidad, peso, orden FROM serie_ejercicio WHERE serie_id = @serie_14;
INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, plantilla_id, rutina_id, profesor_id)
SELECT nombre, 1, descripcion, 0, creador, repeticiones_serie, id, @r7, @profesor_id FROM serie WHERE id = @serie_15;
SET @r7s2 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden)
SELECT @r7s2, exercise_id, valor, unidad, peso, orden FROM serie_ejercicio WHERE serie_id = @serie_15;

-- Rutina 08: empuje + tirón (7+3) — equivalente a inclinado + remo con 15 series
INSERT INTO rutina (nombre, descripcion, estado, fecha_creacion, fecha_modificacion, es_plantilla, creador, token_publico, usuario_id, profesor_id)
VALUES ('Matt PF Rutina 08', 'Upper empuje y bloque de espalda.', 'ACTIVA', NOW(), NOW(), 1, 'ADMIN', MD5('mattfuncional-pf-rutina-08'), NULL, @profesor_id);
SET @r8 = LAST_INSERT_ID();
INSERT INTO rutina_categoria (rutina_id, categoria_id) VALUES (@r8, @cat_fuerza);
INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, plantilla_id, rutina_id, profesor_id)
SELECT nombre, 0, descripcion, 0, creador, repeticiones_serie, id, @r8, @profesor_id FROM serie WHERE id = @serie_07;
SET @r8s1 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden)
SELECT @r8s1, exercise_id, valor, unidad, peso, orden FROM serie_ejercicio WHERE serie_id = @serie_07;
INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, plantilla_id, rutina_id, profesor_id)
SELECT nombre, 1, descripcion, 0, creador, repeticiones_serie, id, @r8, @profesor_id FROM serie WHERE id = @serie_03;
SET @r8s2 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden)
SELECT @r8s2, exercise_id, valor, unidad, peso, orden FROM serie_ejercicio WHERE serie_id = @serie_03;

-- Rutina 09: circuito suave + elongación (4+9)
INSERT INTO rutina (nombre, descripcion, estado, fecha_creacion, fecha_modificacion, es_plantilla, creador, token_publico, usuario_id, profesor_id)
VALUES ('Matt PF Rutina 09', 'Sesión moderada y movilidad.', 'ACTIVA', NOW(), NOW(), 1, 'ADMIN', MD5('mattfuncional-pf-rutina-09'), NULL, @profesor_id);
SET @r9 = LAST_INSERT_ID();
INSERT INTO rutina_categoria (rutina_id, categoria_id) VALUES (@r9, @cat_flex), (@r9, @cat_func);
INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, plantilla_id, rutina_id, profesor_id)
SELECT nombre, 0, descripcion, 0, creador, repeticiones_serie, id, @r9, @profesor_id FROM serie WHERE id = @serie_04;
SET @r9s1 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden)
SELECT @r9s1, exercise_id, valor, unidad, peso, orden FROM serie_ejercicio WHERE serie_id = @serie_04;
INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, plantilla_id, rutina_id, profesor_id)
SELECT nombre, 1, descripcion, 0, creador, repeticiones_serie, id, @r9, @profesor_id FROM serie WHERE id = @serie_09;
SET @r9s2 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden)
SELECT @r9s2, exercise_id, valor, unidad, peso, orden FROM serie_ejercicio WHERE serie_id = @serie_09;

-- Rutina 10: máquina + piernas (12+2)
INSERT INTO rutina (nombre, descripcion, estado, fecha_creacion, fecha_modificacion, es_plantilla, creador, token_publico, usuario_id, profesor_id)
VALUES ('Matt PF Rutina 10', 'Prensa/gemelos y piernas libres.', 'ACTIVA', NOW(), NOW(), 1, 'ADMIN', MD5('mattfuncional-pf-rutina-10'), NULL, @profesor_id);
SET @r10 = LAST_INSERT_ID();
INSERT INTO rutina_categoria (rutina_id, categoria_id) VALUES (@r10, @cat_fuerza);
INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, plantilla_id, rutina_id, profesor_id)
SELECT nombre, 0, descripcion, 0, creador, repeticiones_serie, id, @r10, @profesor_id FROM serie WHERE id = @serie_12;
SET @r10s1 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden)
SELECT @r10s1, exercise_id, valor, unidad, peso, orden FROM serie_ejercicio WHERE serie_id = @serie_12;
INSERT INTO serie (nombre, orden, descripcion, es_plantilla, creador, repeticiones_serie, plantilla_id, rutina_id, profesor_id)
SELECT nombre, 1, descripcion, 0, creador, repeticiones_serie, id, @r10, @profesor_id FROM serie WHERE id = @serie_02;
SET @r10s2 = LAST_INSERT_ID();
INSERT INTO serie_ejercicio (serie_id, exercise_id, valor, unidad, peso, orden)
SELECT @r10s2, exercise_id, valor, unidad, peso, orden FROM serie_ejercicio WHERE serie_id = @serie_02;

SELECT CONCAT('Rutinas Matt PF plantilla: ', COUNT(*)) AS resultado
FROM rutina WHERE nombre LIKE 'Matt PF Rutina %' AND es_plantilla = 1 AND usuario_id IS NULL;
