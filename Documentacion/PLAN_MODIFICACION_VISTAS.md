# Plan de modificación de vistas (Mattfuncional → alineación con MiGymVirtual)

**Objetivo:** Unificar **aspecto responsive** y **paleta por módulo** con la app de referencia (`APP referencia/Migymvirtual/`), sin perder funcionalidad que **solo existe en Mattfuncional** (calendario, pizarra, presentismo, filtros por día/horario, etc.).

**Documentos de referencia en MiGymVirtual (leer antes de tocar CSS/HTML):**

| Documento | Contenido |
|-----------|-----------|
| `APP referencia/Migymvirtual/Documentacion/PALETA_COLORES.md` | Colores por módulo (hex y criterios pastel / botones). |
| `APP referencia/Migymvirtual/Documentacion/GUIA_RESPONSIVE.md` | Breakpoints, panel móvil, tablas con scroll, patrones por pestaña. |
| `APP referencia/Migymvirtual/Documentacion/DOCUMENTACION_UNIFICADA.md` | §0.1 paleta; detalles de administración y backups. |

**Código de referencia principal del panel:**  
`APP referencia/Migymvirtual/src/main/resources/templates/profesor/dashboard.html`  
(+ `fragments/navbar.html`, `footer`, `static/style.css` donde aplique).

---

## 1. Alcance y exclusiones

- **Sí:** Mismo criterio visual y responsive que MiGymVirtual donde haya equivalente funcional.
- **No (por ahora):** Forzar el mismo layout en módulos **que no existen** en la referencia si eso retrasa entrega; se busca **usable en móvil** y coherencia de marca, no clon pixel a pixel imposible.
- **Módulos solo Mattfuncional:** **Pizarra**, **Calendario** (vista semanal / APIs de asistencia en panel), **Presentismo** integrado en listados — se tratan en fases propias después de alinear el panel base.

---

## 2. Orden de trabajo acordado

| Fase | Vista / módulo | Notas |
|------|----------------|--------|
| **2.1** | **Panel del profesor** (`profesor/dashboard.html`) | Tarjetas superiores + barra de acciones + estructura móvil. |
| **2.2** | **Pizarra** | Responsivo razonable; colores propios si no hay analogía en referencia. |
| **2.3** | **Calendario** | Idem. |
| **2.4** | Pestañas del panel: **Alumnos, Series, Mis rutinas, Asignaciones** | Paleta + filtros tipo referencia + excepciones (ver §4). |

---

## 3. Fase 2.1 — Panel del profesor (primer entregable)

**Estado (Mar 2026):** Implementado en código — paleta de tarjetas secundarias (Ejercicios naranja, Admin gris, Pizarra ámbar distinto), iconos por módulo en tarjetas grandes, icono **Series** = `fa-layer-group`, contador de ejercicios en tarjeta, vista móvil con grid de 8 accesos (incl. Calendario y Pizarra), pestañas ocultas en móvil con navegación por tarjetas, `?tab=` en historial al cambiar pestaña.

---

### 3.1 Tarjetas grandes (contadores)

En la referencia, las cuatro tarjetas **Alumnos, Series, Rutinas, Asignaciones** comparten estructura (`card-module`, gradientes, iconos Font Awesome):

| Tarjeta | Fondo (referencia) | Icono (referencia, escritorio) |
|---------|-------------------|---------------------------------|
| Alumnos | `#c8e6c9` → `#b2dfdb` | `fa-users` |
| Series | `#e1bee7` → `#d1c4e9` | `fa-layer-group` |
| Rutinas | `#fff9c4` → `#ffe082` | `fa-list-check` |
| Asignaciones | `#bbdefb` → `#b3e5fc` | `fa-clipboard-list` |

**Mattfuncional** replica esos gradientes; iconos por módulo en escritorio y móvil aplicados (ver estado arriba).

### 3.2 Tarjetas rectangulares secundarias (`main-action-card`)

En **MiGymVirtual** el layout es: **Ejercicios** (naranja pastel `#ffccbc` / `#ffab91`, icono `#bf360c`, `fa-dumbbell`), **Administrar** (gris `#cfd8dc` / `#b0bec5`, `fa-cogs`). **No** hay pizarra ni calendario en el grid de la referencia.

En **Mattfuncional** (implementado): Ejercicios y Admin alineados a la referencia; Calendario en celeste con `fa-calendar-week`; Pizarra en ámbar más claro que Ejercicios e icono `fa-tv`.

### 3.3 Botones bajo el título (`main-actions`)

Alinear colores con la referencia (`btn-alumnos`, `btn-series`, `btn-rutinas`, `btn-calendario`, etc.) y tamaños táctiles en móvil (`GUIA_RESPONSIVE.md` §5).

### 3.4 Responsive del dashboard

Tomar de la referencia:

- Grid → apilado / ancho completo bajo **991px**.
- Bloque **dashboard-mobile-card** (si aún no está parity completa en Matt): mismas clases y comportamiento que `dashboard.html` referencia.
- `table-responsive` + clases de scroll (`mgv-scroll-panel` / equivalente en `style.css` de Matt).

**Archivos típicos a tocar:**  
`src/main/resources/templates/profesor/dashboard.html`, `static/style.css`, posiblemente `fragments/navbar.html` / `footer`.

---

## 4. Fase 2.4 — Pestañas: Alumnos, Series, Rutinas, Asignaciones

### 4.1 Modelo de color

