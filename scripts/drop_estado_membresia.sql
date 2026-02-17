-- Elimina la columna estado_membresia de usuario (ya no se usa; el estado Activo/Inactivo lo reemplaza).
-- Ejecutar solo si la base de datos ya tenía esta columna y querés quitarla.
-- MySQL / MariaDB:
ALTER TABLE usuario DROP COLUMN estado_membresia;
