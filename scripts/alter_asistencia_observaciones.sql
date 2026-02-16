-- Amplía la columna observaciones de la tabla asistencia para permitir hasta 2000 caracteres.
-- Ejecutar una sola vez si la columna fue creada con longitud menor (p. ej. VARCHAR(255)).
-- MySQL / MariaDB:
ALTER TABLE asistencia MODIFY COLUMN observaciones VARCHAR(2000) NULL;
