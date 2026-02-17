# Changelog – Alumno inactivo y limpieza en detalle del alumno (9 Febrero 2026)

Eliminación del botón obsoleto "Asignar Nueva Rutina" y desactivación de acciones cuando el alumno está en estado INACTIVO (excepto Editar y Eliminar).

---

## 1. Resumen de cambios

- **Botón "Asignar Nueva Rutina" eliminado:** En la ficha del alumno (`/profesor/alumnos/{id}`) se quitó el botón verde al final de la página que quedaba de la app antigua. La asignación de rutinas se hace desde la tarjeta "Rutinas asignadas" o desde el botón "Asignar rutina" en la sección de historial.
- **Alumno INACTIVO – acciones desactivadas:** Cuando `alumno.estadoAlumno == 'INACTIVO'`, todas las acciones que implican uso de rutinas o progreso se desactivan visual y funcionalmente. Los botones **Editar** y **Eliminar** del header siguen activos para poder reactivar o dar de baja al alumno.

---

## 2. Cambios técnicos

### 2.1 Eliminación del botón "Asignar Nueva Rutina"

- **Ubicación:** Final de la página de detalle del alumno.
- **Acción:** Se eliminó el bloque completo del botón (enlace verde "Asignar Nueva Rutina" dentro de un `div` con clases `d-flex gap-3 justify-content-center mt-4`).
- **Motivo:** Flujo heredado de la app vieja; la asignación ya se realiza desde la tarjeta de estadísticas y desde "Asignar rutina" en la sección de rutinas.

### 2.2 Variable Thymeleaf para estado inactivo

- En el contenedor principal de `alumno-detalle.html` se define:
  - `th:with="alumnoInactivo=${alumno.estadoAlumno == 'INACTIVO'}"`
- Se reutiliza en todo el template para mostrar versión activa o deshabilitada de botones y enlaces.

### 2.3 Elementos desactivados cuando el alumno está INACTIVO

| Elemento | Comportamiento si INACTIVO |
|----------|----------------------------|
| **Botón Progreso** (header) | Se muestra un botón deshabilitado con clase `btn-disabled-inactivo`; no abre el modal. Tooltip: "Activa el alumno para registrar progreso". |
| **Tarjeta "Rutinas asignadas"** (cuarta tarjeta de vista rápida) | En lugar del enlace "Clic para asignar", se muestra un `<div>` con la misma estructura visual y clase `stat-item-link-disabled`. Texto: "Activa el alumno para asignar". |
| **Botón "Asignar rutina"** (sección Historial de rutinas) | Se muestra como `<span>` con estilo de botón y clase `btn-disabled-inactivo`. Texto: "Asignar rutina (activa al alumno)". |
| **Tabla de rutinas – Ver** | Botón deshabilitado (`<span>` con `btn-disabled-inactivo`), no navega a la hoja de rutina. |
| **Tabla de rutinas – Copiar enlace** | Botón deshabilitado, no ejecuta la acción de copiar. |
| **Tabla de rutinas – WhatsApp** | Botón deshabilitado, no abre WhatsApp. |

### 2.4 Elementos que no se modifican (siempre activos)

- **Editar** (header): enlace a `/profesor/alumnos/editar/{id}`.
- **Eliminar** (header): enlace a `/profesor/alumnos/eliminar/{id}` con confirmación.

### 2.5 Estilos CSS para estado deshabilitado

- **`.btn-disabled-inactivo`:** `pointer-events: none`, `opacity: 0.6`, `cursor: not-allowed` (aplicable a botones deshabilitados por inactividad).
- **`.stat-item-link-disabled`:** Misma idea para la tarjeta de rutinas cuando no es clicable.

---

## 3. Archivos modificados

| Archivo | Cambio |
|--------|--------|
| **templates/profesor/alumno-detalle.html** | Variable `alumnoInactivo`; eliminado botón "Asignar Nueva Rutina"; condicionales `th:if` / `th:unless` para Progreso, tarjeta rutinas, botón Asignar rutina y botones Ver / Copiar enlace / WhatsApp; estilos `.btn-disabled-inactivo` y `.stat-item-link-disabled` en `<style>`. |

---

## 4. Comportamiento actual

- **Alumno ACTIVO:** Todas las acciones (Progreso, asignar rutina, Ver, Copiar enlace, WhatsApp, Editar, Eliminar) funcionan con normalidad.
- **Alumno INACTIVO:** Solo Editar y Eliminar son clicables. El resto se muestra deshabilitado y no ejecuta ninguna acción hasta que el profesor reactive al alumno.

---

*Documentación creada el 9 de Febrero de 2026.*
