# Changelog unificado – Febrero 2026

Un solo documento con todos los cambios documentados por feature en Febrero 2026 (commit/push y referencia).

---

## Índice

1. [Grupos musculares como entidad](#1-grupos-musculares-como-entidad)
2. [Orden de series en rutinas y orden de ejercicios en series](#2-orden-de-series-en-rutinas-y-orden-de-ejercicios-en-series)
3. [Rutinas, series y modificar rutina](#3-rutinas-series-y-modificar-rutina)
4. [Modal de progreso y ficha del alumno](#4-modal-de-progreso-y-ficha-del-alumno)
5. [Ejercicios (panel) y hoja de rutina pública](#5-ejercicios-panel-y-hoja-de-rutina-pública)
6. [Alumno inactivo y limpieza en detalle del alumno](#6-alumno-inactivo-y-limpieza-en-detalle-del-alumno)
7. [Calendario y presentismo – Cierre](#7-calendario-y-presentismo--cierre-feb-2026)

---

## 1. Grupos musculares como entidad

**Resumen:** El profesor puede crear grupos musculares propios y usarlos en ejercicios, series y filtros. El grupo muscular deja de ser un enum fijo y pasa a ser una entidad en base de datos.

### 1.1 Cambio conceptual
- **Antes:** Grupo muscular era un **enum** `MuscleGroup` (BRAZOS, PIERNAS, PECHO, ESPALDA, CARDIO, ELONGACION). No se podían añadir grupos nuevos sin cambiar código.
- **Ahora:** Grupo muscular es la **entidad `GrupoMuscular`** (tabla `grupo_muscular`). Los 6 grupos anteriores existen como “grupos del sistema” (`profesor_id = null`) y se crean al arranque. El profesor puede **crear, editar y eliminar** sus propios grupos desde el panel (“Grupos Musculares”). En ejercicios, series y filtros se usan siempre entidades (por id y nombre).

### 1.2 Cambios técnicos principales
- **Entidad `GrupoMuscular`:** id, nombre, profesor (ManyToOne, nullable). Restricción única (nombre, profesor_id).
- **Exercise:** de `@ElementCollection Set<MuscleGroup>` a `@ManyToMany Set<GrupoMuscular>` con tabla `exercise_grupos`.
- **Inicialización:** en `DataInitializer` se llama a `GrupoMuscularService.asegurarGruposSistema()` y se crean los 6 grupos del sistema si no existen.
- **Servicios:** GrupoMuscularService (listar, resolver por IDs/nombres, guardar, eliminar). ExerciseService / ExerciseCargaDefaultOptimizado / ExportImport / Asignacion usan `Set<GrupoMuscular>`.
- **Controladores:** ProfesorController rutas `/profesor/mis-grupos-musculares` (listar, nuevo, crear, editar, actualizar, eliminar). ExerciseController y SerieController: filtro por `grupoId`.
- **Vistas:** Listados y filtros con entidad; formularios de ejercicio con checkboxes `name="grupoIds"`. Nuevas: `grupos-musculares-lista.html`, `grupo-muscular-form.html`.
- **Export/Import:** Export con nombres de grupos; import con resolución por nombre (sistema + profesor).
- **Eliminado:** Enum `MuscleGroup` y todas sus referencias.

### 1.3 Rutas nuevas (ABM grupos musculares)
| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/profesor/mis-grupos-musculares` | Lista grupos del sistema + grupos propios. |
| GET | `/profesor/mis-grupos-musculares/nuevo` | Formulario de alta. |
| POST | `/profesor/mis-grupos-musculares/nuevo` | Crear grupo. |
| GET | `/profesor/mis-grupos-musculares/editar/{id}` | Formulario de edición (solo grupos propios). |
| POST | `/profesor/mis-grupos-musculares/editar/{id}` | Guardar cambios. |
| GET | `/profesor/mis-grupos-musculares/eliminar/{id}` | Eliminar grupo (solo grupos propios). |

### 1.4 Archivos creados / eliminados
- **Creados:** `GrupoMuscular.java`, `GrupoMuscularRepository.java`, `GrupoMuscularService.java`, `grupos-musculares-lista.html`, `grupo-muscular-form.html`.
- **Eliminado:** `enums/MuscleGroup.java`.

---

## 2. Orden de series en rutinas y orden de ejercicios en series

**Resumen:** Reordenamiento de series dentro de una rutina y de ejercicios dentro de una serie, con persistencia del orden y botones Subir/Bajar.

### 2.1 Rutinas – Orden de las series
- En **crear rutina:** lista "Series seleccionadas (orden en la rutina)" con botones **Subir** y **Bajar**. El orden al guardar se persiste.
- En **editar rutina:** cada serie en "Series en esta Rutina" tiene **Subir** y **Bajar**. Al guardar, el orden se actualiza.
- Campo **`Serie.orden`** se asigna al crear/actualizar (0, 1, 2, …). Hoja pública y vista al editar muestran series ordenadas por `orden`.

### 2.2 Series – Orden de los ejercicios
- En **crear/editar serie:** tabla "Ejercicios en esta Serie" con **Subir** y **Bajar** por fila.
- Campo **`SerieEjercicio.orden`** para persistir la posición. Vista Ver serie y hoja de rutina muestran ejercicios ordenados por `orden`.

### 2.3 Cambios técnicos
- **RutinaService:** `agregarSerieARutina` asigna `orden`; `obtenerRutinaPorIdConSeries` y `obtenerRutinaPorToken` ordenan series y serieEjercicios por `orden`. `asignarRutinaPlantillaAUsuario` copia series en orden.
- **SerieService:** `copiarSerieParaNuevaRutina(Serie, Rutina, int orden)`; en crear/actualizar serie se asigna `orden` a cada SerieEjercicio.
- **SerieEjercicio.java:** nuevo campo `Integer orden = 0`.
- **Frontend:** `crearRutina.html` y `editarRutina.html` con Subir/Bajar; `crearSerie.html` con columna Orden/Acción y eventos para mover filas.
- **Base de datos:** columna `serie_ejercicio.orden` (INT, DEFAULT 0). Migración: `ALTER TABLE serie_ejercicio ADD COLUMN orden INT DEFAULT 0;` si no se usa ddl-auto=update.

---

## 3. Rutinas, series y modificar rutina

**Resumen:** Corrección del guardado al modificar rutina (conservar todas las series) y mejoras en el flujo de series/rutinas.

### 3.1 Corrección crítica: guardado al modificar rutina
- **Problema:** Al guardar una rutina editada solo se conservaba una serie (o la última); al editar de nuevo solo se veía una serie; en "Ver rutina" seguían las series viejas.
- **Causa:** En `RutinaService.actualizarSeriesDeRutina` se borraban todas las series y luego se intentaba re-añadir con `findById(serieId)`; esas series ya estaban eliminadas, así que solo quedaban las "nuevas".
- **Solución:** Antes de borrar, para cada serie existente se guardan `plantillaId` (o id si es plantilla) y repeticiones en listas; se borran las series; se re-añaden todas (existentes + nuevas) con `agregarSerieARutina`. Archivo: `RutinaService.java`.

### 3.2 Mejoras en el flujo
- **Crear rutina:** Botón "Modificar" en cada tarjeta de serie; carga correcta de ejercicios de la serie en el modal.
- **Modificar rutina:** Título "Modificar Rutina"; carga con `findByIdWithSeries`; layout: detalles a la izquierda, "Series en esta Rutina" arriba a la derecha, "Añadir más Series" abajo; filtro de series disponibles; selección por clic en tarjeta (amarillo/naranja); vista previa "Se agregarán al guardar".
- **Mis Series:** Botón "Ver serie" en Acciones; sin columna ID.
- **Tab correcto tras guardar:** Parámetro `?tab=series|rutinas|asignaciones` en URL y redirect.
- **Logo:** Referencias a `logo matt.jpeg` en navbars.
- **Crear/editar serie:** `editMode` y `serieDTOJson` en modelo para evitar error en template.

### 3.3 Avances posteriores (Fase 4 y 09/02/2026)
- Enlace público con token; Copiar enlace; WhatsApp con celular del alumno; Asignar rutina con ver (ojo); Ficha alumno con tabla compacta de rutinas; Asistencia "Dar presente" en tabla; Crear serie en selector; Ver serie en `verSerie.html`.
- Calendario semanal; presentismo rápido; acciones compactas; inactivos con tooltip; filtros persistentes; editar alumno con estado y historial; tipos Virtual/Semipresencial; seed SQL 30 alumnos.

---

## 4. Modal de progreso y ficha del alumno

**Resumen:** Mejoras en registro de progreso (asistencia, grupos musculares, observaciones) y un solo flujo desde panel y desde detalle del alumno.

### 4.1 Cambios
- **Checkbox "Marcar como presente":** Eliminado del modal (el registro de progreso no fuerza presente).
- **Varios grupos musculares:** Sustituido `<select multiple>` por **checkboxes**; en historial se muestran separados por coma.
- **Observaciones largas:** Columna `observaciones` ampliada a 2000 caracteres (script `alter_asistencia_observaciones.sql`); textareas con `maxlength="2000"`.
- **Un solo modal de progreso:** El botón "Progreso" en la tabla del panel redirige a `/profesor/alumnos/{id}?openModal=progreso`; en alumno-detalle un script abre el modal si existe ese query param. Eliminado modal y JS de progreso en dashboard.

### 4.2 Archivos modificados
- **alumno-detalle.html:** checkboxes para grupos; textarea 2000; script para abrir modal con `openModal=progreso`; modal de resumen mensual de asistencias con detalle por día.
- **dashboard.html:** Botón Progreso → enlace a ficha con `?openModal=progreso`; eliminado modal y JS de progreso.
- **scripts/alter_asistencia_observaciones.sql:** `ALTER TABLE asistencia MODIFY COLUMN observaciones VARCHAR(2000) NULL;`

---

## 5. Ejercicios (panel) y hoja de rutina pública

**Resumen:** Panel de ejercicios (Mis Ejercicios) con Ver en modal y hoja de rutina accesible sin login.

### 5.1 Panel de ejercicios
- **Predeterminados editables y eliminables:** Todos los ejercicios pueden editarse y eliminarse; en ProfesorController se quitó la validación que impedía eliminar predeterminados.
- **Indicador:** Solo estrellita azul junto al nombre; leyenda arriba de la tabla.
- **Botones:** Ver (azul), Editar (amarillo), Eliminar (rojo); contenedor flex en Acciones.
- **Ver en modal:** Al clic en "Ver" se abre overlay/modal en la misma página (imagen, grupo, nombre, descripción); cerrar con clic fuera o Escape. Ruta `/profesor/mis-ejercicios/ver/{id}` y plantilla `ver-ejercicio.html` se mantienen opcionales.

### 5.2 Hoja de rutina pública
- **Problema:** El enlace de la rutina pedía login.
- **Solución:** En SecurityConfig: `.requestMatchers("/rutinas/hoja/**").permitAll()`. `/rutinas/hoja/{token}` es público; el resto de `/rutinas/**` sigue requiriendo ADMIN.

### 5.3 Cambio relacionado (ficha alumno)
- **Ficha completa para alumnos del seed:** `UsuarioService.getUsuarioByIdParaFicha(id)` con `findByIdWithAllRelations` e inicialización de `diasHorariosAsistencia` para mostrar "Horarios de Asistencia".

---

## 6. Alumno inactivo y limpieza en detalle del alumno

**Resumen:** Eliminación del botón obsoleto "Asignar Nueva Rutina" y desactivación de acciones cuando el alumno está INACTIVO (excepto Editar y Eliminar).

### 6.1 Cambios
- **Botón "Asignar Nueva Rutina" eliminado** en la ficha del alumno; la asignación se hace desde la tarjeta "Rutinas asignadas" o "Asignar rutina" en historial.
- **Alumno INACTIVO:** Variable Thymeleaf `alumnoInactivo=${alumno.estadoAlumno == 'INACTIVO'}`. Cuando es INACTIVO:
  - Progreso: botón deshabilitado, tooltip "Activa el alumno para registrar progreso".
  - Tarjeta Rutinas asignadas: texto "Activa el alumno para asignar" (no clicable).
  - Asignar rutina, Ver, Copiar enlace, WhatsApp: deshabilitados.
  - Editar y Eliminar (header): siguen activos.

### 6.2 Estilos
- `.btn-disabled-inactivo`: pointer-events: none, opacity 0.6, cursor not-allowed.
- `.stat-item-link-disabled`: para la tarjeta de rutinas no clicable.

### 6.3 Archivo modificado
- **alumno-detalle.html:** Variable `alumnoInactivo`; eliminado botón "Asignar Nueva Rutina"; condicionales para Progreso, tarjeta rutinas, Asignar rutina, Ver/Copiar/WhatsApp; estilos en `<style>`.

---

## 7. Calendario y presentismo – Cierre (Feb 2026)

**Resumen:** Ajustes finales de lógica de presentismo (tres estados por defecto pendiente, sin ausente automático), sincronización del historial y modal de resumen con el calendario, y apertura del calendario en nueva pestaña. **Calendario y presentismo quedan cerrados por ahora.**

### 7.1 Lógica de tres estados (pendiente por defecto)
- **Por defecto** todos los alumnos hasta el día actual quedan en **pendiente**; el sistema **no asume ausente** si no se cambió el estado (feriados, días sin clase, profesor faltó).
- **Ciclo completo:** en calendario (puntos) y en vista Mis Alumnos (botón Presente) se puede alternar **Pendiente → Presente → Ausente → Pendiente**.
- **API** `POST /calendario/api/marcar-asistencia`: parámetro **`estado`** (PENDIENTE | PRESENTE | AUSENTE). PENDIENTE elimina el registro; PRESENTE/AUSENTE crean o actualizan. Compatibilidad con parámetro antiguo `presente` (boolean).
- **Backend:** Se dejó de llamar a `CalendarioService.registrarAusentesParaSlotsPasados`. `AsistenciaService.eliminarRegistroAsistencia(Usuario, LocalDate)` para dejar estado pendiente.

### 7.2 Sincronización historial y modal con el calendario
- **Problema:** Lo marcado en el calendario (p. ej. presente por excepción el 18/2 a las 10) no se veía en el historial de la ficha ni en el modal “Resumen mensual de asistencias”.
- **Solución:** GET **`/profesor/alumnos/{id}/asistencias`** (JSON) con la lista actualizada de asistencias del alumno. Al **cargar la ficha** se refresca el historial con esa API. Al **abrir el modal** “Consultar asistencias” se vuelve a pedir la lista, se actualiza el historial y se arma el resumen/detalle del modal. Así, lo marcado en el calendario (incluido por excepción) se refleja en historial y resumen.
- **Ajustes posteriores:** el modal **no actualiza** la tabla de historial al abrir (evita borrar la vista del progreso) y agrega **fallback** a los datos ya renderizados si el JSON falla.

### 7.5 Admin: selector de profesor en historial
- **Antes:** el admin debía hacer clic en el nombre para que aparezca el selector.
- **Ahora:** el selector aparece **siempre** en filas con trabajo/observaciones y **guarda automáticamente** al cambiar.

### 7.6 Roles del sistema (DEVELOPER/ADMIN/AYUDANTE)
- **DEVELOPER:** rol super admin con acceso total al sistema y sin aparecer en listas de profesor.
- **ADMIN:** gestiona usuarios del sistema y puede editar su propio perfil.
- **AYUDANTE:** acceso a panel profesor sin panel de usuarios.
- **Panel usuarios:** edición de nombre/correo/rol/contraseña y bloque “Mi perfil”.

### 7.3 Calendario en nueva pestaña
- El botón **“Calendario Semanal”** en el panel del profesor abre el calendario en **nueva pestaña** (`target="_blank"` y `rel="noopener noreferrer"` en `dashboard.html`).

### 7.4 Archivos tocados
- **AsistenciaService:** `eliminarRegistroAsistencia(Usuario, LocalDate)`; `eliminarAsistenciaDeHoy` lo usa.
- **CalendarioController:** sin llamada a `registrarAusentesParaSlotsPasados`; API `marcar-asistencia` con `estado` (y opcional `presente` por compatibilidad).
- **ProfesorController:** GET `/profesor/alumnos/{id}/asistencias` (JSON) para listar asistencias del alumno.
- **semanal-profesor.html:** puntos con `data-estado`; JS ciclo PENDIENTE→PRESENTE→AUSENTE→PENDIENTE.
- **dashboard.html:** botón Presente con ciclo de 3 estados; enlace Calendario Semanal con `target="_blank"`.
- **alumno-detalle.html:** `id="historialAsistenciaBody"`; al cargar página fetch asistencias y rellenar historial; al abrir modal fetch asistencias, actualizar historial y construir resumen/detalle.

---

*Changelog unificado – Febrero 2026. Sustituye a los documentos individuales CHANGELOG_*_FEB2026.md. Calendario y presentismo: cerrado por ahora.*
