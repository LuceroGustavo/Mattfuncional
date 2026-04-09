-- =============================================================================
-- Borra TODOS los usuarios con rol ALUMNO y sus datos relacionados (FK).
-- NO elimina ADMIN, AYUDANTE ni DEVELOPER (sigue pudiendo entrar al panel).
--
-- Orden alineado con UsuarioService.eliminarUsuario + rutinas asignadas.
-- Evita Error 1175 de MySQL Workbench (safe updates) al desactivar el modo.
--
-- Uso típico: ejecutar 98 → luego 01_alumnos_prueba_15.sql (30 alumnos de prueba).
-- =============================================================================

USE mattfuncional;

SET @OLD_SAFE := @@SQL_SAFE_UPDATES;
SET SQL_SAFE_UPDATES = 0;

-- 1) Asistencia: primero N:M grupos trabajados
DELETE agt FROM asistencia_grupos_trabajados agt
INNER JOIN asistencia a ON agt.asistencia_id = a.id
INNER JOIN usuario u ON a.usuario_id = u.id
WHERE u.rol = 'ALUMNO';

-- 2) Soltar FK registrado_por si un alumno registró algo (poco habitual)
UPDATE asistencia a
INNER JOIN usuario u ON a.registrado_por_id = u.id
SET a.registrado_por_id = NULL
WHERE u.rol = 'ALUMNO';

-- 3) Asistencias del alumno
DELETE a FROM asistencia a
INNER JOIN usuario u ON a.usuario_id = u.id
WHERE u.rol = 'ALUMNO';

DELETE FROM medicion_fisica
WHERE usuario_id IN (SELECT id FROM usuario WHERE rol = 'ALUMNO');

DELETE FROM calendario_excepcion
WHERE usuario_id IN (SELECT id FROM usuario WHERE rol = 'ALUMNO');

DELETE FROM usuario_dias_horarios_asistencia
WHERE usuario_id IN (SELECT id FROM usuario WHERE rol = 'ALUMNO');

-- 4) Rutinas asignadas al alumno (no plantillas: usuario_id NOT NULL)
DELETE se FROM serie_ejercicio se
INNER JOIN serie s ON se.serie_id = s.id
INNER JOIN rutina r ON s.rutina_id = r.id
INNER JOIN usuario u ON r.usuario_id = u.id
WHERE u.rol = 'ALUMNO';

DELETE rc FROM rutina_categoria rc
INNER JOIN rutina r ON rc.rutina_id = r.id
INNER JOIN usuario u ON r.usuario_id = u.id
WHERE u.rol = 'ALUMNO';

DELETE s FROM serie s
INNER JOIN rutina r ON s.rutina_id = r.id
INNER JOIN usuario u ON r.usuario_id = u.id
WHERE u.rol = 'ALUMNO';

DELETE r FROM rutina r
INNER JOIN usuario u ON r.usuario_id = u.id
WHERE u.rol = 'ALUMNO';

-- 5) Alumnos
DELETE FROM usuario WHERE rol = 'ALUMNO';

SET SQL_SAFE_UPDATES = @OLD_SAFE;

SELECT CONCAT('Alumnos restantes: ', IFNULL((SELECT COUNT(*) FROM usuario WHERE rol = 'ALUMNO'), 0),
              ' | Usuarios sistema: ', IFNULL((SELECT COUNT(*) FROM usuario WHERE rol IN ('ADMIN','AYUDANTE','DEVELOPER')), 0)) AS resultado;
