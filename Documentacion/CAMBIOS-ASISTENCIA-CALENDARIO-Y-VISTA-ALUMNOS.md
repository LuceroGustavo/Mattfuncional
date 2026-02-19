# Documentación: Asistencia en Calendario y Vista de Alumnos

## Resumen

Se implementó y unificó la funcionalidad de **dar presente / ausente** entre el **calendario semanal del profesor** y la **vista Mis Alumnos** del panel del profesor. Los cambios se reflejan en ambos lugares y persisten en base de datos.

---

## 1. Calendario semanal – Asistencia por slot

### Comportamiento

- En cada celda del calendario, junto al nombre de cada alumno, se muestra un **punto de estado**:
  - **Verde:** presente
  - **Rojo:** ausente
  - **Gris:** pendiente (aún sin marcar)
- **Clic en el punto:** alterna entre presente y ausente vía API; la vista se actualiza sin recargar.
- Al **abrir el calendario** se ejecuta la lógica de "registrar ausentes" para slots ya pasados (solo se crean registros cuando no existe ninguno).
- **Fechas futuras:** los puntos no se muestran ni se pueden marcar en días posteriores a hoy.
- **Excepciones por día/hora:** botón “+” sutil por celda, habilitable con un botón superior, para agregar un alumno por excepción (modal con alumno + motivo). Los alumnos agregados por excepción muestran etiqueta `Ex`.

### Archivos modificados / añadidos

| Archivo | Cambios |
|---------|--------|
| `AsistenciaRepository` | `findByFechaBetween(inicio, fin)`, `findByFechaBetweenWithUsuario(inicio, fin)` con JOIN FETCH de `usuario` |
| `AsistenciaService` | `registrarAusenteSiNoExiste(usuario, fecha)`, `getMapaPresentePorUsuarioYFecha(inicio, fin)` con query que trae usuario, claves con `String.valueOf(id)` |
| `CalendarioService` | Inyección de `AsistenciaService`, `registrarAusentesParaSlotsPasados(calendario)` para marcar ausentes en slots pasados de la semana |
| `CalendarioController` | Tras generar el calendario: llamada a `registrarAusentesParaSlotsPasados`, construcción de `asistenciaMap`, **relleno de `presentePorUsuarioId` por slot** según fecha del día, paso de `slotsPorDiaList` (con estado por usuario) a la vista |
| `CalendarioSemanalDTO.SlotHorarioDTO` | Nuevo campo `Map<Long, Boolean> presentePorUsuarioId` (true/false/null por usuario del slot) |
| `CalendarioSemanalDTO.SlotHorarioDTO` | Nuevo campo `Map<Long, Boolean> excepcionPorUsuarioId` para marcar alumnos agregados por excepción |
| `semanal-profesor.html` | Puntos verde/rojo/gris por usuario usando `slot.presentePorUsuarioId[usuario.id]`, leyenda (Presente, Ausente, Pendiente), **puntos ocultos en fechas futuras**, botón “+” por celda y modal de excepción, script con delegación de eventos para clic en el punto y llamada a `POST /calendario/api/marcar-asistencia` |
| `CalendarioController` | Endpoint `POST /calendario/api/marcar-asistencia` (usuarioId, fecha, presente) → `guardarOActualizarProgreso` |
| `CalendarioExcepcion` | Nueva entidad para guardar excepciones (alumno, profesor, fecha, horaInicio, horaFin, motivo) |
| `CalendarioExcepcionService` | Alta de excepción y listado por semana |
| `CalendarioExcepcionRepository` | Query por semana y validación de duplicados |

### Persistencia y consistencia

- Los estados se guardan con `AsistenciaService.guardarOActualizarProgreso` (crea o actualiza un registro por `(usuario, fecha)`).
- El mapa para pintar los puntos se obtiene con `findByFechaBetweenWithUsuario` para evitar problemas de lazy y asegurar que al volver a entrar al calendario se muestren correctamente verde/rojo/gris.

---

## 2. Vista Mis Alumnos – Columna "Presente"

### Comportamiento

- **Título de la columna:** `Presente (dd/MM/yyyy)` con la **fecha del día actual**.
- **Botones solo para alumnos que deben asistir hoy:** se muestra botón únicamente si el alumno es **presencial** y tiene al menos un horario de asistencia para el **día de la semana actual** (ej. si hoy es martes, debe tener un `DiaHorarioAsistencia` con día = MARTES). El resto de celdas quedan **en blanco**.
- **Tres estados** (alineados con el calendario):
  - **Gris – Pendiente:** sin registro de asistencia para hoy.
  - **Rojo – Ausente:** registro con `presente = false`.
  - **Verde – Presente:** registro con `presente = true`.
- **Clic en el botón:** alterna el estado y llama al **mismo endpoint** que el calendario (`/calendario/api/marcar-asistencia`), por lo que los cambios se ven en el calendario y viceversa.

### Archivos modificados

