# Avances del app – Mattfuncional

**Última actualización:** Febrero 2026  
**Uso:** Referencia única de todo lo implementado hasta la fecha.

---

## 1. Resumen general

- **Proyecto:** Mattfuncional (evolución de MiGym).
- **Panel único:** Profesor (no hay panel alumno ni admin con acceso).
- Este documento concentra **todos los avances**; los planes de desarrollo y pendientes están en [PLAN_DE_DESARROLLO_UNIFICADO.md](PLAN_DE_DESARROLLO_UNIFICADO.md). La lista de mejoras concretas (por implementar / implementado) está en [AYUDA_MEMORIA.md](AYUDA_MEMORIA.md).

---

## 2. Ejercicios predeterminados

- **Objetivo:** Una sola fuente de imágenes en `uploads/ejercicios/` (nombres `1.webp` … `60.webp` o `.gif`). Sin botón “Cargar predeterminados”: el sistema **asegura los 60 ejercicios** si no existen al abrir “Ver ejercicios”.
- **Archivos clave:** `ExerciseCargaDefaultOptimizado.asegurarEjerciciosPredeterminados()`, `ImagenServicio.registrarArchivoExistente`, `ExerciseRepository.findByNameAndProfesorIsNull`, `ProfesorController` GET mis-ejercicios.
- **Comportamiento:** Al entrar en Mis ejercicios se ejecuta el asegurar; si faltan ejercicios se crean con metadata fija y imagen desde `uploads/ejercicios/N.webp` o imagen por defecto.

---

## 3. Alumnos

- **Formulario:** Sin contraseña; agregado **celular**.
- **Estado del alumno:** ACTIVO/INACTIVO, fecha de alta/baja, historial de estado.
- **Vista de alumnos (panel):** Columnas estado y celular; **filtros** por nombre, estado, tipo (presencial/virtual/semipresencial), día y horario. Filtros persistentes en localStorage.
- **Acciones:** Ver, Editar, Borrar, Asignar rutina; columna “Presente” separada (ver sección Asistencia). Inactivos: “Asignar rutina” y “Dar presente” deshabilitados con tooltip.
- **Editar alumno:** Estado arriba a la derecha, colores pastel según estado.
- **Seeds:** Script SQL con alumnos variados en `scripts/seed_alumnos_mattfuncional.sql`.

---

## 4. Rutinas, series y asignación

- **Enlace público con token:** Hoja de rutina en `/rutinas/hoja/{token}`. Acceso sin login (`permitAll` en SecurityConfig) para enlace compartido (p. ej. celular).
- **Copiar enlace** en hoja de rutina y en ficha del alumno (tabla de rutinas asignadas).
- **WhatsApp:** Botón en ficha del alumno que abre chat con el celular del alumno y el enlace de la rutina listo.
- **Botones “Ver”** de rutinas abren en nueva pestaña; en asignar rutina las tarjetas tienen botón “ver” (ojo).
- **Ficha del alumno:** Historial de asistencia (solo registro); “Dar presente” en tabla de alumnos; rutinas asignadas en tabla compacta + “Asignar rutina”; sin estado “terminada/en proceso”.
- **Creación de rutinas:** Botón “Crear serie” en selector de series; botón “Modificar” en cada tarjeta de serie (carga ejercicios al editar).
- **Modificar rutina:** Título “Modificar Rutina”; carga con `findByIdWithSeries`; tres bloques (Detalles, Series en esta Rutina, Añadir más Series); selección por clic en tarjeta (resaltado amarillo); vista previa “Se agregarán al guardar”. **Fix guardado:** `RutinaService.actualizarSeriesDeRutina` resuelve plantillaId y repeticiones antes de borrar y re-añade todas las series.
- **Orden de series en rutina:** En crear rutina: lista “Series seleccionadas” con Subir/Bajar; en editar: Subir/Bajar por serie. Campo `orden` en `Serie` persistido; hoja y vista al editar muestran ese orden.
- **Vista de series:** Botón “Ver serie”, vista `series/verSerie.html`; sin columna ID en tabla del dashboard.
- **Tabs del dashboard:** Parámetro `?tab=series|rutinas|asignaciones` para volver al tab correcto tras guardar.
- **Logo:** Referencias unificadas a `logo matt.jpeg` en navbar y vistas profesor.

---

## 5. Calendario semanal

- **Vista:** Encabezados con día + número (ej. Lunes 09), mes visible, horario extendido hasta 20–21.
- **Asistencia desde el calendario (implementado):**
  - En cada celda, junto a cada alumno: **punto verde** (presente), **rojo** (ausente), **gris** (pendiente).
  - **Clic en el punto** alterna presente/ausente vía API; la vista se actualiza sin recargar.
  - Al **abrir el calendario** se ejecuta `registrarAusentesParaSlotsPasados`: para cada slot ya pasado de la semana se crea registro “ausente” solo si no existe ninguno (no se sobrescribe un presente ya guardado).
  - Endpoint único: `POST /calendario/api/marcar-asistencia` (usuarioId, fecha, presente). Estado por slot en DTO (`presentePorUsuarioId`); carga con JOIN FETCH para que los colores persistan al recargar.
