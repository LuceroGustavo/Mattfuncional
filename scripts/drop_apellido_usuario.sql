-- Elimina la columna apellido de usuario (solo se usa nombre completo).
-- Ejecutar solo si la base de datos ya tenía esta columna.
-- MySQL / MariaDB:
ALTER TABLE usuario DROP COLUMN apellido;
