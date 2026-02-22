# Manual del usuario – Mattfuncional

Manual para quien usa el sistema a diario: **profesor**, **administrador** o **ayudante**.  
Para contexto técnico del proyecto ver [LEEME_PRIMERO.md](LEEME_PRIMERO.md). Para mejoras pendientes ver [AYUDA_MEMORIA.md](AYUDA_MEMORIA.md).

---

## 1. Acceso al sistema

- **URL:** La que te haya dado el administrador (ej. `http://tu-servidor:8080`).
- **Inicio de sesión:** Solo usuarios con rol **ADMIN**, **AYUDANTE** o **DEVELOPER**. Los alumnos no tienen usuario ni contraseña; se gestionan desde el panel.
- **Credenciales:** Correo electrónico y contraseña. Si las olvidás, un administrador puede restablecerlas desde **Usuarios del sistema**.

Tras ingresar, se abre el **Panel del profesor** (dashboard).

---

## 2. Panel del profesor (dashboard)

En el panel ves un resumen con:

- **Alumnos** (total).
- **Series** (total).
- **Rutinas** (total).
- **Asignadas** (rutinas asignadas a alumnos).

Y botones de acceso rápido:

- **Crear Serie** – Crear una nueva serie de ejercicios.
- **Crear Rutina** – Crear una rutina (combinando series).
- **Ver Ejercicios** – Lista de ejercicios (predeterminados y propios).
- **Grupos Musculares** – Gestionar grupos (BRAZOS, PIERNAS, etc.) y los que crees vos.
- **Calendario Semanal** – Ver la semana y marcar presente/ausente.
- **Pizarra** – Pizarra digital para mostrar en TV en la sala.

Desde la barra inferior o el menú podés ir a **Mis Alumnos**, **Mis Series**, **Mis Rutinas** y **Asignaciones**.

---

## 3. Alumnos

### 3.1 Lista de alumnos (Mis Alumnos)

- Se muestran todos los alumnos del profesor con columnas: nombre, estado (Activo/Inactivo), tipo de asistencia, celular, **Presente** (fecha del día), acciones.
- **Filtros:** Por nombre, estado, tipo (presencial/virtual/semipresencial), día y horario. Los filtros se recuerdan al volver a la página.
- **Acciones por alumno:** Ver (ficha), Editar, Dar presente (cicla Pendiente → Presente → Ausente), Asignar rutina, y en la ficha también Eliminar.
- **Alumnos inactivos:** No se puede asignar rutina ni dar presente; el botón muestra un aviso.

### 3.2 Crear alumno

- Botón **Crear alumno** (o similar). Completás: nombre, edad, sexo, peso, correo (interno), celular, tipo de asistencia (Presencial/Virtual/Semipresencial), horarios de asistencia (días y franjas horarias) y detalle de asistencia (ej. “2 veces por semana”).
- Los alumnos **no tienen usuario ni contraseña**; no inician sesión. La rutina se comparte por enlace o WhatsApp.

### 3.3 Editar alumno

- Desde la lista, **Editar**. Podés cambiar datos personales, estado (Activo/Inactivo), fechas de alta/baja, horarios y detalle de asistencia.

### 3.4 Ficha del alumno (detalle)

- Al hacer clic en **Ver** se abre la ficha con:
  - Datos personales y estado.
  - **Historial de asistencia** (registros de presente/ausente y observaciones).
  - **Resumen mensual de asistencias** (modal por mes).
  - **Progreso:** modal para cargar observaciones, grupos trabajados y marcar presente/ausente para una fecha.
  - **Rutinas asignadas:** tabla con nombre de rutina, enlace para ver la hoja, **Copiar enlace** y **WhatsApp** (abre chat con el celular del alumno y el enlace de la rutina).
  - Botón **Asignar rutina** para elegir una rutina y asignarla al alumno.

---

## 4. Ejercicios (Mis Ejercicios)

- **Lista:** Ejercicios **predeterminados del sistema** (marcados con estrellita azul) y **ejercicios propios** del profesor.
- Podés **buscar** por nombre o descripción y **filtrar** por grupo muscular.
- **Acciones:** Ver (modal), Editar, Eliminar. Los predeterminados también se pueden editar o eliminar.
- **Crear ejercicio:** Botón **Nuevo**; completás nombre, descripción, tipo, grupos musculares y opcionalmente imagen o video.
- Las imágenes de los predeterminados se cargan solas si existen en el servidor (1.webp … 60.webp); si no, el ejercicio se crea sin imagen.

---

## 5. Series

- **Mis Series:** Lista de series. Cada serie agrupa varios ejercicios (con repeticiones, peso, etc.).
- **Crear serie:** Definís nombre y agregás ejercicios desde el selector, con repeticiones y orden. Podés **subir/bajar** el orden de los ejercicios en la serie.
- **Editar serie:** Mismo flujo; podés agregar o quitar ejercicios y cambiar el orden.
- **Ver serie:** Abre la vista de la serie con sus ejercicios.

