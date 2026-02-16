# Plan de desarrollo – Mattfuncional

**Última actualización:** Febrero 2026  
**Uso:** Referencia de cambios realizados y estado del plan. Sirve como guía para commits y próximas fases.

---

## 1. Resumen

- **Proyecto:** Mattfuncional (evolución de MiGym).
- **Documentos de referencia:**
  - [PLAN_MODIFICACIONES_MATTFUNCIONAL.md](PLAN_MODIFICACIONES_MATTFUNCIONAL.md) – Visión general, lo que queda/eliminar, fases y checklist.
  - [OPTIMIZACION_EJERCICIOS_PREDETERMINADOS.md](OPTIMIZACION_EJERCICIOS_PREDETERMINADOS.md) – Diseño y convención de imágenes 1–60 en `uploads/ejercicios/`.

Este archivo resume **los cambios ya hechos** y **lo que falta** según ese plan.

---

## 2. Cambios realizados (ejercicios predeterminados)

### 2.1 Objetivo de los cambios

- Dejar de depender del admin para cargar ejercicios.
- Una sola fuente de imágenes: **`uploads/ejercicios/`** (sin copiar desde classpath).
- Nombres por número: **`1.webp`**, **`2.webp`**, … **`60.webp`** (o `.gif`). Si falta un número → imagen por defecto.
- Sin botón “Cargar predeterminados”: el sistema **asegura los 60 ejercicios si no existen** al abrir “Ver ejercicios”.

### 2.2 Archivos modificados / creados

| Archivo | Cambio |
|--------|--------|
| **ImagenServicio.java** | Nuevo método `registrarArchivoExistente(String nombreArchivo)`: comprueba que exista el archivo en `uploads/ejercicios/`, crea la entidad `Imagen` (sin copiar bytes) y la guarda. Devuelve `null` si el archivo no existe. |
| **ExerciseRepository.java** | Nuevo método `findByNameAndProfesorIsNull(String name)` para no duplicar ejercicios predeterminados al asegurar. |
| **ExerciseCargaDefaultOptimizado.java** | • `crearListaEjerciciosPredeterminados(..., boolean soloMetadata)`: tercer parámetro; si `soloMetadata == true` no se cargan imágenes desde classpath.<br>• **`asegurarEjerciciosPredeterminados()`**: si ya hay ≥ 60 predeterminados no hace nada; si no, crea solo los que falten usando metadata y asigna imagen desde `uploads/ejercicios/` (`N.webp` o `N.gif`), o imagen por defecto si no existe.<br>• Los 8 métodos `crearEjercicios*` (Brazos, Piernas, Pecho, Espalda, Hombros, Abdomen, Cardio, Elongación) tienen parámetro `soloMetadata` y usan `imagenOpcionalParaEjercicio(..., soloMetadata, ...)`.<br>• Helper `imagenOpcionalParaEjercicio(4 args)`: si `soloMetadata` devuelve `null`; si no, delega en `imagenOpcionalParaEjercicioDesdeClasspath` (carga desde classpath para flujo antiguo).<br>• Método que cargaba desde classpath renombrado a `imagenOpcionalParaEjercicioDesdeClasspath` (2 y 3 args) para evitar conflicto con el helper y corregir el error de compilación por `crearImagenParaEjercicio` inexistente. |
| **ProfesorController.java** | En el GET **`/profesor/mis-ejercicios`** se llama a `exerciseCargaDefaultOptimizado.asegurarEjerciciosPredeterminados()` antes de cargar la lista. |
| **profesor/ejercicios-lista.html** | Eliminado el botón/card **“Cargar predeterminados (60)”** (form POST a `/profesor/mis-ejercicios/cargar-predeterminados`). |
| **Documentacion/OPTIMIZACION_EJERCICIOS_PREDETERMINADOS.md** | Añadida tabla **Nº 1–60 → nombre del ejercicio** para nombrar archivos en `uploads/ejercicios/`. |
| **uploads/ejercicios/LEEME.txt** | Creado: instrucciones rápidas de nombres (1.webp … 60.webp o .gif) y que los predeterminados se aseguran al entrar a “Ver ejercicios”. |

### 2.3 Comportamiento actual

