# Documentación unificada – Referencias y resúmenes

Contenido importante reunido de los documentos que antes estaban dispersos. Para contexto del proyecto usá [LEEME_PRIMERO.md](LEEME_PRIMERO.md) y [PLAN_DE_DESARROLLO_UNIFICADO.md](PLAN_DE_DESARROLLO_UNIFICADO.md).

**Mantenimiento:** Preferir **editar este archivo** (y los demás ya listados en `LEEME_PRIMERO.md` §3) antes que crear documentos nuevos. Detalle de versiones: [CHANGELOG.md](../CHANGELOG.md).

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
- **Vistas responsive (mar 2026):** Alineación con MiGymVirtual para panel, ficha alumno, series, rutinas (crear/asignar), login, ejercicios, grupos musculares, hojas y modales según alcance acordado. Detalle y lista de pantallas en **[PLAN_MODIFICACION_VISTAS.md](PLAN_MODIFICACION_VISTAS.md)** §4.2.1.
- **Backup (implementación terminada mar 2026):** Ver sección 2 — **las pruebas manuales del flujo completo siguen pendientes** (validación en uso).
- **Depuración de datos (terminado Feb 2026):** Ver sección 2.1.

---

## 2. Backup y exportación

**Estado implementación:** Cerrado en código (marzo 2026). Acceso: **Administración → Backup y resguardo**.

**Validación pendiente:** Conviene ejecutar la receta de prueba del equipo (export ZIP → modificar/borrar datos de prueba en la misma BD → importar con **Suplantar** → verificar listados, rutinas, series y vista **Ver serie** sin errores). Incluye el comportamiento nuevo (manifest v1.1, imágenes hasta 50 MB en restore, `READ_COMMITTED`, normalización NFC). Ver pie de esta sección y `CHANGELOG.md` **[2026-03-30]**.

| Funcionalidad | Descripción |
|---------------|-------------|
| **Ejercicios + grupos + rutinas + series** | Exportar/importar ZIP. Opciones por checkbox (Grupos, Ejercicios, Rutinas, Series). Modos Agregar o Suplantar. Imágenes con nombres originales. |
| **Alumnos – JSON** | Exportar backup (datos, mediciones, asistencias). Importar desde JSON (Agregar o Suplantar). |
| **Alumnos – Excel** | Exportar a Excel para reportes. Una fila por alumno; columna final "Último trabajo" (fecha + grupos y observaciones del último progreso). No se usa para importar. |

**Excel alumnos – columnas:** Título "Exportación de alumnos fecha dd/MM/yyyy". Columnas: Nombre, Correo, Celular, Edad, Sexo, Estado, Fecha de alta, Fecha baja, Tipo de asistencia, Días y horarios, Objetivos personales, Restricciones médicas, Notas profesor, Cantidad de asignaciones, **Último trabajo** (fecha en una línea, grupos y observaciones en la siguiente; ej. "11/03/26" y "CARDIO - CORE - trabajo muy bien"). No se exportan: Peso, Detalle asistencia, Contacto emergencia.

**Servicios:** `ExerciseZipBackupService`, `AlumnoJsonBackupService`, `AlumnoExportService`. Rutas en `AdminPanelController`: `/profesor/backup`, exportar-zip, importar, exportar-alumnos-json, importar-alumnos, exportar-alumnos-excel.

