-- Crear usuario y permisos para Mattfuncional (ejecutar en el servidor como root)
CREATE DATABASE IF NOT EXISTS mattfuncional CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'mattfuncional_user'@'localhost' IDENTIFIED BY 'Matt2026';
GRANT ALL PRIVILEGES ON mattfuncional.* TO 'mattfuncional_user'@'localhost';
FLUSH PRIVILEGES;