1. El profesor entra en **“Ver ejercicios”** (Mis ejercicios).
2. Se ejecuta **`asegurarEjerciciosPredeterminados()`**:
   - Si ya hay ≥ 60 ejercicios predeterminados → no hace nada.
   - Si faltan → crea solo los que falten (metadata fija) y para cada uno busca `uploads/ejercicios/N.webp` o `N.gif`; si existe, registra la imagen y la asigna; si no, deja imagen por defecto.
3. No se borra nada; no se usa classpath en este flujo para los predeterminados.

### 2.4 Pendiente opcional (ejercicios)

- Llamar a `asegurarEjerciciosPredeterminados()` al arranque (p. ej. en `DataInitializer` o un `ApplicationRunner`) para que los 60 existan desde el primer inicio.
- Deprecar o eliminar el endpoint POST `/profesor/mis-ejercicios/cargar-predeterminados` si ya no se usa en ningún otro sitio.

### 2.5 Cambios recientes (alumnos)

- **Formulario de alumno**: se quitó contraseña y se agregó **celular**.
- **Estado del alumno**: se agregó `ACTIVO/INACTIVO`, **fecha de alta/baja** y **historial de estado**.
- **Vista de alumnos**: columna de estado y celular + **filtros** por nombre, estado, tipo (presencial/online), día y horario.

### 2.6 Cambios recientes (rutinas, series y asignación)

- **Enlace público con token**: la hoja de rutina ahora usa `/rutinas/hoja/{token}` para evitar URLs adivinables.  
- **Botón “Copiar enlace”** en la hoja de rutina y en la ficha del alumno (tabla de rutinas asignadas).
- **WhatsApp** en ficha de alumno: botón que abre chat con el **celular del alumno** y el enlace listo.
- **Abrir en pestaña nueva**: botones “Ver” de rutinas abren en nueva pestaña.
- **Ficha del alumno**:  
  - **Historial de asistencia** solo registro.  
  - **Dar presente** pasó a la tabla de alumnos (acciones).  
  - **Rutinas asignadas** en formato tabla compacta + botón “Asignar rutina”.  
  - **Sin estado “terminada/en proceso”** en la vista del alumno.
- **Asignar rutina**: en las tarjetas de rutina se agregó botón **ver (ojo)**.
- **Creación de rutinas**: botón **Crear serie** en el selector de series; botón **Modificar** en cada tarjeta de serie (carga ejercicios al editar).
- **Vista de series**: botón **Ver serie** y nueva vista `series/verSerie.html`; eliminada columna ID en tabla del dashboard.
- **Modificar rutina**: título "Modificar Rutina"; carga de series con `findByIdWithSeries`; layout en tres bloques (Detalles, Series en esta Rutina, Añadir más Series); selección por clic en tarjeta (resaltado amarillo); vista previa "Se agregarán al guardar"; reps desde plantilla.
- **Fix guardado al modificar rutina (Feb 2026):** al guardar solo se conservaba una serie porque se borraban todas y luego se intentaba re-añadir por ID (ya eliminadas). Corregido en `RutinaService.actualizarSeriesDeRutina`: se resuelven `plantillaId` y repeticiones **antes** de borrar; después se re-añaden todas (existentes + nuevas). Ver [CHANGELOG_RUTINAS_SERIES_FEB2026.md](CHANGELOG_RUTINAS_SERIES_FEB2026.md).
- **Tabs del dashboard:** parámetro `?tab=series|rutinas|asignaciones` para volver al tab correcto tras guardar.
- **Logo:** referencias unificadas a `logo matt.jpeg` en navbar y vistas profesor.

### 2.7 Cambios recientes (09/02/2026)

