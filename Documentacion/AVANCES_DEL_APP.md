# Avances del app – Mattfuncional

**Para contexto del proyecto (sobre todo desde otra PC):** [LEEME_PRIMERO.md](LEEME_PRIMERO.md).

**Última actualización:** Febrero 2026  
**Uso:** Referencia única de todo lo implementado hasta la fecha.

---

## 1. Resumen general

- **Proyecto:** Mattfuncional (evolución de MiGym).
- **Panel único:** Profesor (roles DEVELOPER, ADMIN y AYUDANTE; no hay panel alumno ni panel admin separado).
- Este documento concentra **todos los avances**; los planes de desarrollo y pendientes están en [PLAN_DE_DESARROLLO_UNIFICADO.md](PLAN_DE_DESARROLLO_UNIFICADO.md). La lista de mejoras concretas (por implementar / implementado) está en [AYUDA_MEMORIA.md](AYUDA_MEMORIA.md).

---

## 1.1 Usuarios del sistema (roles)

- **DEVELOPER:** super admin con acceso total al sistema; no aparece en selectores de profesor.
- **ADMIN:** gestiona usuarios del sistema (admin/ayudante), puede editar su perfil y contraseñas.
- **AYUDANTE:** acceso a panel profesor, sin panel de usuarios.
- **Panel de usuarios:** edición de nombre/correo/rol/contraseña y bloque de “Mi perfil”.

## 2. Ejercicios predeterminados

- **Objetivo:** Una sola fuente de imágenes en `uploads/ejercicios/` (nombres `1.webp` … `60.webp` o `.gif`). Sin botón “Cargar predeterminados”: el sistema **asegura los 60 ejercicios** si no existen al abrir “Ver ejercicios”.
- **Archivos clave:** `ExerciseCargaDefaultOptimizado.asegurarEjerciciosPredeterminados()`, `ImagenServicio.registrarArchivoExistente`, `ExerciseRepository.findByNameAndProfesorIsNull`, `ProfesorController` GET mis-ejercicios.
- **Comportamiento:** Al entrar en Mis ejercicios se ejecuta el asegurar; si faltan ejercicios se crean con metadata fija y imagen desde `uploads/ejercicios/N.webp` o imagen por defecto.

---

## 3. Alumnos (Fase 6 – sin login)

- **Sin usuario ni contraseña:** El alumno es solo ficha (física + online). No hay login de alumno; el profesor envía rutinas por WhatsApp.
- **Seguridad:** `UsuarioRepository.findByCorreoParaLogin` y `UserDetailsService` excluyen rol ALUMNO; los alumnos nunca pueden autenticarse.
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

**Estado:** Calendario y presentismo cerrados por ahora (Feb 2026).

- **Vista:** Encabezados con día + número (ej. Lunes 09), mes visible, horario extendido hasta 20–21.
- **Acceso:** El botón “Calendario Semanal” en el panel del profesor abre el calendario en **nueva pestaña** (`target="_blank"`).
- **Asistencia desde el calendario:**
  - En cada celda, junto a cada alumno: **punto verde** (presente), **rojo** (ausente), **gris** (pendiente).
  - **Ciclo de 3 estados:** clic en el punto alterna **Pendiente → Presente → Ausente → Pendiente**. Por defecto **no se asume ausente**: todos quedan pendientes hasta que el profesor marque (permite feriados o días sin clase).
  - **No se marcan ausentes automáticamente** al abrir el calendario; se eliminó la llamada a `registrarAusentesParaSlotsPasados`.
  - Endpoint: `POST /calendario/api/marcar-asistencia` (usuarioId, fecha, **estado**: PENDIENTE | PRESENTE | AUSENTE). PENDIENTE elimina el registro; PRESENTE/AUSENTE crean o actualizan.
  - Estado por slot en DTO (`presentePorUsuarioId`); carga con JOIN FETCH para que los colores persistan al recargar.