Aplicar la misma familia de color por pestaña que en la referencia (cabeceras, botones “Crear / Nueva…”, botón **Limpiar** del filtro, badges):

- Alumnos → verdes (`#c8e6c9`, `#81c784`, texto `#1b5e20`).
- Series → violetas.
- Rutinas → amarillos / naranja oscuro para texto.
- Asignaciones → azules.

### 4.2 Filtros y tabla

- **Series / Rutinas / Asignaciones:** copiar de la referencia el patrón **card de filtro** bajo el título, placeholder + **Limpiar** en la misma fila, y mejoras de UX que ya estén en `dashboard.html` referencia.
- **Mis Alumnos (excepción explícita):**
  - **Conservar** el filtro actual que usa **días y horarios** (no existe igual en MiGymVirtual).
  - **Conservar** la columna / lógica de **presentismo** y enlaces a flujos de asistencia.
  - **Sí aplicar:** formatos de **botones**, **badges**, **card** del filtro donde no pisen la lógica de días/horarios, y responsive de tabla (popover móvil, columnas ocultas, etc.) según `GUIA_RESPONSIVE.md` §5 y §5.1.

### 4.2.1 Registro de implementación (entorno 1 / 2)

**Convención:** Ir documentando solo en **este archivo** (`PLAN_MODIFICACION_VISTAS.md`), ampliando §4.2.1 y el estado de fases, para no multiplicar documentos.

**Terminología:** Entorno 1 ≈ escritorio (≥992px); entorno 2 ≈ móvil (≤991px en panel). Referencia: `APP referencia/Migymvirtual/`.

#### Login (`templates/login.html`)

- `viewport`, `100dvh`, `safe-area`, padding responsive; toggle contraseña y `autocomplete` en campos.
- Fondo degradado (sin depender de `/img/login.jpeg` en repo).

#### Navbar (`templates/fragments/navbar.html`)

- Entorno 2: barra compacta tipo referencia (logo + marca, sobre consultas en recuadro, texto de rol, **Salir**); ocultos Volver, panel, bloque nombre/correo y avatar.
- Enlaces de marca según sesión/rol (`sec:authorize` + `th:if` / `th:unless`).

#### `static/style.css`

- ≤576px: `.modal-confirmar-footer` en columna y botones anchos (confirmaciones genéricas).
- Excepción `#modalVerSerieMobile`: pie en fila, botones proporcionados; colores pastel para acciones del modal serie.
- `.modal-card-rutina`: tarjeta del modal de detalle de rutina.
- `.table-responsive.mgv-scroll-panel` (≤991px): scroll interno en tablas de lectura del panel.

#### Pestaña **Mis Alumnos** (`profesor/dashboard.html`)

- Entorno 2: columnas visibles **Nombre, Edad, Estado, Presente**; resto oculto hasta `lg`.
- Clic en fila → detalle (`data-alumno-href` + delegación en `tbody`); no navega si el clic es en enlace o botón (p. ej. presentismo).
- Filtros móvil: Estado + Tipo en una fila; Día + Horario en otra.

#### Pestaña **Mis Series**

- Card filtro buscar + Limpiar violeta; `#tablaSeries`; datos en `data-*` por fila.
- Entorno 2: sin columna Acciones; fila abre `#modalVerSerieMobile`; Ver en móvil misma pestaña.

#### Pestaña **Mis Rutinas**

- **Categorías** + **Nueva Rutina**; card filtro (amarillo rutina) + Limpiar.
- Tabla sin columna ID; categorías como badges; entorno 2 sin descripción ni acciones en tabla → `#modalVerRutinaMobile` (`.modal-card-rutina`); Ver en móvil misma pestaña.
- Tabla con `mgv-scroll-panel` donde aplica.

#### Pendiente en fase 2.4

- **Asignaciones:** misma línea que referencia MiGym (filtro en card, columnas y modal en móvil).

### 4.3 Detalle de alumno

Cuando se aborde la ficha, usar como guía `profesor/alumno-detalle.html` de la referencia para móvil/escritorio; respetar campos extra de Mattfuncional (notas, mediciones, etc.).

---

## 5. Seguimiento

- Ir cerrando **sub-bloques** (p. ej. “2.1 tarjetas escritorio”, “2.1 móvil”, “2.1 ejercicios+admin colores”) y compilar / probar en navegador real y tamaño móvil.
- **Documentación del plan de vistas:** actualizar **solo** este `PLAN_MODIFICACION_VISTAS.md` (estados de fase y §4.2.1), no crear archivos nuevos en `Documentacion/` salvo decisión explícita del proyecto.
- Opcional: añadir filas en `MEJORAS_DESDE_MIGYMVIRTUAL.md` por cada bloque de vistas completado.

---

## 6. Resumen ejecutivo

1. Leer **`PALETA_COLORES.md`** y **`GUIA_RESPONSIVE.md`** de la referencia.  
2. **Panel:** igualar colores e iconos de tarjetas equivalentes; corregir **Ejercicios** (naranja) y **Administrar** (gris); definir paleta explícita para **Calendario** y **Pizarra** sin confundir con otros módulos.  
3. **Luego** pizarra y calendario como vistas dedicadas.  
4. **Después** pestañas del panel con paleta MiGymVirtual y filtros mejorados, **salvo** filtro por días/horarios y presentismo en alumnos.

---

*Creado para Mattfuncional. Orden de trabajo acordado: panel profesor → pizarra → calendario → pestañas (con excepción alumnos).*
