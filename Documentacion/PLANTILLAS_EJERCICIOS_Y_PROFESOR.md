# Plantillas: ejercicios (unificado)

## Estado actual (post-unificación)

Hay **una sola base de ejercicios** para todos los usuarios. Crear y editar se hace desde **Mis Ejercicios** (`/profesor/mis-ejercicios`). Las rutas antiguas (`/ejercicios/nuevo`, `/ejercicios/modificar/{id}`, `/ejercicios/abm`) redirigen a ese flujo.

---

## Estructura en `templates/ejercicios/`

| Archivo | Uso | Rutas |
|--------|-----|-------|
| **formulario-ejercicio.html** | Crear ejercicio | GET/POST `/profesor/mis-ejercicios/nuevo` (ProfesorController). GET `/ejercicios/nuevo` → redirect a nuevo. |
| **formulario-modificar-ejercicio.html** | Editar ejercicio | GET/POST `/profesor/mis-ejercicios/editar/{id}` (ProfesorController). GET `/ejercicios/modificar/{id}` → redirect a editar. |
| **exercise-lista.html** | Listado (otro flujo) | `/exercise/lista` |
| **abm-ejercicios.html** | Legacy | Ningún controlador la devuelve; `/ejercicios/abm` redirige a `/profesor/mis-ejercicios`. Enlaces internos actualizados a mis-ejercicios. |
| **ejercicios-profesor.html** | Legacy | No referenciada por controladores; enlaces actualizados a mis-ejercicios. |

Se eliminaron **formulario-ejercicio-profesor.html** y **formulario-modificar-ejercicio-profesor.html**; su contenido pasó a **formulario-ejercicio.html** y **formulario-modificar-ejercicio.html** para evitar duplicados.

---

## Resumen

- **Crear ejercicio:** `/profesor/mis-ejercicios/nuevo` → `ejercicios/formulario-ejercicio.html` (POST a `/profesor/mis-ejercicios/nuevo`).
- **Editar ejercicio:** `/profesor/mis-ejercicios/editar/{id}` → `ejercicios/formulario-modificar-ejercicio.html` (POST a `/profesor/mis-ejercicios/editar/{id}`).
- **Lista:** `/profesor/mis-ejercicios` → `profesor/ejercicios-lista.html`.