- **Excepciones por día/hora:** Botón “+” por celda (habilitable con botón superior), modal con alumno + motivo; etiqueta `Ex` junto al nombre. Las marcas de presente/ausente desde excepción se guardan en la misma tabla de asistencia.
- **Sincronización con ficha del alumno:** Al abrir la ficha, el **historial de asistencia** y el **modal “Resumen mensual de asistencias”** cargan datos actualizados del servidor (GET `/profesor/alumnos/{id}/asistencias`), de modo que lo marcado en el calendario (incluido por excepción) se refleja en historial y resumen sin recargar la página.
- **Reglas de marcado:** los puntos no se muestran ni se pueden marcar en fechas futuras.
- **Detalle:** Ver `Documentacion/CAMBIOS-ASISTENCIA-CALENDARIO-Y-VISTA-ALUMNOS.md`.

---

## 6. Asistencia – Vista Mis Alumnos (columna Presente)

- **Título de columna:** “Presente (dd/MM/yyyy)” con la fecha del día actual.
- **Botones solo para quienes asisten hoy:** Se muestra botón solo si el alumno es **presencial** y tiene al menos un horario de asistencia para el **día de la semana actual**. El resto de celdas quedan en blanco.
- **Tres estados (ciclo completo):** Gris = Pendiente, Rojo = Ausente, Verde = Presente. Clic en el botón **cicla** Pendiente → Presente → Ausente → Pendiente; mismo endpoint que el calendario (`estado` PENDIENTE/PRESENTE/AUSENTE). Los cambios se reflejan en calendario y viceversa.
- **Presente** solo se considera cuando existe registro con `presente == true`; sin registro = Pendiente.

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

- **Checkbox presente:** Eliminado del modal; el progreso ya no fuerza presente desde la ficha.
- **Grupos musculares:** Select múltiple reemplazado por checkboxes; en historial se muestran separados por coma.
- **Observaciones:** Columna ampliada a 2000 caracteres; formularios con `maxlength="2000"`.
- **Un solo flujo:** El botón “Progreso” en la tabla del panel redirige a la ficha del alumno con `?openModal=progreso`; el modal se abre allí. Eliminado el modal duplicado del dashboard.

---

## 10. Tipo de asistencia y ficha

- **Tipos:** Presencial, Virtual (antes “Online”), Semipresencial. Horarios visibles en presencial/semipresencial.
- **Ficha de alumno:** Scroll asegurado para historial y rutinas completas.
- **Resumen mensual de asistencias:** Modal con resumen por mes (asistencias/ausencias) y **detalle por día** del mes seleccionado (ordenado por fecha reciente primero).
- **Admin – profesor por registro:** En historial, el admin ve **selector directo** por fila (solo cuando hay trabajo/observaciones) con **guardado automático**.
- **Modal resumen robusto:** Si el endpoint JSON falla, usa el historial ya renderizado como fallback; abrir/cerrar el modal **no borra** la vista del progreso.

---

## 11. Pizarra (Fase 7 – Pantalla de sala)

- **Objetivo:** Reemplazar la pizarra física por una pizarra digital mostrada en TV.
- **Panel profesor:** Lista de pizarras, crear nueva (1-6 columnas), editor con drag and drop de ejercicios.
- **Columnas:** Título editable por columna (ej: nombre de alumno). Ejercicios con peso y repeticiones editables en tarjeta.
- **Vista TV:** URL `/sala/{token}`; fullscreen con F11. Polling cada 15 s para actualizar.
- **Plan detallado:** [FASE_7_PANTALLA_DE_SALA.md](FASE_7_PANTALLA_DE_SALA.md).

---

## 12. Referencias a documentación detallada

| Tema | Documento |
|------|-----------|
| Asistencia calendario + vista alumnos | `Documentacion/CAMBIOS-ASISTENCIA-CALENDARIO-Y-VISTA-ALUMNOS.md` |
| Ejercicios predeterminados (imágenes 1–60) | `Documentacion/OPTIMIZACION_EJERCICIOS_PREDETERMINADOS.md` |
| Grupos musculares (plan e implementación) | `Documentacion/PLAN_GRUPOS_MUSCULARES_ENTIDAD.md` |
| Changelog detallado Feb 2026 (rutinas, series, progreso, ejercicios, grupos, alumno inactivo) | `Documentacion/CHANGELOG_UNIFICADO_FEB2026.md` |
| Fase 7 – Pizarra / Pantalla de sala | `Documentacion/FASE_7_PANTALLA_DE_SALA.md` |

---

*Todos los avances del app quedan registrados en este archivo. Para pendientes y próximos pasos, ver AYUDA_MEMORIA.md y PLAN_DE_DESARROLLO_UNIFICADO.md.*