- **Detalle:** Ver `docs/CAMBIOS-ASISTENCIA-CALENDARIO-Y-VISTA-ALUMNOS.md`.

---

## 6. Asistencia – Vista Mis Alumnos (columna Presente)

- **Título de columna:** “Presente (dd/MM/yyyy)” con la fecha del día actual.
- **Botones solo para quienes asisten hoy:** Se muestra botón solo si el alumno es **presencial** y tiene al menos un horario de asistencia para el **día de la semana actual** (ej. martes → debe tener `DiaHorarioAsistencia` con día MARTES). El resto de celdas quedan en blanco.
- **Tres estados:** Gris = Pendiente, Rojo = Ausente, Verde = Presente. Clic en el botón alterna el estado y llama al **mismo endpoint** que el calendario; los cambios se reflejan en calendario y viceversa.
- **Corrección:** “Presente” solo se muestra cuando el registro de asistencia tiene `presente == true` (antes se consideraba presente si existía cualquier registro ese día).

---

## 7. Grupos musculares como entidad

- **Objetivo:** El profesor puede crear grupos musculares propios y usarlos en ejercicios/series/filtros.
- **Implementado:** Sustitución del enum `MuscleGroup` por la entidad `GrupoMuscular` (id, nombre, profesor_id nullable). Seis grupos del sistema (BRAZOS, PIERNAS, PECHO, ESPALDA, CARDIO, ELONGACION) se crean al arranque en `DataInitializer`. Exercise usa `@ManyToMany Set<GrupoMuscular>` con tabla `exercise_grupos`. ABM en panel: `/profesor/mis-grupos-musculares` (listar, crear, editar, eliminar solo grupos propios). Filtros y formularios usan `gruposMusculares` y `grupoIds`. Export/Import por nombre de grupo.
- **Detalle:** [PLAN_GRUPOS_MUSCULARES_ENTIDAD.md](PLAN_GRUPOS_MUSCULARES_ENTIDAD.md) (sección 9 implementación).

---

## 8. Panel Mis Ejercicios y hoja pública

- **Mis Ejercicios:** Predeterminados editables y eliminables; indicador “predeterminado” (estrellita azul + leyenda); botones Ver (modal)/Editar/Eliminar; Ver abre modal en la misma página (fondo gris, clic fuera cierra).
- **Hoja de rutina pública:** Acceso sin login a `/rutinas/hoja/**` para enlace compartido.
- **Ficha alumno (seed):** Carga con horarios de asistencia para que alumnos del script muestren ficha completa.

---

## 9. Modal de progreso unificado

- **Checkbox presente:** En el modal de progreso se usa `asistenciaHoy.isPresente()` para que quede tildado cuando ya hay registro de presente ese día.
- **Grupos musculares:** Select múltiple reemplazado por checkboxes; en historial se muestran separados por coma.
- **Observaciones:** Columna ampliada a 2000 caracteres; formularios con `maxlength="2000"`.
- **Un solo flujo:** El botón “Progreso” en la tabla del panel redirige a la ficha del alumno con `?openModal=progreso`; el modal se abre allí. Eliminado el modal duplicado del dashboard.

---

## 10. Tipo de asistencia y ficha

- **Tipos:** Presencial, Virtual (antes “Online”), Semipresencial. Horarios visibles en presencial/semipresencial.
- **Ficha de alumno:** Scroll asegurado para historial y rutinas completas.

---

## 11. Referencias a documentación detallada

| Tema | Documento |
|------|-----------|
| Asistencia calendario + vista alumnos | `docs/CAMBIOS-ASISTENCIA-CALENDARIO-Y-VISTA-ALUMNOS.md` |
| Ejercicios predeterminados (imágenes 1–60) | `Documentacion/OPTIMIZACION_EJERCICIOS_PREDETERMINADOS.md` |
| Grupos musculares (plan e implementación) | `Documentacion/PLAN_GRUPOS_MUSCULARES_ENTIDAD.md` |
| Changelog detallado Feb 2026 (rutinas, series, progreso, ejercicios, grupos, alumno inactivo) | `Documentacion/CHANGELOG_UNIFICADO_FEB2026.md` |

---

*Todos los avances del app quedan registrados en este archivo. Para pendientes y próximos pasos, ver AYUDA_MEMORIA.md y PLAN_DE_DESARROLLO_UNIFICADO.md.*
