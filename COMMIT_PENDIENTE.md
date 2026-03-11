# Commit pendiente – Documentación de acceso y Nginx

**Fecha:** Marzo 2026  
**Para hacer desde casa** (red bloqueada en el trabajo).

---

## Archivos modificados (sin commitear)

| Archivo | Cambios |
|---------|---------|
| `Documentacion/LEEME_PRIMERO.md` | Nueva sección "0. Cómo ingresar": URLs (detodoya.com.ar, localhost, IP), páginas públicas, credenciales de desarrollo (profesor, developer) |
| `Documentacion/MANUAL-USUARIO.md` | Sección "1. Acceso al sistema" ampliada: URLs concretas, usuario de prueba profesor@mattfuncional.com / profesor |
| `Documentacion/servidor/DESPLIEGUE-SERVIDOR.md` | Nueva sección "8.1 Modificar límite de subida (client_max_body_size)": instrucciones SSH, SCP y VNC para aplicar/cambiar el límite en Nginx |

---

## Comandos para commit y push

```bash
cd C:\Users\ESS03\Desktop\APPS\Mattfuncional

git add Documentacion/LEEME_PRIMERO.md Documentacion/MANUAL-USUARIO.md Documentacion/servidor/DESPLIEGUE-SERVIDOR.md CHANGELOG.md COMMIT_PENDIENTE.md tarea_actual.md

git commit -m "docs: acceso al sistema y límite de subida Nginx

- LEEME_PRIMERO: sección Cómo ingresar (URLs, credenciales dev)
- MANUAL-USUARIO: URLs concretas y usuario de prueba
- DESPLIEGUE-SERVIDOR: sección 8.1 client_max_body_size (SSH, SCP, VNC)
- CHANGELOG: entrada docs acceso y Nginx"

git push origin main
```

---

## Resumen de cambios de esta sesión (ya commiteados o incluidos)

### 1. Backup ZIP – Nombres originales y series
- **ExerciseZipBackupService:** Export con `rutaArchivo` (1.webp, 2.webp); import con `guardarParaRestore(bytes, rutaEnZip)`; parámetro `Profesor` para rutinas/series
- **ImagenServicio:** `guardarParaRestore(byte[], String rutaEnZip)` preserva nombre del ZIP
- **AdminPanelController:** Inyecta ProfesorService; pasa profesor a importarDesdeZip
- **ExerciseService:** deleteExercise elimina archivo físico
- **RutinaRepository:** findByNombreAndEsPlantillaTrueAndProfesorId
- **Fix:** Código duplicado al final de ExerciseZipBackupService

### 2. Documentación
- **PLAN_BACKUP_Y_EXPORTACION.md:** Cambios recientes, archivos modificados, pendiente testear
- **CHANGELOG.md:** Entrada fix(backup) nombres originales y series
- **CHANGELOG_UNIFICADO_FEB2026.md:** Sección 22
- **LEEME_PRIMERO.md:** Cómo ingresar (pendiente commit)
- **MANUAL-USUARIO.md:** Acceso al sistema (pendiente commit)
- **DESPLIEGUE-SERVIDOR.md:** Sección 8.1 Nginx client_max_body_size (pendiente commit)

---

## Pendiente testear (desde casa)

- [ ] Exportar backup con ejercicios → verificar ZIP con imagenes/1.webp, 2.webp
- [ ] Importar con "Suplantar" → verificar imágenes en uploads/ejercicios/
- [ ] Restauración de series visible en panel del profesor
- [ ] Aplicar client_max_body_size en Nginx del servidor (SSH o VNC)
