# Changelog – Modal de progreso y ficha del alumno (Febrero 2026)

Mejoras en el registro de progreso del alumno (asistencia, grupos musculares trabajados, observaciones) y unificación del flujo desde el panel y desde el detalle del alumno.

---

## 1. Resumen de cambios

- **Checkbox "Marcar como presente":** En el modal de progreso se muestra correctamente tildado cuando ese día el alumno ya tiene registro de presente (uso de `isPresente()` en Thymeleaf).
- **Varios grupos musculares:** Sustituido el `<select multiple>` por **checkboxes** para elegir varios grupos sin usar Ctrl/Cmd; en el historial se muestran separados por coma.
- **Observaciones largas:** Columna `observaciones` ampliada a 2000 caracteres (script SQL); formularios con `maxlength="2000"` y texto de ayuda.
- **Un solo modal de progreso:** El botón "Progreso" en la tabla del panel (Mis Alumnos) ya no abre un modal vacío en el dashboard; redirige a la **ficha del alumno** con `?openModal=progreso`, donde se abre el mismo modal con todos los datos cargados (fecha, presente, grupos, observaciones).

---

## 2. Cambios técnicos

### 2.1 Checkbox "Marcar como presente"

- **Problema:** En Thymeleaf, `asistenciaHoy.presente` no reflejaba el getter `isPresente()` de la entidad.
- **Solución:** En `alumno-detalle.html` se usa `th:checked="${asistenciaHoy != null && asistenciaHoy.isPresente()}"`.

### 2.2 Grupos musculares (varios)

- **Antes:** `<select name="grupoIds" multiple>` con instrucción de mantener Ctrl para seleccionar varios; en la práctica solo se podía elegir uno de forma clara.
- **Ahora:** Lista de **checkboxes** con `name="grupoIds"` y `value="${g.id}"` en ambos contextos (detalle alumno y, cuando existía, dashboard). El backend ya recibía `List<Long> grupoIds`; no hubo cambio en el controlador.
- **Historial:** Se mantiene `#strings.listJoin(asistencia.gruposTrabajados.![nombre], ', ')` para mostrar los grupos separados por coma.

### 2.3 Observaciones (longitud 2000)

- **Problema:** Error 500 "Data too long for column 'observaciones'" al guardar texto largo. La entidad tenía `@Column(length = 2000)` pero la columna en BD podía ser VARCHAR(255).
- **Solución:**
  - Script **`scripts/alter_asistencia_observaciones.sql`**: `ALTER TABLE asistencia MODIFY COLUMN observaciones VARCHAR(2000) NULL;` (ejecutar una vez en MySQL/MariaDB).
  - En los textareas del modal: `maxlength="2000"` y texto de ayuda "Máximo 2000 caracteres."

### 2.4 Unificación del flujo "Progreso"

- **Problema:** Desde el detalle del alumno el modal se abría con datos (asistenciaHoy, grupos, observaciones); desde el panel (tabla Mis Alumnos) se abría un segundo modal sin datos.
- **Solución:** Un solo flujo y un solo modal.
  - **Dashboard:** El botón "Progreso" de cada fila pasó de abrir un modal a ser un **enlace** a `/profesor/alumnos/{id}?openModal=progreso`.
  - **Alumno-detalle:** Al cargar la página, si la URL tiene `openModal=progreso`, un script abre automáticamente el modal `#modalProgreso` (Bootstrap).
  - **Eliminado:** Modal `#modalProgresoDashboard`, formulario `#formProgresoDashboard` y el JavaScript asociado en `dashboard.html`.

---

## 3. Archivos modificados

| Archivo | Cambio |
|--------|--------|
| **profesor/alumno-detalle.html** | Checkbox presente con `isPresente()`; grupos musculares por checkboxes; textarea observaciones con `maxlength="2000"` y texto de ayuda; script que abre el modal si `openModal=progreso` en la URL. |
| **profesor/dashboard.html** | Botón "Progreso" sustituido por enlace a `/profesor/alumnos/{id}?openModal=progreso`; eliminado modal de progreso y su JS. |
| **scripts/alter_asistencia_observaciones.sql** | Nuevo: script para ampliar la columna `observaciones` a VARCHAR(2000). |

---

## 4. Comportamiento actual

- **Desde la ficha del alumno:** Clic en "Progreso" → se abre el modal en la misma página con fecha de hoy, checkbox presente, grupos y observaciones pre-rellenados si existe registro para hoy.
- **Desde el panel (Mis Alumnos):** Clic en "Progreso" → navegación a la ficha de ese alumno con `?openModal=progreso` → la página carga y el modal se abre automáticamente con los mismos datos.
- **Guardar:** En ambos casos el POST va a `/profesor/alumnos/{id}/progreso` y tras guardar se redirige a la ficha con `?progreso=ok`.

---

*Documentación creada en Febrero 2026.*
