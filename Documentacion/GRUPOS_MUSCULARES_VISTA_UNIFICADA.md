# Grupos Musculares: vista unificada y formulario inline

**Estado:** Implementado (Febrero 2026)

**URL:** `/profesor/mis-grupos-musculares`

---

## 1. Resumen

La gestión de grupos musculares del profesor se unificó en una sola vista: listado de grupos del sistema, listado de “Mis grupos” y **formulario de creación** integrado en la misma página. Todos los enlaces “Crear grupo muscular” (desde ejercicios o panel) llevan a esta URL.

---

## 2. Comportamiento

### 2.1 Vista principal

- **Título:** “Grupos Musculares” con subtítulo explicativo.
- **Acciones superiores:**
  - “Volver a ejercicios” → `/profesor/mis-ejercicios`
  - “Volver al ejercicio” (solo si se entró con `returnUrl`, p. ej. desde crear/editar ejercicio)
- **Tarjeta “Nuevo grupo muscular”** (arriba, ancho completo):
  - Campo: nombre del grupo.
  - Botón: “Crear”.
  - Envío por POST a `/profesor/mis-grupos-musculares/nuevo`.
  - Texto de ayuda: se guarda en mayúsculas y no puede repetirse con sistema ni con los propios.
- **Dos columnas debajo:**
  - **Izquierda – Grupos del sistema:** 6 grupos predefinidos (solo lectura). Tarjetas en grid (varias por fila).
  - **Derecha – Mis grupos:** grupos creados por el profesor; tarjetas en grid con acciones Editar / Eliminar.

### 2.2 Parámetro `returnUrl`

- **GET** `/profesor/mis-grupos-musculares?returnUrl=...`  
  Se acepta `returnUrl` si empieza por `/profesor/`. Se muestra “Volver al ejercicio” y se reenvía en el formulario de creación (campo oculto).

- **GET** `/profesor/mis-grupos-musculares/nuevo?returnUrl=...`  
  Redirige a `/profesor/mis-grupos-musculares` conservando `returnUrl` (enlaces antiguos siguen funcionando).

- **POST** crear grupo con `returnUrl`:  
  Tras guardar correctamente, redirección a `returnUrl?success=grupo_creado` (p. ej. volver al formulario de ejercicio). Sin `returnUrl`, redirección a la lista con `?success=grupo_creado`.

### 2.3 Errores en creación

Si hay error de validación o nombre duplicado, el POST redirige a `/profesor/mis-grupos-musculares` (con `returnUrl` en query si aplica) y se envían por **flash**:

- `grupo` (objeto con el nombre enviado)
- `org.springframework.validation.BindingResult.grupo`
- `errorMessage` (mensaje de error)

La vista unificada muestra el formulario con errores y el mensaje correspondiente.

---

## 3. Enlaces “Crear grupo muscular”

Todos deben apuntar a la vista unificada:

| Origen                    | URL objetivo                                                                 |
|---------------------------|-------------------------------------------------------------------------------|
| Formulario nuevo ejercicio | `/profesor/mis-grupos-musculares?returnUrl=/profesor/mis-ejercicios/nuevo`   |
| Formulario editar ejercicio | `/profesor/mis-grupos-musculares?returnUrl=/profesor/mis-ejercicios/editar/{id}` |
| Lista de ejercicios (tarjeta) | `/profesor/mis-grupos-musculares`                                            |

No se usa ya la vista separada `/profesor/mis-grupos-musculares/nuevo` para mostrar formulario; solo redirige a la lista.

---

## 4. Layout y eliminación de duplicados

- **Formulario de crear:** en una sola fila arriba (tarjeta full width) para no mezclar con las listas.
- **Columnas:** solo listas de grupos; tarjetas en grid (`col-6 col-md-4`) para que varios grupos queden en la misma fila y escale bien.
- **Botones quitados en esta vista:**
  - “+ Crear Grupo Muscular” (verde), porque el crear está incluido en la tarjeta superior.
  - “Ir a Mis Ejercicios” del pie, porque ya existe “Volver a ejercicios” arriba.

---

## 5. Archivos relevantes

| Archivo | Uso |
|---------|-----|
| `ProfesorController.java` | GET lista con `returnUrl` y `grupo`; GET `/nuevo` redirige a lista; POST crear con redirect + flash en error. |
| `grupos-musculares-lista.html` | Vista unificada: formulario arriba, dos columnas (sistema / mis grupos), mensajes y botones actualizados. |
| `formulario-ejercicio-profesor.html` | Enlace “Crear grupo muscular” → lista con `returnUrl` nuevo ejercicio. |
| `formulario-modificar-ejercicio-profesor.html` | Enlace “Crear grupo muscular” → lista con `returnUrl` editar ejercicio. |
| `grupo-muscular-form.html` | Se sigue usando solo para **editar** grupo (GET/POST `/mis-grupos-musculares/editar/{id}`). |

---

## 6. Seguridad

- `returnUrl` solo se acepta si comienza por `/profesor/` (evitar open redirect).
- Creación y edición de grupos siguen restringidas al profesor autenticado y a sus propios grupos donde corresponda.
