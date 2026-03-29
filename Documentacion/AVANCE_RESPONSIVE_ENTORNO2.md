# Avance — responsive entorno 2 y alineación MiGymVirtual

**Fecha de corte:** 28/03/2026  
**Contexto:** Entorno 1 = escritorio (≥992px aprox.); entorno 2 = móvil (≤991px en panel). Documento para retomar el trabajo al día siguiente.

---

## Resumen ejecutivo

Se avanzó en **login**, **navbar móvil**, **pestaña Mis Alumnos** (tabla y filtros), **pestaña Mis Series** (tabla, modal y filtro) y **estilos globales de modales** donde chocaban con el modal de serie. La referencia visual/UX sigue siendo `APP referencia/Migymvirtual/` y el plan general en `PLAN_MODIFICACION_VISTAS.md`.

---

## 1. Login (`templates/login.html`)

- `viewport`, `100dvh`, márgenes `safe-area`, padding responsive.
- Botón mostrar/ocultar contraseña + `autocomplete` en campos.
- Fondo degradado violeta/índigo (sin imagen `/img/login.jpeg` para no depender de un archivo ausente en repo).

---

## 2. Navbar (`templates/fragments/navbar.html`)

- **Móvil (≤991px):** layout tipo MiGym — logo + marca; ocultos Volver, “Ir a mi Panel”, bloque nombre/correo y avatar.
- Sobre consultas en recuadro con borde; texto de **rol** (Administrador, Ayudante, etc.); botón **Salir** compacto.
- Marca con enlace según sesión/rol (inicio vs panel) mediante `sec:authorize` + `th:if` / `th:unless`.

---

## 3. `static/style.css`

- **≤576px:** `.modal-confirmar-footer` en columna y botones a ancho completo (mejor táctil en confirmaciones genéricas).
- **Excepción** `#modalVerSerieMobile`: pie en **fila**, botones proporcionados (~48px alto), sin apilar como el resto de modales.
- Colores **pastel** para Ver / Editar / Eliminar dentro de ese modal (coherentes con la pestaña Series del panel).

---

## 4. Panel — pestaña **Mis Alumnos** (`profesor/dashboard.html`)

- Tabla con clase `table-alumnos-dashboard`: en **≤991px** solo columnas **Nombre, Edad, Estado, Presente** (`d-none d-lg-table-cell` en Celular, Tipo, Rutinas, Acciones).
- Nombre como enlace a ficha; `data-alumno-href` + clic en fila (delegado en `tbody`) para abrir detalle sin depender solo del nombre; se ignora clic en enlaces/botones (p. ej. Presente).
- Filtros en móvil: **Estado + Tipo** en una fila (`col-6`), **Día + Horario** en otra; búsqueda y Limpiar a ancho completo donde aplica.

---

## 5. Panel — pestaña **Mis Series** (`profesor/dashboard.html`)

- Tarjeta **Buscar por nombre** + **Limpiar**; tabla `#tablaSeries`, filas `fila-serie` con `data-nombre`, `data-ejercicios`, `data-descripcion`, URLs ver/editar/eliminar.
- **≤991px:** columna **Acciones** oculta (`.col-acciones-series`); filas `serie-row-clickable` abren **`#modalVerSerieMobile`** con datos y botones; **Ver** en móvil cierra modal y navega en la misma pestaña.
- Script de filtro por nombre y script del modal (ancho ≤991px).

---

## 6. Pendiente sugerido (mañana)

- **Fase 2.4 restante:** Rutinas y Asignaciones en entorno 2 (tabla + modal al tocar fila, como en referencia MiGym).
- **Pizarra / Calendario** (fases 2.2–2.3 del plan).
- Opcional: imagen `static/img/login.jpeg` + capa de fondo tipo referencia.
- Revisar si hace falta el mismo patrón de modal/fila en otras vistas fuera del dashboard.

---

## Archivos tocados en esta tanda (referencia rápida)

| Archivo |
|---------|
| `src/main/resources/templates/login.html` |
| `src/main/resources/templates/fragments/navbar.html` |
| `src/main/resources/templates/profesor/dashboard.html` |
| `src/main/resources/static/style.css` |

*(Si hubo otros cambios menores en la misma sesión, conviene `git status` antes del commit.)*