- **Calendario semanal:** encabezados con día + número (ej. Lunes 09), mes visible arriba a la derecha y horario extendido hasta 20–21.
- **Asistencia rápida en tabla:** botón “Dar presente” en el panel, con estado visual (outline/verde) y sin redirección; solo para presenciales.
- **Acciones en alumnos:** columna “Presente” separada; botones compactos (Ver/Editar/Borrar/Asignar).
- **Inactivos:** “Asignar rutina” y “Dar presente” quedan deshabilitados con tooltip “Alumno inactivo”.
- **Filtros persistentes:** se guardan en localStorage y se restauran al volver al panel.
- **Ficha de alumno:** scroll asegurado para ver historial y rutinas completas.
- **Tipo de asistencia:** agregado **Semipresencial**, “Online” renombrado a **Virtual**, horarios visibles en presencial/semipresencial.
- **Editar alumno:** reorden de campos (estado arriba a la derecha), colores pastel según estado, sin fechas alta/baja.
- **Asignar rutina:** tarjetas con botón “ver” (icono ojo) en nueva pestaña.
- **WhatsApp rutinas:** botón en ficha con enlace al celular del alumno y link de rutina.
- **Seeds:** script SQL con 30 alumnos variados en `scripts/seed_alumnos_mattfuncional.sql`.

### 2.8 Grupos musculares como entidad (implementado – Feb 2026)
- **Objetivo:** Que el profesor pueda crear grupos musculares propios y usarlos en ejercicios/series/filtros.
- **Implementado:** Sustitución del enum `MuscleGroup` por la **entidad `GrupoMuscular`** (id, nombre, profesor_id nullable). Los 6 grupos del sistema (BRAZOS, PIERNAS, PECHO, ESPALDA, CARDIO, ELONGACION) se crean al arranque en `DataInitializer`. Exercise usa `@ManyToMany Set<GrupoMuscular>` con tabla `exercise_grupos`. ABM en panel profesor: `/profesor/mis-grupos-musculares` (listar, crear, editar, eliminar solo grupos propios). Filtros y formularios de ejercicios/series usan `gruposMusculares` y `grupoIds`. Export/Import por nombre de grupo.
- **Documentación:** [PLAN_GRUPOS_MUSCULARES_ENTIDAD.md](PLAN_GRUPOS_MUSCULARES_ENTIDAD.md) (estado IMPLEMENTADO y sección 9 con detalle). [CHANGELOG_GRUPOS_MUSCULARES_FEB2026.md](CHANGELOG_GRUPOS_MUSCULARES_FEB2026.md).

### 2.9 Cambios recientes (panel ejercicios y hoja pública)

- **Panel Mis Ejercicios:** ejercicios predeterminados editables y eliminables; indicador “predeterminado” solo con estrellita azul + leyenda; botones Ver (modal)/Editar/Eliminar para todos; Ver abre modal/overlay en la misma página (fondo gris, clic fuera cierra).
- **Hoja de rutina pública:** acceso sin login a `/rutinas/hoja/**` en SecurityConfig (`permitAll`), para que el enlace compartido (p. ej. desde celular con IP local) abra la rutina sin pedir logueo.
- **Ficha alumno (seed):** carga con horarios de asistencia para que alumnos cargados por script muestren la ficha completa; ver [CHANGELOG_EJERCICIOS_Y_HOJA_PUBLICA_FEB2026.md](CHANGELOG_EJERCICIOS_Y_HOJA_PUBLICA_FEB2026.md).

### 2.10 Modal de progreso unificado (Feb 2026)

- **Checkbox presente:** En el modal de progreso se usa `asistenciaHoy.isPresente()` para que quede tildado cuando ya hay registro de presente ese día.
- **Grupos musculares:** Select múltiple reemplazado por checkboxes para elegir varios; en historial se muestran separados por coma.
- **Observaciones:** Columna ampliada a 2000 caracteres (script `scripts/alter_asistencia_observaciones.sql`); formularios con `maxlength="2000"`.
- **Un solo flujo:** El botón "Progreso" en la tabla del panel redirige a la ficha del alumno con `?openModal=progreso`; el modal se abre allí con todos los datos. Eliminado el modal duplicado del dashboard. Ver [CHANGELOG_MODAL_PROGRESO_FEB2026.md](CHANGELOG_MODAL_PROGRESO_FEB2026.md).

---

## 3. Estado del plan general (según PLAN_MODIFICACIONES_MATTFUNCIONAL.md)

### 3.1 Hecho