| Archivo | Cambios |
|---------|--------|
| `ProfesorController` (dashboard) | Sustitución de `asistenciaHoy` (Boolean) por `estadoAsistenciaHoy` (Map<Long, String>: "PENDIENTE" \| "AUSENTE" \| "PRESENTE"). Cálculo solo para usuarios presenciales con horario el día de hoy (mapeo `DayOfWeek` → `DiaSemana`). Modelo: `estadoAsistenciaHoy`, `fechaHoyFormateada` |
| `ProfesorController` (vista alumno) | La columna "Presente" en Mis Alumnos usaba `asistenciaHoy`; ahora se basa en `estadoAsistenciaHoy` y en "asiste hoy" |
| `dashboard.html` | Encabezado "Presente (fecha)"; celda con botón solo si `estadoAsistenciaHoy[usuario.id]` existe; estilos gris/rojo/verde e iconos según estado; script de toggle que usa `urlMarcarAsistencia` y actualiza botón (clase, icono, texto, data-presente, data-estado) |

### Corrección previa (Presente vs Ausente)

- En el dashboard se corregió la lógica para que "Presente" solo se muestre cuando el registro de asistencia tiene `presente == true` (antes se consideraba "presente" si existía cualquier registro para ese día).

---

## 3. Unificación y flujo de datos

- **Única fuente de verdad:** tabla `Asistencia` (usuario, fecha, presente). Sin registro = pendiente.
- **Endpoint para marcar:** `POST /calendario/api/marcar-asistencia` (usuarioId, fecha, **estado**: PENDIENTE | PRESENTE | AUSENTE). PENDIENTE elimina el registro; PRESENTE/AUSENTE llaman a `guardarOActualizarProgreso`. Compatibilidad con parámetro `presente` (boolean).
- Lo que se marca en el calendario se refleja en Mis Alumnos y al revés.

---

## 4. Tres estados y pendiente por defecto (cierre presentismo)

- **Por defecto** no se asume ausente: todos quedan **pendiente** hasta que el profesor marque (feriados, días sin clase).
- **Ciclo en puntos y en columna Presente:** Pendiente → Presente → Ausente → Pendiente.
- **Se eliminó** la llamada a `registrarAusentesParaSlotsPasados` al abrir el calendario; ya no se crean registros "ausente" automáticamente para slots pasados.
- **AsistenciaService:** `eliminarRegistroAsistencia(Usuario, LocalDate)` para dejar estado pendiente.

---

## 5. Ficha del alumno – Historial y resumen mensual sincronizados con el calendario

- **GET `/profesor/alumnos/{id}/asistencias`:** devuelve en JSON la lista actualizada de asistencias del alumno (fecha, presente, observaciones, grupos trabajados).
- **Al cargar la ficha:** se llama a esa API y se actualiza la tabla "Historial de Asistencia" con los datos del servidor (incluye lo marcado en el calendario, también por excepción).
- **Al abrir el modal "Consultar asistencias":** se vuelve a pedir la lista, se actualiza el historial y se construye el resumen por mes y el detalle por día. El resumen y el historial reflejan lo marcado en el calendario sin recargar la página.
- **Robustez del modal:** si el JSON falla, el resumen se arma con el historial ya renderizado. Abrir/cerrar el modal **no modifica** la tabla de historial.

## 5.1 Admin: selector de profesor en historial
- En filas con **trabajo/observaciones**, el admin ve **selector directo** (no requiere click previo).
- El cambio se guarda **automáticamente** al seleccionar otro usuario.

---

## 6. Calendario en nueva pestaña

- El botón **"Calendario Semanal"** en el panel del profesor (`dashboard.html`) abre el calendario en **nueva pestaña** (`target="_blank"`, `rel="noopener noreferrer"`).

---

## 7. Resumen de archivos tocados

- **Repositorios:** `AsistenciaRepository`
- **Servicios:** `AsistenciaService`, `CalendarioService`
- **Controladores:** `CalendarioController` (API `estado`, sin `registrarAusentesParaSlotsPasados`), `ProfesorController` (GET `/profesor/alumnos/{id}/asistencias` en JSON)
- **DTOs:** `CalendarioSemanalDTO` (SlotHorarioDTO)
- **Vistas:** `calendario/semanal-profesor.html` (ciclo 3 estados, `data-estado`), `profesor/dashboard.html` (ciclo 3 estados, Calendario Semanal `target="_blank"`), `profesor/alumno-detalle.html` (historial con id, fetch asistencias al cargar y al abrir modal)
- **Documentación:** `Documentacion/CAMBIOS-ASISTENCIA-CALENDARIO-Y-VISTA-ALUMNOS.md` (este archivo), `AVANCES_DEL_APP.md`, `CHANGELOG_UNIFICADO_FEB2026.md` (sección 7)

---

## 8. Estado: calendario y presentismo cerrados por ahora

- Calendario semanal, asistencia (tres estados), excepciones, sincronización con historial y resumen, y apertura en nueva pestaña están implementados. **Se considera cerrado el módulo de calendario y presentismo por ahora** (Feb 2026).
