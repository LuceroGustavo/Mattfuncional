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
8. [Fase 6 – Alumnos sin login](#8-fase-6--alumnos-sin-login)
9. [Fase 7 – Pizarra / Pantalla de sala – Mejoras](#9-fase-7--pizarra--pantalla-de-sala--mejoras-feb-2026)
10. [Mis Ejercicios: vista lista, actualización de imágenes, redirects y placeholder](#10-mis-ejercicios-vista-lista-actualización-de-imágenes-redirects-y-placeholder-feb-2026)
11. [Fase 8 – Página pública del gimnasio](#11-fase-8--página-pública-del-gimnasio-feb-2026)
12. [Reparación calendario – slot_config duplicados y herramientas servidor](#12-reparación-calendario--slot_config-duplicados-y-herramientas-servidor-feb-2026)
13. [Página Planes y administración pública](#13-página-planes-y-administración-pública-feb-2026)
14. [Formulario de consulta – Email opcional y script BD](#14-formulario-de-consulta--email-opcional-y-script-bd-feb-2026)
15. [Página Planes y formulario – Cierre de desarrollo HTML](#15-página-planes-y-formulario--cierre-de-desarrollo-html-feb-2026)
16. [Peso en hoja pública de rutina y acción Eliminar en Asignaciones](#16-peso-en-hoja-pública-de-rutina-y-acción-eliminar-en-asignaciones-feb-2026)
17. [Botón WhatsApp en detalle del alumno](#17-botón-whatsapp-en-detalle-del-alumno-feb-2026)
18. [Mejoras AYUDA_MEMORIA – Panel profesor y rutinas](#18-mejoras-ayuda_memoria--panel-profesor-y-rutinas-feb-2026)
19. [Vista de serie y rutinas – Formato unificado y escritorio](#19-vista-de-serie-y-rutinas--formato-unificado-y-escritorio-feb-2026)
20. [Eliminar alumno – Fix FK asistencia y referencias](#20-eliminar-alumno--fix-fk-asistencia-y-referencias-mar-2026)
21. [Ejercicios: formularios, modal Ver ejercicio, permisos y hoja rutina](#21-ejercicios-formularios-modal-ver-ejercicio-permisos-y-hoja-rutina-mar-2026)
22. [Backup ZIP: nombres originales de imágenes y restauración de series](#22-backup-zip-nombres-originales-de-imágenes-y-restauración-de-series-mar-2026)
23. [Fix: botones Guardar/Cancelar en formulario editar ejercicio](#23-fix-botones-guardarcancelar-en-formulario-editar-ejercicio-feb-2026)

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

## 8. Fase 6 – Alumnos sin login

**Resumen:** El alumno es solo ficha (física + online); nunca tiene usuario ni contraseña. El profesor envía rutinas por WhatsApp. Se refuerza la seguridad para que los alumnos nunca puedan autenticarse.

### 8.1 Cambios técnicos
- **UsuarioRepository:** Nuevo método `findByCorreoParaLogin(String correo)` que solo devuelve usuarios con rol ADMIN, AYUDANTE o DEVELOPER. Excluye ALUMNO.
- **SecurityConfig / UserDetailsService:** Usa `findByCorreoParaLogin` en lugar de `findByCorreo`. Así, aunque un alumno tuviera contraseña por error, nunca se cargaría para autenticación.
- **UsuarioService.crearAlumno:** Ya no guardaba contraseña (queda null); sin cambios adicionales.

### 8.2 Archivos modificados
- `UsuarioRepository.java`: método `findByCorreoParaLogin`.
- `SecurityConfig.java`: `loadUserByUsername` usa `findByCorreoParaLogin`.

---

## 9. Fase 7 – Pizarra / Pantalla de sala – Mejoras (Feb 2026)

**Resumen:** Pulido de la Fase 7 para que los cambios del profesor se reflejen en la vista TV casi al instante, persistencia correcta de títulos y peso/reps, agregar/quitar columnas desde el editor, y correcciones de login y arranque.

### 9.1 Peso y repeticiones en la TV
- Las peticiones POST del editor no enviaban CSRF; Spring rechazaba y no se guardaban peso/reps.
- Metas `_csrf` y `_csrf_header` en `pizarra-editor.html`; función `postHeaders()` en todos los fetch POST.

### 9.2 Títulos que no se borran al agregar ejercicio
- Al soltar un ejercicio se hacía `location.reload()` y se perdían los títulos no guardados.
- Se deja de recargar: se crea la tarjeta en el DOM con los datos del ejercicio arrastrado y se enlazan eventos.

### 9.3 Auto-guardado de títulos y nombre
- Guardado al salir del campo (blur) y con debounce 500 ms al escribir en nombre de pizarra y títulos de columnas.
- Backend: `columnaRepository.saveAll(cols)` tras actualizar títulos; controlador convierte `titulos` del JSON a `List<String>` de forma segura.

### 9.4 Vista TV – Actualización manual (sin polling)
- **Antes:** Polling cada 2,5 s (consumía recursos con la pantalla abierta horas).
- **Ahora:** Botón **"Actualizar"** en la barra superior (logo + título + botón). Una sola petición al hacer clic; sin peticiones en segundo plano.

### 9.5 Agregar y quitar columnas
- **Agregar:** Botón "Agregar columna" (máx. 6). POST `/profesor/pizarra/agregar-columna`.
- **Quitar:** Botón ✕ en cada columna (mín. 1). POST `/profesor/pizarra/quitar-columna`. Elimina columna e items, reordena y actualiza `cantidadColumnas`.

### 9.6 Corrección login y arranque
- **Login:** `PortalControlador` devolvía `"login.html"`; Thymeleaf requiere `"login"`. Corregido también `"index"` y `"demo"`.
- **PizarraService:** `import com.mattfuncional.entidades.*` sustituido por imports explícitos para evitar `ClassNotFoundException` con DevTools RestartClassLoader.

### 9.7 Token de sala legible (tv + 6 dígitos)
- **Antes:** La URL de la sala usaba un token alfanumérico largo (ej. `http://localhost:8080/sala/lexmGIFeyzCX`), poco legible para escribir o dictar.
- **Ahora:** El token de las **nuevas** pizarras tiene formato **"tv" + 6 dígitos** (ej. `tv45677`, `tv123456`). La URL queda tipo `http://localhost:8080/sala/tv45677`.
- **Implementación:** En `PizarraService.generarTokenUnico()` se genera `"tv" + String.format("%06d", num)` con `num` aleatorio 0–999999; se comprueba unicidad con `pizarraRepository.existsByToken(token)` y se repite hasta obtener un token no usado. Se eliminaron la constante `TOKEN_CHARS` y el método auxiliar `generarToken(int length)`.
- **Compatibilidad:** Las pizarras ya existentes conservan su token antiguo; solo las creadas a partir de este cambio usan el formato `tvXXXXXX`. El campo `token` en la entidad `Pizarra` sigue siendo único y `length = 32` (suficiente para 8 caracteres).

### 9.8 Archivos modificados
- `pizarra-editor.html`: CSRF, postHeaders, agregar tarjeta en DOM sin reload, blur + debounce para títulos, botones agregar/quitar columna.
- `sala.html`: logo Mattfuncional arriba a la izquierda, botón "Actualizar" en la barra (sin polling); `actualizarDesdeAPI()` solo al clic.
- `PizarraService.java`: saveAll(cols), agregarColumna, quitarColumna, imports explícitos; **generarTokenUnico()** ahora genera token "tv" + 6 dígitos (eliminados TOKEN_CHARS y generarToken(length)).
- `PizarraController.java`: actualizar-basico con titulos como List<String>, agregar-columna, quitar-columna.
- `PortalControlador.java`: return "login", "index", "demo" (sin .html).

### 9.9 Vista TV – Tarjetas: nombre, peso, reps/tiempo y estilo (Feb 2026)
- **Nombre:** Recuadro con puntas redondeadas y texto justificado a la derecha.
- **Orden:** Nombre → Peso (solo "K", ej. 12K) → Reps/tiempo (una letra: 12m, 15s, 25r).
- **Estilo:** Peso y reps/tiempo en verde con fondo oscuro (sombreado negro); contenedores compactos (`width: fit-content`, padding reducido) para ~3 caracteres.
- **Archivo:** `sala.html` (estilos, Thymeleaf y JS de actualización desde API). Documentación: FASE_7_PANTALLA_DE_SALA.md sección 15.

---

## 10. Mis Ejercicios: vista lista, actualización de imágenes, redirects y placeholder (Feb 2026)

**Resumen:** La vista Mis Ejercicios no mostraba la tabla de ejercicios (respuesta incompleta). Se corrigió la carga de `grupos` en transacción, se añadió la acción “Actualizar imágenes desde carpeta” mediante enlace GET (sin form en esa zona) y se unificaron redirects del ExerciseController a Mis Ejercicios. Imágenes no encontradas redirigen al placeholder.

### 10.1 Vista lista visible
- **Problema:** Tabla de ejercicios no se renderizaba; consola: `ERR_INCOMPLETE_CHUNKED_ENCODING`. Causa probable: `LazyInitializationException` al acceder a `ejercicio.grupos` en Thymeleaf.
- **Solución:** `ExerciseService.findEjerciciosDisponiblesParaProfesorWithImages` con `@Transactional(readOnly = true)` e inicialización de `grupos` (`e.getGrupos().size()`) dentro de la transacción. Template con condiciones null-safe: `ejercicios == null or ejercicios.empty`.
- **Archivos:** `ExerciseService.java`, `ExerciseRepository.java`, `profesor/ejercicios-lista.html`.

### 10.2 Actualización de imágenes desde carpeta
- **Objetivo:** Profesor coloca en `uploads/ejercicios/` los archivos 1.webp … 60.webp y actualiza en masa la relación ejercicio–imagen.
- **Implementación:** Tarjeta en Mis Ejercicios con enlace a `GET /profesor/mis-ejercicios/actualizar-imagenes?confirm=1` (sin formulario ni _csrf en esa zona). Nuevo método GET en ProfesorController con `confirm=1`; POST se mantiene. Mensaje de éxito con `imagenesActualizadas`.
- **Archivos:** `ProfesorController.java`, `profesor/ejercicios-lista.html`.

### 10.3 ExerciseController: redirects a Mis Ejercicios
- ABM de ejercicios (`abm-ejercicios.html`) no se usa; todo se hace desde Mis Ejercicios. `GET /exercise/editar` y `GET /ejercicios/abm` redirigen a `/profesor/mis-ejercicios`. Redirects tras crear, modificar, eliminar y cambiar imagen unificados a `/profesor/mis-ejercicios`.
- **Archivos:** `ExerciseController.java`.

### 10.4 ImagenController: redirect a placeholder
- Si la imagen no existe o falla la lectura: 302 a `/img/not_imagen.png` en lugar de 404/500 para evitar errores en consola del navegador.
- **Archivos:** `ImagenController.java`.

### 10.5 Archivos tocados
- `ExerciseController.java`, `ExerciseService.java`, `ExerciseRepository.java`, `ProfesorController.java`, `ImagenController.java`, `profesor/ejercicios-lista.html`.

---

## 11. Fase 8 – Página pública del gimnasio (Feb 2026)

**Resumen:** La página de inicio (`/`) se reemplazó por una landing pública estilo RedFit. Quien entra a la app ve primero esta página; el acceso al panel de gestión es por un ícono “Iniciar sesión” en la barra superior que lleva a `/login`.

### 11.1 Implementación
- **Template:** `index-publica.html` (nueva). Estructura: hero con carrusel, navbar flotante, sección “Rasgos que nos caracterizan”, contacto, footer, botón flotante WhatsApp.
- **Hero/carrusel:** Primer slide con video de fondo (`/img/publica/video matt.mp4`) y poster `fondo matt.jpeg`; slides 2–4 con imágenes `1.png`, `2.png`, `3.png`. Títulos y listas con checkmarks verdes; botón “Conocé más” / “Contacto”.
- **Navbar:** Logo Matt + “MATTFUNCIONAL”, enlaces Inicio, Servicios, Contacto. **Ícono “Iniciar sesión”** (y texto en desktop) que lleva a `/login`. Al hacer scroll, la barra pasa a fija con fondo oscuro (`.small-header`). Menú móvil (offcanvas) con las mismas opciones.
- **Rasgos que nos caracterizan:** Fondo oscuro (#333), título verde, 3 columnas con imágenes `4.jpg`, `5.jpg`, `6.jpg` y textos (entrenamiento personalizado, multi-horarios, presencial y virtual).
- **Contacto y footer:** Bloque contacto con enlaces placeholder a WhatsApp e Instagram; footer con nombre y año; **botón flotante WhatsApp** (esquina inferior derecha).
- **Estilos:** `publica.css` (verde #85CB46, Montserrat, carousel-caption, overlay en hero, navbar, wapp).

### 11.2 Rutas y seguridad
- **GET `/`:** Devuelve `index-publica` (página de inicio actual).
- **GET `/publica`:** Misma vista (alternativa por si hay enlaces guardados).
- **SecurityConfig:** `/publica` y `/` ya estaban en `permitAll`; la raíz ahora sirve la landing pública.
- **PortalControlador:** `index()` para `/` y `indexPublica()` para `/publica` delegan al mismo método que devuelve `"index-publica"`.

### 11.3 Assets
- Imágenes y video en `src/main/resources/static/img/publica/`: `fondo matt.jpeg`, `video matt.mp4`, `1.png`–`6.jpg`. Archivo `LEEME.txt` en esa carpeta indica qué copiar. WhatsApp e Instagram son placeholders (número y URL a reemplazar cuando el cliente los pase).

### 11.4 Archivos creados / modificados
- **Creados:** `templates/index-publica.html`, `static/css/publica.css`, `static/img/publica/LEEME.txt`.
- **Modificados:** `PortalControlador.java` (raíz → index-publica, ruta `/publica`), `SecurityConfig.java` (permitAll `/publica`). La plantilla antigua `index.html` se mantiene en el proyecto pero ya no se usa como inicio.

### 11.5 Ajustes y mejoras página pública (videos, navbar, móvil)

- **Videos y assets:** Poster del hero = `fondo-inicial.png`. Video escritorio = `video-inicial.mp4` (recomendado 2:1, ej. 1920×960). Video móvil = `video-movil.mp4` (9:16, ej. 1080×1920); se muestra con `@media (max-width: 767px)`. Logo del navbar = `/img/logo.png` en contenedor circular (`.logo-nav-circle`).
- **Hero sin scroll:** Carrusel con `max-height: 100vh`; video/imágenes con `position: absolute` + `object-fit: cover` para que no desborden la pantalla.
- **Retraso del módulo hero:** Título, lista, botón e indicadores del primer slide se ocultan 5 s al cargar (clase `hero-delay-pending` en `body`), luego aparecen con fade-in para dejar ver la animación del video.
- **Navbar:** Enlaces en blanco (`.nav-header .nav-link` con `color: #ffffff !important`). Eliminado `justify-content-center` del header; en móvil `justify-content: flex-start !important` para que logo + texto queden a la izquierda; texto "MATTFUNCIONAL" visible también en móvil (clase `nav-brand-text`, sin `d-none d-md-inline`), tamaño 0.95rem en móvil.
- **Carrusel:** `data-bs-interval="6000"` (6 s entre slides). Transición entre slides: `opacity 1.5s` en `.carousel-fade .carousel-item`.
- **Documentación assets:** `img/publica/LEEME.txt` actualizado con nombres `fondo-inicial.png`, `video-inicial.mp4`, `video-movil.mp4`, `logo.png` y relaciones de aspecto recomendadas.

---

## 12. Reparación calendario – slot_config duplicados y herramientas servidor (Feb 2026)

**Resumen:** Error "Query did not return a unique result: 2 results were returned" en el calendario semanal (`/calendario/semanal/profesor/1`). Causa: registros duplicados en la tabla `slot_config` (mismo dia + hora_inicio). Se corrigió el código y se añadieron opciones al menú del servidor.

### 12.1 Corrección del error del calendario

- **Problema:** `SlotConfigRepository.findByDiaAndHoraInicio` esperaba un único resultado; con duplicados en BD lanzaba `NonUniqueResultException`.
- **Solución lectura:** Cambio a `findFirstByDiaAndHoraInicio` para devolver el primer resultado aunque existan duplicados.
- **Solución escritura (robusta):** En `SlotConfigService.setCapacidadMaxima`: se usa `findAllByDiaAndHoraInicio`; si hay varios registros, se actualiza el primero y se eliminan los duplicados. Así se evita crear nuevos duplicados y se limpian los existentes al guardar.

### 12.2 Menú del servidor

El menú `./mattfuncional` tiene opciones 1–11 (parar, actualizar, compilar, iniciar, despliegue completo, estado, logs, reiniciar, información, espacio en disco, salir). *Nota (Feb 2026): No se modificará por ahora el menú del servidor; se descartó la instalación de Workbench y scripts adicionales.*

### 12.3 Archivos modificados / creados

- **SlotConfigRepository.java:** `findFirstByDiaAndHoraInicio`, `findAllByDiaAndHoraInicio`.
- **SlotConfigService.java:** `setCapacidadMaxima` con lógica anti-duplicados y `@Transactional`.
- **scripts/servidor/limpiar_duplicados_slot_config.sql:** Script SQL para eliminar duplicados en `slot_config` (uso manual si hace falta).

### 12.4 Para commit y continuar desarrollando

```
git add -A
git commit -m "Reparación calendario: slot_config duplicados"
git push origin main
```

Luego en el servidor: opción 5 (Despliegue completo) para aplicar los cambios.

---

## 13. Página Planes y administración pública (Feb 2026)

**Resumen:** Nueva página `/planes` con cards de planes, servicios, días/horarios y formulario de consulta. Panel de administración en `/profesor/pagina-publica` para que el profesor edite precios, planes y datos de contacto sin intervención de developer. Imagen de fondo en header de Planes.

### 13.1 Backend

- **Entidad `PlanPublico`:** nombre, descripcion, precio, vecesPorSemana, orden, activo.
- **Entidad `ConfiguracionPaginaPublica`:** clave-valor (whatsapp, instagram, direccion, dias_horarios, telefono).
- **Repositorios y servicios:** PlanPublicoService, ConfiguracionPaginaPublicaService.
- **Seed inicial:** 4 planes (1x, 2x, 3x, opción libre) y config con placeholders. Se ejecuta en DataInitializer.

### 13.2 Página pública Planes

- **Navbar:** "Servicios" reemplazado por "Planes" (index y planes).
- **Ruta `/planes`:** Cards de planes desde BD, sección servicios, días/horarios desde config, formulario de consulta (POST `/public/consulta`).
- **Imagen de fondo:** `contacto 2 .png` detrás del navbar (estilo RedFit, larga y poco alta).
- **SecurityConfig:** `/planes` y `/public/**` permitAll.

### 13.3 Panel de administración

- **Enlace** en `/profesor/usuarios-sistema`: "Administrar página pública".
- **Ruta `/profesor/pagina-publica`:** Editar configuración (WhatsApp, Instagram, dirección, días/horarios) y planes (crear, editar, eliminar).
- **Acceso:** ADMIN y DEVELOPER.

### 13.4 Archivos creados / modificados

- **Creados:** PlanPublico.java, ConfiguracionPaginaPublica.java, PlanPublicoRepository, ConfiguracionPaginaPublicaRepository, PlanPublicoService, ConfiguracionPaginaPublicaService, planes-publica.html, pagina-publica-admin.html, PaginaPublicaAdminController, PublicoController.
- **Modificados:** index-publica.html (navbar Planes, enlaces), DataInitializer (seed planes y config), PortalControlador (GET /planes), SecurityConfig, usuarios-sistema.html (card Administrar página pública), publica.css (estilos planes, imagen fondo).
- **Imagen:** `img/publica/contacto 2 .png` usada como fondo del header en Planes.

### 13.5 Nota

La configuración editada en el panel afecta **solo la página Planes** por ahora. El index (footer, contacto, botón flotante) sigue con valores hardcodeados. Opcional: integrar config en index en una fase posterior.

---

## 14. Formulario de consulta – Email opcional y script BD (Feb 2026)

**Resumen:** El formulario de contacto en `/planes` permitía solo email o solo teléfono según validación, pero la columna `email` en la tabla `consulta` era NOT NULL. Al enviar con solo teléfono (sin email) fallaba con `Column 'email' cannot be null`. Se corrigió con un script SQL y se documentaron pendientes de servidor.

### 14.1 Mejoras realizadas

- **Entidad `Consulta`:** `email` ya estaba definido como nullable (`@Column(length = 150)` sin `nullable = false`). La tabla en BD se había creado antes con NOT NULL.
- **Script SQL:** `scripts/servidor/alter_consulta_email_nullable.sql` para permitir `email` NULL en la tabla `consulta`.
- **Validación:** El formulario exige al menos uno de los dos (email o teléfono). Si el usuario completa solo teléfono, se guarda con `email = null`.
- **Ejecución local:** El script se ejecutó contra la BD local (MySQL) para corregir el error de inmediato.

### 14.2 Archivos creados

- **scripts/servidor/alter_consulta_email_nullable.sql:** `ALTER TABLE consulta MODIFY COLUMN email VARCHAR(150) NULL;`

### 14.3 Pendientes documentados (ver PLAN_DE_DESARROLLO_UNIFICADO y ESTADO-PLANES-Y-PENDIENTES)

- **Actualizar script en servidor:** Ejecutar `alter_consulta_email_nullable.sql` en la base de datos del servidor (Donweb) para que el formulario funcione con solo teléfono en producción.
- **Script borrar base entera:** Crear un script que elimine la base de datos completa para que, al reiniciar la app con `ddl-auto=create` o similar, Hibernate la recree desde cero. Útil para entornos de desarrollo o reset completo.

---

## 15. Página Planes y formulario – Cierre de desarrollo HTML (Feb 2026)

**Resumen:** El desarrollo del HTML de la página Planes (`/planes`) y del formulario de consulta se considera **terminado**. Incluye mensajes de validación en rojo (sin alert), mensaje de éxito con flash (no persiste al refrescar), días y horarios multilínea con alineación correcta.

### 15.1 Mejoras documentadas

| Mejora | Descripción |
|--------|-------------|
| **Validación sin alert** | Los mensajes de validación del formulario (email/teléfono requerido, formato email, dígitos teléfono) se muestran en un bloque rojo (`alert-publica-error`) igual que los errores del servidor, en lugar de `alert()` nativo. |
| **Mensaje de éxito con flash** | El mensaje verde "Gracias por tu consulta" usa `addFlashAttribute("consultaOk")` en lugar de `?ok=consulta` en la URL; así no persiste al refrescar (F5). |
| **Días y horarios multilínea** | En el panel de administración, el campo "Días y horarios" pasó de input a **textarea** (4 filas); cada línea se guarda y se muestra debajo de la otra en la página pública. |
| **Alineación días y horarios** | Los horarios en la página pública se muestran en lista (`<ul><li>`) con `padding-left: 1.75rem` para alinearse al margen izquierdo de los ítems de Servicios (Entrenamiento personalizado, etc.). |

### 15.2 Archivos modificados

- **planes-publica.html:** Div de validación cliente, mensaje éxito por `consultaOk`, días y horarios como lista con `diasHorariosLineas`.
- **pagina-publica-admin.html:** Textarea para días/horarios, texto de ayuda.
- **PublicoController.java:** `addFlashAttribute("consultaOk")` en lugar de `?ok=consulta`.
- **PortalControlador.java:** `diasHorariosLineas` (lista de líneas) para la vista pública.
- **publica.css:** `.dias-horarios-lista`, `.dias-horarios-multilinea`.

### 15.3 Estado

**Desarrollo HTML de página Planes y formulario de consulta: TERMINADO.** Las mejoras de contenido (textos, imágenes, datos de contacto) se gestionan desde el panel de administración sin cambios de código.

---

## 16. Peso en hoja pública de rutina y acción Eliminar en Asignaciones (Feb 2026)

**Resumen:** (1) Al agregar una serie plantilla a una rutina asignada ahora se copian **peso** y **orden** de cada ejercicio, por lo que la hoja pública (`/rutinas/hoja/{token}`) muestra correctamente el peso (ej. "25 kg"). (2) En la pestaña **Asignaciones** del panel del profesor se añade el botón **Eliminar rutina** en la tabla "Rutinas Asignadas", con confirmación y redirección a la misma pestaña.

### 16.1 Peso en hoja pública

- **Problema:** En la hoja pública de la rutina los ejercicios mostraban "Sin peso" aunque la serie tuviera peso; en Ver serie y Modificar Serie sí se veía.
- **Causa:** En `RutinaService.agregarSerieARutina` al copiar los ejercicios de la serie plantilla solo se copiaban `valor`, `unidad` y `exercise`; no `peso` ni `orden`.
- **Solución:** En el bucle que crea cada `SerieEjercicio` se añade `setPeso(seOriginal.getPeso())` y se mantiene el orden (lista ordenada por `orden`, `setOrden(i)`). Archivo: `RutinaService.java`.

### 16.2 Acción Eliminar en Asignaciones

- **Vista:** En `profesor/dashboard.html`, columna Acciones de la tabla Rutinas Asignadas: botón rojo "Eliminar" con icono papelera, enlace a `/rutinas/eliminar/{id}?tab=asignaciones`, con `confirm()` antes de enviar.
- **Controlador:** `RutinaControlador.eliminarRutina` acepta `@RequestParam(required = false) String tab`. Si `tab=asignaciones`, el redirect tras eliminar es a `dashboard?tab=asignaciones`; si no, a `tab=rutinas`.

### 16.3 Archivos

| Archivo | Cambios |
|--------|--------|
| `RutinaService.java` | En `agregarSerieARutina`, copiar `peso` y `orden` al crear cada `SerieEjercicio` desde la plantilla. |
| `profesor/dashboard.html` | Botón "Eliminar" en Acciones de la tabla Rutinas Asignadas. |
| `RutinaControlador.java` | Parámetro `tab` en `eliminarRutina`; redirect según `tab` (asignaciones / rutinas). |

---

## 17. Botón WhatsApp en detalle del alumno (Feb 2026)

**Resumen:** En el detalle del alumno, en "Rutinas del Alumno", el botón **WhatsApp** por cada rutina abre WhatsApp con el mensaje "Rutina: [enlace a la hoja]". Si el alumno tiene celular guardado, el enlace usa `wa.me/{número}?text=...` para pre-seleccionar el contacto; si no, abre WhatsApp con el mensaje listo para elegir destinatario. Se documenta el comportamiento y se mejoran la plantilla (evitar `data-phone="null"`, título del botón según haya o no teléfono).

### 17.1 Comportamiento

- **Ubicación:** Columna Acciones de la tabla "Rutinas del Alumno" en `/profesor/alumnos/{id}` (solo si el alumno no está inactivo).
- **Al hacer clic:** Nueva pestaña con `https://wa.me/{teléfono}?text=Rutina:%20{url}`; si no hay teléfono: `https://wa.me/?text=...`. El número se normaliza a solo dígitos para `wa.me`.

### 17.2 Cambios en la plantilla

- **data-phone:** `data-phone=${alumno.celular != null ? alumno.celular : ''}` para no enviar la cadena `"null"` cuando no hay celular.
- **title:** Con celular: "Abrir WhatsApp para enviar la rutina al alumno". Sin celular: "Abrir WhatsApp (agrega el celular del alumno para pre-seleccionar el contacto)".

### 17.3 Archivos

| Archivo | Cambios |
|--------|--------|
| `profesor/alumno-detalle.html` | `data-phone` con fallback a vacío cuando `alumno.celular` es null; atributo `title` condicional. |

---

## 18. Mejoras AYUDA_MEMORIA – Panel profesor y rutinas (Feb 2026)

**Resumen:** Implementación completa de los 8 ítems de la lista "Para mañana" del AYUDA_MEMORIA: correo opcional en alumno, inactivar rutinas al dar de baja alumno, mejoras en vistas de rutinas (iconos, textos abreviados), volver al origen tras guardar rutina, quitar asistencia del modal de progreso, botón Crear alumno, mejoras en lista de asignaciones y formulario de modificar rutina.

### 18.1 Cambios implementados

| Ítem | Descripción | Archivos principales |
|------|-------------|----------------------|
| 1 | **Correo opcional** en formulario crear/editar alumno | `Usuario.java`, `nuevoalumno.html`, `ProfesorController.java`, `UsuarioRepository.java`, `alumno-detalle.html`, `alter_usuario_correo_nullable.sql` |
| 2 | **Alumno inactivo** → inactivar todas las rutinas asignadas automáticamente | `UsuarioService.java` (inyección `RutinaService`, llamada a `inactivarTodasRutinasDelAlumno`) |
| 3 | **Detalle alumno – Rutinas:** textos largos → iconos; reseña con texto truncado; acciones centradas | `alumno-detalle.html` |
| 4 | **Volver al origen** tras guardar rutina (detalle alumno o panel rutinas/asignaciones) | `RutinaControlador.java`, `editarRutina.html`, `alumno-detalle.html`, `dashboard.html` |
| 5 | **Modal de progreso:** quitar checkbox "Estuvo presente" (asistencia se gestiona en panel/calendario) | `alumno-detalle.html` |
| 6 | **Formulario modificar rutina:** nuevo layout (Series a seleccionar izq, Series seleccionadas der, Detalles abajo); 2 tarjetas por fila; estilos mejorados | `editarRutina.html` |
| 7 | **Botón "Crear alumno"** en título de vista Mis Alumnos | `dashboard.html` |
| 8 | **Lista rutinas asignadas:** textos abreviados, estado con iconos, acciones centradas con solo iconos | `dashboard.html` |

### 18.2 Detalles técnicos

- **Tarjetas dashboard:** clic en tarjeta (no en +) activa tab y hace scroll a la sección de listas.
- **Correo alumno:** `@NotBlank` eliminado de `Usuario.correo`; validación de duplicados solo cuando correo no es null; query duplicados excluye NULL.
- **Rutina editar:** parámetros `alumnoId` y `returnTab` en GET/POST; hidden inputs en formulario.
- **Modal progreso:** meta tags no-cache en `alumno-detalle.html` para evitar caché.

### 18.3 Archivos modificados

`Usuario.java`, `ProfesorController.java`, `UsuarioService.java`, `UsuarioRepository.java`, `RutinaControlador.java`, `profesor/nuevoalumno.html`, `profesor/alumno-detalle.html`, `profesor/dashboard.html`, `rutinas/editarRutina.html`, `scripts/servidor/alter_usuario_correo_nullable.sql`.

---

## 19. Vista de serie y rutinas – Formato unificado y escritorio (Feb 2026)

**Resumen:** La vista de serie (`/series/ver/{id}`) y la vista de rutina no asignada (`/profesor/rutinas/ver/{id}`) se unifican para usar el mismo formato visual (oscuro, overlays de peso/reps). Ambas son **no responsive** (solo escritorio). La rutina asignada (`/rutinas/hoja/{token}`) sigue siendo responsive. Se achican los datos de peso y repeticiones en vistas de escritorio.

### 19.1 Cambios en vista de serie

- **Antes:** Fondo verde claro, tarjetas con cajas de repeticiones/peso en la parte inferior.
- **Ahora:** Mismo formato que rutinas (captura3): fondo oscuro (#0d1117), contenedor con borde naranja (#f97316), tarjetas con título negro, imagen blanca y overlays de peso/reps en verde.
- **No responsive:** Grid fijo de 3 columnas; `min-width: 800px` para escritorio.
- **Peso y repeticiones:** Tamaño reducido (`font-size: 0.85rem`, `padding: 0.25rem 0.45rem`).

### 19.2 Cambios en vista de rutina

- **Flag `esVistaEscritorio`:** Se pasa en el modelo para distinguir:
  - `true` en `/profesor/rutinas/ver/{id}` (rutina no asignada) → vista fija escritorio.
  - `false` en `/rutinas/hoja/{token}` (rutina asignada) → vista responsive.
- **Vista escritorio:** Grid fijo de 3 columnas; peso/reps más pequeños.
- **Vista asignada:** Responsive sin cambios (para móvil del alumno).

### 19.3 Archivos modificados

| Archivo | Cambios |
|---------|---------|
| `series/verSerie.html` | Rediseño completo al formato oscuro; grid fijo; overlays peso/reps achicados. |
| `rutinas/verRutina.html` | Estilos condicionales según `esVistaEscritorio`; clase `vista-escritorio` en body. |
| `ProfesorController.java` | `model.addAttribute("esVistaEscritorio", true)` en verRutinaPrivada. |
| `RutinaControlador.java` | `model.addAttribute("esVistaEscritorio", false)` en verHojaRutina. |

---

## 20. Eliminar alumno – Fix FK asistencia y referencias (Mar 2026)

**Resumen:** Al eliminar un alumno desde `/profesor/alumnos/eliminar/{id}` fallaba con error 500 por violación de FK: la tabla `asistencia` (y otras) referencian `usuario.id`. Se corrige eliminando o desasignando antes todas las referencias al usuario.

### Cambios técnicos
- **UsuarioService.eliminarUsuario(id):** Antes de `usuarioRepository.deleteById(id)` se ejecuta en orden: (1) eliminar asistencias del alumno, (2) anular "registrado por" en asistencias, (3) eliminar mediciones físicas, (4) eliminar excepciones de calendario, (5) **eliminar** todas las rutinas asignadas al alumno con `rutinaService.eliminarRutina(r.getId())` (ya no se desasignan; así no quedan rutinas huérfanas en "Rutinas asignadas"), (6) eliminar el usuario.
- **Repositorios:** Añadidos `void deleteByUsuario_Id(Long usuarioId)` en `AsistenciaRepository`, `MedicionFisicaRepository` y `CalendarioExcepcionRepository`.
- **UsuarioService:** Inyección de `MedicionFisicaRepository`, `CalendarioExcepcionRepository` y `RutinaRepository`; imports de `Rutina` y repositorios.

### Archivos modificados
| Archivo | Cambios |
|---------|---------|
| `UsuarioService.java` | Lógica completa en `eliminarUsuario`; nuevos @Autowired y método @Transactional. |
| `AsistenciaRepository.java` | `deleteByUsuario_Id(Long usuarioId)`. |
| `MedicionFisicaRepository.java` | `deleteByUsuario_Id(Long usuarioId)`. |
| `CalendarioExcepcionRepository.java` | `deleteByUsuario_Id(Long usuarioId)`. |

---

## 21. Ejercicios: formularios, modal Ver ejercicio, permisos y hoja rutina (Mar 2026)

**Resumen:** Mejoras de vista y permisos en el módulo ejercicios: formularios crear/editar alineados con series y rutinas (título compacto, ancho completo, cabecera gradiente), modal "Ver ejercicio" con estilo unificado, DEVELOPER puede editar predeterminados, mensaje visible para sin_permisos_editar, y en la hoja de rutina el botón "Ver video" solo si hay URL.

### Cambios
- **Formularios crear y editar ejercicio:** Título en una línea (icono + "Nuevo/Editar Ejercicio" + subtítulo + "Volver a Ejercicios"); `container-fluid` max-width 1200px; bloque `.form-section` con `.card-header-ejercicio` en gradiente #764ba2 → #667eea "Datos del ejercicio"; fondo de página en gradiente suave; mismo criterio que crear serie.
- **Modal Ver ejercicio (Mis Ejercicios):** Cabecera con gradiente y "Ver ejercicio" + botón X; cuerpo con imagen en `.modal-ejercicio-img-wrap`, badge grupos en lavanda (#e1bee7, #5e35b1), nombre y descripción; clases CSS `.modal-ejercicio-overlay`, `.modal-ejercicio-box`, etc.
- **Permisos:** `Exercise.puedeSerEditadoPor` incluye rol DEVELOPER (además de ADMIN) para editar todo; en `ejercicios-lista.html` mensaje para `param.error=sin_permisos_editar`.
- **Hoja de rutina (`verRutina.html`):** `data-video-url` solo se envía cuando hay URL; en JS, botón "Ver video" solo si `hasVideo` (URL válida, no "null" ni vacío).

### Archivos
`formulario-ejercicio-profesor.html`, `formulario-modificar-ejercicio-profesor.html`, `profesor/ejercicios-lista.html`, `rutinas/verRutina.html`, `Exercise.java`.

---

## 22. Backup ZIP: nombres originales de imágenes y restauración de series (Mar 2026)

**Resumen:** Correcciones en el módulo de backup ZIP: export/import usan nombres originales de imágenes (1.webp, 2.webp) en lugar de ejercicio_0, ejercicio_1; rutinas y series se asignan al profesor logueado; eliminación de ejercicios borra también el archivo físico.

### Cambios
- **Export:** Usa `rutaArchivo` de la imagen en BD para los archivos en el ZIP (1.webp, 2.webp). Fallback a `ejercicio_N.ext` si no hay nombre.
- **Import:** `ImagenServicio.guardarParaRestore(byte[], rutaEnZip)` extrae el nombre del ZIP y guarda con ese nombre. Preserva gif/webp sin optimizar.
- **Rutinas y series:** Se asignan al profesor del usuario que importa. `importarDesdeZip` recibe `Profesor profesorParaRestore` desde `AdminPanelController`.
- **ExerciseService.deleteExercise():** Elimina el archivo físico de la imagen.
- **Fix sintaxis:** Eliminado código duplicado al final de `ExerciseZipBackupService.java`.

### Archivos
`ExerciseZipBackupService.java`, `ImagenServicio.java`, `AdminPanelController.java`, `ExerciseService.java`, `RutinaRepository.java`, `Documentacion/PLAN_BACKUP_Y_EXPORTACION.md`.

### Pendiente testear
- Exportar e importar con "Suplantar" verificando nombres 1.webp, 2.webp.
- Restauración de series visible en panel del profesor.
- Importar backup antiguo con ejercicio_0.jpg.

---

## 23. Fix: botones Guardar/Cancelar en formulario editar ejercicio (Feb 2026)

**Resumen:** En la página de edición de ejercicio (`/profesor/mis-ejercicios/editar/{id}`) los botones "Guardar Cambios" y "Cancelar" no se veían (formulario más largo que el de crear; en algunos casos la respuesta podía cortarse por ERR_INCOMPLETE_CHUNKED_ENCODING). Se ajustó la plantilla para que los botones sean siempre visibles.

### Cambios
- **Plantilla `formulario-modificar-ejercicio.html`:**
  - Se eliminó la barra de botones fija al pie (`.form-actions-fixed`), que podía quedar oculta o dar problemas de layout.
  - Se eliminó la barra duplicada fuera del card (arriba del formulario).
  - Se mantuvieron los botones al final del formulario (`.form-actions-bottom`) en flujo normal.
  - Se añadió una **barra de botones al inicio del formulario** (justo debajo del mensaje de error), visible sin scroll, para que Cancelar y Guardar Cambios se vean siempre aunque el contenido sea largo o la respuesta se corte.
- **Referencia:** Se comparó con la versión del formulario en el commit `487172658aedfd15419f3f2a93b66cdf996a0b86` (`formulario-modificar-ejercicio-profesor.html`), donde los botones estaban solo al final en un layout más corto.

### Archivos
`src/main/resources/templates/ejercicios/formulario-modificar-ejercicio.html`, `Documentacion/CHANGELOG_UNIFICADO_FEB2026.md`, `Documentacion/PLANTILLAS_EJERCICIOS_Y_PROFESOR.md`.

---

*Changelog unificado – Febrero 2026. Página Planes y formulario: desarrollo HTML terminado. Calendario y presentismo: cerrado. Fase 8 (página pública): implementada.* Calendario y presentismo: cerrado por ahora. Fase 6 (alumnos sin login): completada. Fase 7 (pizarra/sala): mejoras documentadas. Fase 8 (página pública): implementada. Reparación calendario slot_config: findFirst, setCapacidadMaxima robusto. Menú servidor: sin modificaciones (Workbench descartado). Página Planes: /planes, panel administración, PlanPublico, ConfiguracionPaginaPublica. Documentación: AYUDA_MEMORIA y PLAN_DE_DESARROLLO unificados.*
