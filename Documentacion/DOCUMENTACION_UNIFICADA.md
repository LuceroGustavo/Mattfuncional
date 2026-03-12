# Documentación unificada – Referencias y resúmenes

Contenido importante reunido de los documentos que antes estaban dispersos. Para contexto del proyecto usá [LEEME_PRIMERO.md](LEEME_PRIMERO.md), [AYUDA_MEMORIA.md](AYUDA_MEMORIA.md) y [PLAN_DE_DESARROLLO_UNIFICADO.md](PLAN_DE_DESARROLLO_UNIFICADO.md).

---

## 1. Resumen de lo implementado

- **Panel único:** Profesor (roles DEVELOPER, ADMIN, AYUDANTE). Sin panel alumno ni admin separado.
- **Ejercicios:** Predeterminados 1–60 desde `uploads/ejercicios/`; ABM; grupos musculares como entidad (`GrupoMuscular`); formularios y modal Ver alineados con series/rutinas.
- **Series y rutinas:** ABM; asignación rutina → alumno; enlace por token `/rutinas/hoja/{token}`; Copiar enlace y WhatsApp desde ficha alumno; orden de series; modificar rutina con tres bloques (Detalles, Series en rutina, Añadir más).
- **Alumnos:** Solo ficha (sin login). Estado ACTIVO/INACTIVO; filtros por nombre, estado, tipo, día/horario; columna Presente (ciclo Pendiente→Presente→Ausente). Al eliminar alumno se borran asistencias, mediciones, excepciones y rutinas asignadas.
- **Calendario:** Semanal; presente/ausente/pendiente por clic; excepciones por día; sincronizado con ficha y columna Presente en Mis Alumnos.
- **Progreso:** Modal en ficha (grupos trabajados, observaciones); sin checkbox presente; historial y resumen mensual con detalle por día.
- **Pizarra y sala TV:** Editor desde panel; vista TV en `/sala/{token}`; columnas editables; ejercicios con peso/rep.
- **Página pública:** Landing `/`, Planes `/planes`, consultas; administración en `/profesor/pagina-publica`.
- **Manual del usuario:** HTML en `/profesor/manual` (botón en panel); cubre acceso, panel, alumnos, ejercicios, series, rutinas, calendario, pizarra, usuarios, administración.
- **Backup (terminado Mar 2026):** Ver sección 2.

**Pendiente real:** Depuración de datos antiguos (archivar/eliminar asistencia de más de 12 meses). Ver PLAN_DE_DESARROLLO_UNIFICADO.md.

---

## 2. Backup y exportación

**Estado:** Terminado (marzo 2026). Acceso: Administración → Backup y resguardo.

| Funcionalidad | Descripción |
|---------------|-------------|
| **Ejercicios + grupos + rutinas + series** | Exportar/importar ZIP. Opciones por checkbox (Grupos, Ejercicios, Rutinas, Series). Modos Agregar o Suplantar. Imágenes con nombres originales. |
| **Alumnos – JSON** | Exportar backup (datos, mediciones, asistencias). Importar desde JSON (Agregar o Suplantar). |
| **Alumnos – Excel** | Exportar a Excel para reportes. Una fila por alumno; columna final "Último trabajo" (fecha + grupos y observaciones del último progreso). No se usa para importar. |

**Excel alumnos – columnas:** Título "Exportación de alumnos fecha dd/MM/yyyy". Columnas: Nombre, Correo, Celular, Edad, Sexo, Estado, Fecha de alta, Fecha baja, Tipo de asistencia, Días y horarios, Objetivos personales, Restricciones médicas, Notas profesor, Cantidad de asignaciones, **Último trabajo** (fecha en una línea, grupos y observaciones en la siguiente; ej. "11/03/26" y "CARDIO - CORE - trabajo muy bien"). No se exportan: Peso, Detalle asistencia, Contacto emergencia.

**Servicios:** `ExerciseZipBackupService`, `AlumnoJsonBackupService`, `AlumnoExportService`. Rutas en `AdminPanelController`: `/profesor/backup`, exportar-zip, importar, exportar-alumnos-json, importar-alumnos, exportar-alumnos-excel.

---

## 3. Despliegue y servidor

**Resumen:** App en VPS Donweb. Acceso SSH: `ssh -p 5638 root@149.50.144.53`. Aplicación en puerto 8080. Si PowerShell está bloqueado, usar Consola VNC de Donweb y menú `./iniciar-menu.sh` / `screen -r mattfuncional`. **Límite de subida (Nginx):** Para restaurar backups grandes, configurar `client_max_body_size` (ej. 50M) en la config de Nginx; ver archivo de ejemplo en `servidor/nginx-detodoya.conf`.

**Detalle completo:** [servidor/DESPLIEGUE-SERVIDOR.md](servidor/DESPLIEGUE-SERVIDOR.md) (acceso SSH, Consola VNC, menú, Nginx, reinicio, backups en servidor).

---

## 4. Manual del usuario – Índice de secciones

El manual en la app (`/profesor/manual`) incluye:

1. Acceso al sistema (URL, login, credenciales)
2. Panel del profesor (dashboard, botones, tabs)
3. Alumnos (lista, crear, editar, ficha, filtros, Presente, progreso, rutinas asignadas)
4. Ejercicios (lista, crear, editar, grupos musculares)
5. Series (crear, editar, ver)
6. Rutinas (crear, modificar, asignar, enlace, WhatsApp)
7. Calendario semanal (presente/ausente, excepciones)
8. Presentismo (columna Presente en Mis Alumnos)
9. Progreso del alumno (modal grupos + observaciones)
10. Pizarra en sala (editor, vista TV)
11. Usuarios del sistema (admin/ayudante, perfiles)
12. Administración (backup, página pública, etc.)
13. Resumen rápido (tabla "Quiero… / Dónde")

---

## 5. Referencias técnicas (una línea)

| Tema | Resumen |
|------|--------|
| **Grupos musculares** | Entidad `GrupoMuscular`; sistema + por profesor; ABM en `/profesor/mis-grupos-musculares`; ejercicios con `@ManyToMany`. |
| **Asistencia en calendario** | `CalendarioController`, `AsistenciaService`; endpoint `POST /calendario/api/marcar-asistencia` (estado PENDIENTE/PRESENTE/AUSENTE); columna Presente en Mis Alumnos usa el mismo endpoint. |
| **Pizarra / sala TV** | Fase 7. Editor en panel; vista `/sala/{token}`; API estado y actualizaciones; columnas y ejercicios con peso/rep. |
| **Página pública** | Fase 8. Landing `/`, Planes `/planes`, consultas; hero con video/carrusel; administración en panel. |
| **Ejercicios predeterminados** | `ExerciseCargaDefaultOptimizado.asegurarEjerciciosPredeterminados()`; imágenes en `uploads/ejercicios/` (1.webp–60.webp). |
| **Restricción AYUDANTE** | No puede acceder a "Administrar sistema"; redirección y mensaje si intenta entrar a `/profesor/administracion`. |
| **Eliminar alumno** | `UsuarioService.eliminarUsuario`: borra asistencias, mediciones, excepciones, rutinas asignadas; luego el usuario. |

---

*Última actualización: Marzo 2026. Para pendientes y checklist ver PLAN_DE_DESARROLLO_UNIFICADO.md.*