| Ítem | Estado |
|------|--------|
| Optimización ejercicios predeterminados | Hecho: imágenes solo en `uploads/ejercicios/`, nombres 1–60, sin botón, auto-asegurar al abrir lista. |
| ABM de ejercicios en panel profesor | Ya existía; se mantiene. |
| Referencia “Cargar predeterminados” en plan | Actualizado conceptualmente: ya no es un botón manual, sino asegurar si no existen. |
| Fase 1 – Limpieza | Hecho: un único rol ADMIN, sin panel alumno ni registro, sin panel admin separado, navbar y security ajustados. |
| Fase 2 – Un solo profesor | Hecho: usuario único ADMIN creado en arranque y login redirigido al panel. |
| Fase 3 – Ejercicios y series | Hecho: ABM ejercicios + ABM series en panel profesor, predeterminados auto‑asegurados. |
| Fase 4 – Rutinas y asignación | Hecho: ABM rutinas basadas en series (crear, editar, fix guardado), asignación rutina → alumno, token, hoja `/rutinas/hoja/{token}`, Copiar enlace, WhatsApp desde ficha alumno. |

### 3.2 Por hacer (resumen del plan)

- [x] **Fase 1 – Limpieza:** Renombrar proyecto a Mattfuncional. Eliminar panel admin, chat, WebSocket, ABM de profesores y lógica de múltiples profesores. Ajustar SecurityConfig y navbar.
- [x] **Fase 2 – Un solo profesor:** Profesor único en arranque. Redirigir login al panel profesor. Quitar referencias a admin y lista de profesores.
- [x] **Fase 3 – Ejercicios y series:** Ejercicios con “asegurar predeterminados” (hecho). Mantener ABM de ejercicios y ABM de series en panel profesor.
- [x] **Fase 4 – Rutinas y asignación:** Completado: ABM rutinas basadas en series, asignación rutina → alumno, enlace único (token), hoja de rutina, Copiar enlace, WhatsApp, modificar rutina con series, tabs dashboard, logo.
- [ ] **Fase 5 – Vista rutina por enlace:** Página pública (sin login) con token/enlace que muestre la rutina en HTML. (Hoja en `/rutinas/hoja/{token}` implementada; opcional: permitAll en SecurityConfig para acceso anónimo.)
- [ ] **Fase 6 – Alumnos sin login:** Alumno como ficha (física + online), sin usuario/contraseña. Calendario semanal y presentismo.
- [ ] **Fase 7 – Pantalla de sala:** Modo sala para TV, ruta de solo lectura, control desde panel profesor.
- [ ] **Fase 8 – Página pública:** Sitio institucional (presentación, servicios, horarios, contacto, promociones, productos).

### 3.3 Checklist rápido (actualizado)

- [ ] Renombrar app a **Mattfuncional** (pom, títulos, documentación).
- [x] Un único **panel: profesor** (no admin, no alumno).
- [x] **Ejercicios:** Predeterminados asegurados automáticamente desde `uploads/ejercicios/` (1–60); ABM de ejercicios en panel profesor.
- [x] **Series y rutinas:** ABM y rutinas basadas en series.
- [ ] **Alumnos:** solo ficha (física + online), sin usuario/contraseña.
- [x] **Asignación de rutinas** + **enlace para WhatsApp** + **vista HTML de rutina** (Fase 4 completada; hoja en `/rutinas/hoja/{token}`).
- [ ] **Calendario semanal** y presentismo.
- [ ] **Pantalla de entrenamiento en sala** (modo TV, control desde panel).
- [ ] **Página pública:** presentación, servicios, horarios, contacto, promociones, productos.
- [ ] Eliminar: panel alumno, chat, panel admin, creación de profesores, WebSocket, login alumno.

---

## 4. Próximos pasos sugeridos

1. **Commit actual:** Incluir cambios de ejercicios predeterminados + este documento y la documentación relacionada (ver sección 2.2).
2. **Siguiente fase:** Seguir con Fase 1 (limpieza) o Fase 3 (series) según prioridad; el plan detallado está en [PLAN_MODIFICACIONES_MATTFUNCIONAL.md](PLAN_MODIFICACIONES_MATTFUNCIONAL.md).

---

## 5. Pendiente inmediato (próxima sesión)

- **Ficha del detalle de alumno:** Modificar y mejorar la vista de detalle del alumno; hay margen para mejorar bastante (organización, legibilidad, accesos rápidos, historial y progreso más claros, etc.).

---

*Este archivo se puede ir actualizando en cada iteración para reflejar nuevos cambios y tachar ítems del checklist.*