**Dónde deben estar las imágenes en disco (export ZIP):** la app guarda y lee bajo `mattfuncional.uploads.dir` + `ejercicios/` (por defecto `%USERPROFILE%\Mattfuncional\uploads\ejercicios` en Windows). Si en desarrollo tenés los `1.webp`… solo en `<proyecto>\uploads\ejercicios\`, el export también los busca ahí (misma carpeta relativa al directorio de trabajo del proceso) para armar la carpeta `imagenes/` del ZIP.

**ZIP ejercicios — detalle técnico (marzo 2026):**
- **`manifest.json`:** Versión **1.1** incluye `cantidadSeriesBiblioteca` (series plantilla sin rutina, “Mis series” sueltas) y `cantidadSeriesEnRutinas` (series dentro de rutinas plantilla). `cantidadSeries` sigue siendo la suma (total en `series.json`). ZIPs exportados antes solo traen v1.0 con `cantidadSeries`; la pantalla de import sigue mostrándolo.
- **Import:** Imágenes en restore admiten hasta **50 MB** por archivo (el formulario manual de ejercicios sigue limitado a 5 MB). Al enlazar series, cada ejercicio se resuelve por nombre (con **trim y normalización Unicode NFC**) y se persiste con referencia válida en BD para evitar errores de clave foránea.
- **Categorías:** Si el ZIP incluye `categorias.json` y hay profesor de restauración, se importan **aunque no se marquen** Rutinas ni Series (así no se pierden categorías propias al traer solo ejercicios o grupos). Ver `CHANGELOG.md` **[2026-04-08]**.
- **Transacción:** La importación ZIP (`importarDesdeZip`) usa aislamiento **READ_COMMITTED**. Con el aislamiento por defecto de MySQL (`REPEATABLE_READ`), tras borrar y recrear ejercicios en transacciones independientes (`REQUIRES_NEW`), una lectura en la transacción principal podía seguir “viendo” IDs antiguos y fallar la FK al insertar `serie_ejercicio`.
- **Vista previa:** Al elegir el archivo ZIP, el resumen muestra el desglose de series cuando el manifest trae los campos v1.1.
- **Pruebas recomendadas — pendiente:** exportar ZIP → borrar o modificar datos de prueba en la misma BD → importar con **Suplantar** → comprobar rutinas, series, ejercicios y “Ver serie” sin errores. Ver `CHANGELOG.md` **[2026-03-30]** y **[2026-04-08]** (categorías, mapa ejercicios, `plantilla_id` al guardar rutina).
- **Análisis largo de bugs (scripts SQL, FK, abril 2026):** quedó absorbido por las correcciones en código y entradas de `CHANGELOG.md` citadas arriba; no se mantiene un PDF separado en el repo.

---

## 2.1 Depuración de datos

**Estado:** Terminado (febrero 2026). Acceso: Administración → Depuración de datos (entre Sistema de backups y Manual de usuario).

Permite eliminar registros antiguos para mantener la base de datos ligera. Dos tarjetas independientes:

| Funcionalidad | Descripción |
|---------------|-------------|
| **Registro de asistencias e inasistencias** | Se elige una fecha límite. Se eliminan todos los registros con fecha **anterior** a la elegida (ej.: 12/12/2025 → se borra todo antes de esa fecha). Acción irreversible; se recomienda hacer backup antes. |
| **Rutinas asignadas a alumnos** | Se elige una fecha límite. Se eliminan todas las rutinas asignadas cuya fecha de creación es **anterior** a la elegida. Las rutinas plantilla (Mis Rutinas) no se tocan. Acción irreversible. |

**Servicios:** `DepuracionService`. Rutas en `AdminPanelController`: `GET /profesor/depuracion`, `POST /profesor/depuracion/asistencias`, `POST /profesor/depuracion/rutinas-asignadas`. Repositorios: `AsistenciaRepository` (countByFechaBefore, deleteByFechaBefore), `RutinaRepository` (findByEsPlantillaFalseAndFechaCreacionBefore).

### 2.2 Modales y avisos unificados (confirmaciones y alertas)

**Estado:** Completado (febrero 2026). En toda la app las confirmaciones y avisos usan modales con estilo Mattfuncional (cabecera morada `.modal-confirmar-header`, pie `.modal-confirmar-footer` en `style.css`), reemplazando `alert()` y `confirm()` nativos del navegador.

**Vistas con modal de confirmación y/o alerta:**

| Vista | Confirmación | Alerta (éxito/error/info) |
|-------|--------------|---------------------------|
| Panel Administración (backup, depuracion, usuarios-sistema, pagina-publica-admin) | Sí | Sí |
| Dashboard profesor | Eliminar serie, rutina, rutina asignada | Enlace copiado, “Debe ser administrador” |
| Detalle alumno | Eliminar alumno, inactivar todas las rutinas | Enlace copiado, “Datos actualizados” (flash) |
| Series crear/editar | — | Validación, éxito con redirección, errores |
| Rutinas crear | — | Nombre y al menos una serie |
| Ejercicios lista (profesor) | Eliminar ejercicio | — |
| Grupos musculares | Eliminar grupo | — |
| Pizarra lista | Eliminar pizarra | Código 4 dígitos, errores, enlace copiado |
| Pizarra editor | Quitar columna, eliminar ejercicio, nuevo enlace TV | Todos los mensajes (nombre, errores, enlace copiado) |
| Listado ejercicios (ejercicios) | — | “Ejercicio(s) agregado(s) a rutina” |

**Editar alumno:** Tras guardar, redirección al detalle del alumno (`/profesor/alumnos/{id}`) con mensaje flash “Datos del alumno actualizados correctamente.” (ya no redirige al dashboard).

**Vista Mis Ejercicios:** No se muestra la estrella azul ni el aviso “La estrellita azul indica ejercicios predeterminados del sistema.”; todas las filas tienen el mismo estilo (sin `table-info` en predeterminados).

**Referencia:** CHANGELOG entrada [2026-02-09] feat(ux): modales unificados y mejoras en flujos.

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
12. Administración (backup, depuración de datos, página pública, etc.)
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
| **Depuración de datos** | `DepuracionService`; panel en `/profesor/depuracion`; elimina asistencias o rutinas asignadas anteriores a una fecha elegida. |

---

*Última actualización: 8 abr 2026 — §2: import de `categorias.json` aunque Rutinas/Series desmarcados (`CHANGELOG` 2026-04-08). Resto: 30 mar 2026 (responsive, backup, modales). Pendientes de proceso: `PLAN_DE_DESARROLLO_UNIFICADO.md`.*