---

## 6. Rutinas

- **Mis Rutinas:** Lista de rutinas. Una rutina se arma con **una o más series**.
- **Crear rutina:** Nombre y descripción; elegís series (por clic en tarjetas). La lista “Series seleccionadas” permite **subir/bajar** para definir el orden antes de guardar.
- **Editar rutina:** Podés cambiar detalles, agregar o quitar series y reordenar con Subir/Bajar.
- **Asignar rutina a un alumno:** Desde la ficha del alumno, **Asignar rutina**; elegís la rutina y se genera un **enlace único** para esa asignación. Ese enlace se puede **copiar** o enviar por **WhatsApp** (abre el chat con el celular del alumno y el enlace).
- **Hoja de rutina:** El enlace abre una **hoja pública** (sin login) con la rutina para que el alumno la vea en el celular o en papel.

---

## 7. Calendario semanal

- **Acceso:** Botón **Calendario Semanal** en el panel (se abre en nueva pestaña).
- **Vista:** Semana con días en columnas y franjas horarias; en cada celda aparecen los alumnos que tienen asistencia ese día/hora (según su horario o excepciones).
- **Presente / Ausente / Pendiente:** Junto a cada alumno hay un **punto** (verde = presente, rojo = ausente, gris = pendiente). Un **clic** en el punto **cicla**: Pendiente → Presente → Ausente → Pendiente. Los cambios se guardan al instante y se ven también en la columna “Presente” de Mis Alumnos y en el historial de la ficha.
- **Excepciones (recuperar clase):** Podés agregar a un alumno a un día/horario que no es su horario habitual (botón “+” en la celda si está habilitado). Ese alumno aparece con etiqueta “Ex” y también podés marcarle presente/ausente desde el calendario.

---

## 8. Asistencia desde Mis Alumnos

- En la tabla de alumnos, la columna **“Presente (dd/MM/yyyy)”** muestra el estado del día actual.
- Solo para alumnos **presenciales** que tienen horario el **día de la semana actual** se muestra un botón que cicla: **Pendiente → Presente → Ausente → Pendiente**. Es el mismo criterio que en el calendario; lo que marques acá se ve en el calendario y en el historial del alumno.

---

## 9. Grupos musculares

- **Mis Grupos Musculares:** El sistema trae grupos por defecto (BRAZOS, PIERNAS, PECHO, ESPALDA, CARDIO, ELONGACIÓN). Vos podés **crear grupos propios** y usarlos en ejercicios y series.
- Acciones: listar, crear, editar y eliminar (solo los grupos propios). Los ejercicios y filtros usan estos grupos.

---

## 10. Pizarra (pantalla de sala)

- **Objetivo:** Mostrar en una TV en la sala la rutina o ejercicios del día (pizarra digital).
- **Panel profesor:** Creás una **pizarra**, elegís número de columnas (1–6). En el **editor** arrastrás ejercicios a cada columna; podés editar **título de columna** (ej. nombre del alumno) y **peso/repeticiones** en cada tarjeta. Los cambios se guardan al salir del campo o al soltar un ejercicio.
- **Vista TV:** Una URL tipo `/sala/{token}` se abre en la TV (o en otra pestaña). Ahí se ve la pizarra en pantalla completa; un botón **Actualizar** refresca el contenido cuando cambies algo desde el panel.

---

## 11. Usuarios del sistema (solo ADMIN y DEVELOPER)

- **Acceso:** Menú **Usuarios del sistema** (no visible para AYUDANTE).
- **Qué se hace:** Listar usuarios con login (ADMIN, AYUDANTE, DEVELOPER), **editar** nombre, correo, rol y contraseña. El usuario DEVELOPER del sistema no se puede eliminar ni cambiar de rol desde esta pantalla.
- **Mi perfil:** Bloque para que cada uno cambie su propia contraseña o datos si la app lo permite.

---

## 12. Resumen rápido

| Quiero…                    | Dónde |
|----------------------------|--------|
| Ver o editar alumnos       | Panel → Mis Alumnos |
| Dar presente hoy           | Mis Alumnos (columna Presente) o Calendario semanal |
| Crear o editar ejercicios  | Ver Ejercicios / Mis Ejercicios |
| Armar series o rutinas    | Mis Series, Mis Rutinas |
| Asignar rutina a un alumno | Ficha del alumno → Asignar rutina |
| Compartir rutina           | En la ficha: Copiar enlace o WhatsApp |
| Marcar semana en calendario| Calendario semanal (puntos verde/rojo/gris) |
| Pizarra para la TV         | Pizarra → crear/editar → abrir URL en la TV |
| Gestionar usuarios        | Usuarios del sistema (solo ADMIN/DEVELOPER) |

---

*Última actualización: Febrero 2026.*
