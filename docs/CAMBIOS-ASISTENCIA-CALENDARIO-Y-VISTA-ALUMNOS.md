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
- Al **abrir el calendario** se ejecuta la lógica de “registrar ausentes” para slots ya pasados (solo se crean registros cuando no existe ninguno).

### Archivos modificados / añadidos

| Archivo | Cambios |
|---------|--------|
| `AsistenciaRepository` | `findByFechaBetween(inicio, fin)`, `findByFechaBetweenWithUsuario(inicio, fin)` con JOIN FETCH de `usuario` |
| `AsistenciaService` | `registrarAusenteSiNoExiste(usuario, fecha)`, `getMapaPresentePorUsuarioYFecha(inicio, fin)` con query que trae usuario, claves con `String.valueOf(id)` |
| `CalendarioService` | Inyección de `AsistenciaService`, `registrarAusentesParaSlotsPasados(calendario)` para marcar ausentes en slots pasados de la semana |
| `CalendarioController` | Tras generar el calendario: llamada a `registrarAusentesParaSlotsPasados`, construcción de `asistenciaMap`, **relleno de `presentePorUsuarioId` por slot** según fecha del día, paso de `slotsPorDiaList` (con estado por usuario) a la vista |
| `CalendarioSemanalDTO.SlotHorarioDTO` | Nuevo campo `Map<Long, Boolean> presentePorUsuarioId` (true/false/null por usuario del slot) |
| `semanal-profesor.html` | Puntos verde/rojo/gris por usuario usando `slot.presentePorUsuarioId[usuario.id]`, leyenda (Presente, Ausente, Pendiente), script con delegación de eventos para clic en el punto y llamada a `POST /calendario/api/marcar-asistencia` |
| `CalendarioController` | Endpoint `POST /calendario/api/marcar-asistencia` (usuarioId, fecha, presente) → `guardarOActualizarProgreso` |

### Persistencia y consistencia

- Los estados se guardan con `AsistenciaService.guardarOActualizarProgreso` (crea o actualiza un registro por `(usuario, fecha)`).
- El mapa para pintar los puntos se obtiene con `findByFechaBetweenWithUsuario` para evitar problemas de lazy y asegurar que al volver a entrar al calendario se muestren correctamente verde/rojo/gris.

---

## 2. Vista Mis Alumnos – Columna “Presente”

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
| `ProfesorController` (vista alumno) | La columna “Presente” en Mis Alumnos usaba `asistenciaHoy`; ahora se basa en `estadoAsistenciaHoy` y en “asiste hoy” |
| `dashboard.html` | Encabezado "Presente (fecha)"; celda con botón solo si `estadoAsistenciaHoy[usuario.id]` existe; estilos gris/rojo/verde e iconos según estado; script de toggle que usa `urlMarcarAsistencia` y actualiza botón (clase, icono, texto, data-presente, data-estado) |

### Corrección previa (Presente vs Ausente)

- En el dashboard se corregió la lógica para que “Presente” solo se muestre cuando el registro de asistencia tiene `presente == true` (antes se consideraba “presente” si existía cualquier registro para ese día).

---

## 3. Unificación y flujo de datos

- **Única fuente de verdad:** tabla `Asistencia` (usuario, fecha, presente) y método `AsistenciaService.guardarOActualizarProgreso`.
- **Mismo endpoint para marcar:** `POST /calendario/api/marcar-asistencia` (usuarioId, fecha, presente) usado tanto por el calendario como por la vista de alumnos.
- Así, lo que se marca en el calendario se refleja en Mis Alumnos y al revés.

---

## 4. Resumen de archivos tocados

- **Repositorios:** `AsistenciaRepository`
- **Servicios:** `AsistenciaService`, `CalendarioService`
- **Controladores:** `CalendarioController`, `ProfesorController`
- **DTOs:** `CalendarioSemanalDTO` (SlotHorarioDTO)
- **Vistas:** `calendario/semanal-profesor.html`, `profesor/dashboard.html`
- **Documentación:** `docs/CAMBIOS-ASISTENCIA-CALENDARIO-Y-VISTA-ALUMNOS.md` (este archivo)

---

## 5. Sugerencia de commit

```
feat(asistencia): calendario y vista alumnos con presente/ausente/pendiente unificados

- Calendario semanal: puntos verde/rojo/gris por alumno y slot; clic alterna estado vía API
- Registrar ausentes al abrir calendario (solo slots pasados, sin sobrescribir)
- Estado por slot en DTO (presentePorUsuarioId) y carga con JOIN FETCH para persistencia al recargar
- Endpoint POST /calendario/api/marcar-asistencia (usuarioId, fecha, presente)
- Vista Mis Alumnos: columna Presente con fecha actual; botones solo para quienes asisten hoy
- Tres estados: Pendiente (gris), Ausente (rojo), Presente (verde); mismo API que calendario
- Documentación en docs/CAMBIOS-ASISTENCIA-CALENDARIO-Y-VISTA-ALUMNOS.md
```
