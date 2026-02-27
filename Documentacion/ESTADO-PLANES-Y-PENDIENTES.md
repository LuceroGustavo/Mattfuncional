# Dónde están los planes y qué falta

**Contexto:** Para entrar en tema del proyecto ver [LEEME_PRIMERO.md](LEEME_PRIMERO.md). Todo lo implementado está en [AVANCES_DEL_APP.md](AVANCES_DEL_APP.md).

**Estado página Planes (Feb 2026):** El desarrollo del HTML de la página `/planes` y del formulario de consulta está **terminado**. Incluye: validación en rojo (sin alert), mensaje de éxito con flash, días/horarios multilínea con alineación. Ver [CHANGELOG_UNIFICADO_FEB2026.md](CHANGELOG_UNIFICADO_FEB2026.md) sección 15.

---

## Estructura actual (reorganizada)

| Documento | Ruta | Contenido |
|----------|------|-----------|
| **Avances del app** | `Documentacion/AVANCES_DEL_APP.md` | **Todo lo implementado** en un solo archivo. |
| **Plan de desarrollo unificado** | `Documentacion/PLAN_DE_DESARROLLO_UNIFICADO.md` | **Todos los planes en uno:** visión, fases, checklist y pendientes detallados (ítem por ítem). Unifica plan y ayuda memoria. |
| **Documentación asistencia** | `Documentacion/CAMBIOS-ASISTENCIA-CALENDARIO-Y-VISTA-ALUMNOS.md` | Detalle técnico de asistencia en calendario y columna Presente. |
| **Plan grupos musculares** | `Documentacion/PLAN_GRUPOS_MUSCULARES_ENTIDAD.md` | Grupos musculares como entidad (ya implementado; referencia). |

Los antiguos `PLAN_DE_DESARROLLO.md`, `PLAN_MODIFICACIONES_MATTFUNCIONAL.md` y `AYUDA_MEMORIA.md` fueron unificados en `PLAN_DE_DESARROLLO_UNIFICADO.md`.

---

## Lo que ya está hecho (actualizado en PLAN_DE_DESARROLLO_UNIFICADO)

1. **Calendario semanal – Dar presente / falta desde el calendario**  
   - **Realidad:** Implementado. Puntos verde/rojo/gris, clic para alternar, API única, persistencia y vista Mis Alumnos unificada. Ver `Documentacion/CAMBIOS-ASISTENCIA-CALENDARIO-Y-VISTA-ALUMNOS.md`.

2. **Asistencia – Marcar ausente si pasó el horario sin presente**  
   - **Realidad:** Parcialmente implementado (on-demand al abrir el calendario). Opcional: cron nocturno.

---

## Lo que falta según PLAN_DE_DESARROLLO_UNIFICADO

| Ítem | Descripción | Estado en doc |
|------|-------------|---------------|
| **Calendario – Acceso al detalle del usuario** | Desde el nombre/botón del alumno en cada celda del calendario, ir a la ficha del alumno (`/profesor/alumnos/{id}`). | **Implementado (backend).** Falta pulir frontend |
| **Calendario – Día por excepción (recuperar clase)** | Que el profesor pueda asignar a un alumno un día/horario puntual (ej. recuperar clase) sin que sea su horario habitual; que se vea en el calendario. | **Implementado (backend).** Falta pulir frontend |
| **Asistencia – Ausente automático** | Opcional: proceso programado (cron) que, por ejemplo cada noche, marque ausentes para slots ya pasados sin presente (hoy se hace al abrir el calendario). | **Implementado (backend, on-demand).** Cron opcional |

---

## Lo que falta según PLAN_DE_DESARROLLO y PLAN_MODIFICACIONES

### Fases aún no cerradas

| Fase | Contenido | Estado |
|------|-----------|--------|
| **Fase 5** | Vista rutina por enlace (página pública con token). Hoja en `/rutinas/hoja/{token}` ya existe; falta/opcional: `permitAll` en SecurityConfig para acceso anónimo. | **Implementado (backend).** Falta pulir frontend |
| **Fase 6** | Alumnos sin login: alumno solo como ficha (física + online), sin usuario/contraseña. UserDetailsService excluye ALUMNO del login. | **Completado** |
| **Fase 7** | Pantalla de entrenamiento en sala (modo TV, solo lectura, control desde panel). Pizarra digital con columnas, títulos, peso/reps, agregar/quitar columnas (máx. 6), polling 2,5 s. | **Completada** |
| **Fase 8** | Página pública del gimnasio: presentación, servicios, horarios, contacto, promociones, productos. | **Implementado** (landing estilo RedFit en `/`, login por ícono). **Desarrollo HTML de Planes y formulario: TERMINADO** (ver CHANGELOG sección 15). |

### Checklist general (ítems sin marcar)

- [x] Renombrar app a **Mattfuncional** (pom, títulos, documentación).
- [x] **Alumnos:** solo ficha (física + online), sin usuario/contraseña. (**Completado**)
- [ ] **Calendario semanal y presentismo** (marcar como hecho el punto "dar presente/ausente desde calendario y vista alumnos").
- [x] **Pantalla de entrenamiento en sala** (modo TV, control desde panel). Implementado: pizarra, columnas editables, agregar/quitar columnas, vista TV con polling 2,5 s.
- [x] **Página pública:** presentación, servicios, horarios, contacto (landing en `/`, ícono login; pendiente reemplazar WhatsApp/Instagram por datos reales).
- [ ] **Depuración anual de datos:** Crear método (o proceso) para depurar/archivar datos antiguos (ej. asistencia mayor a 1 año). El plazo anual es adecuado: permite conservar al menos 12 meses para consultas e informes y evita que la base de datos crezca sin control. Ver [ESTIMATIVO_RECURSOS_SERVIDOR.md](ESTIMATIVO_RECURSOS_SERVIDOR.md).
- [ ] **Manual del usuario:** Mantener actualizado (o completar) [MANUAL-USUARIO.md](MANUAL-USUARIO.md) para que refleje todas las funcionalidades actuales: login, panel, alumnos, ejercicios, series, rutinas, calendario, asistencia, **pizarra y sala TV**, usuarios del sistema. Revisar tras cada cambio relevante en la app.
- [x] Eliminar: panel alumno, chat, panel admin, creación de profesores, WebSocket, login alumno.

### Pendiente inmediato (próxima sesión) según PLAN_DE_DESARROLLO

- **Ficha de detalle del alumno:** Mejorar vista de detalle (organización, legibilidad, accesos rápidos, historial y progreso más claros).
- **Manual del usuario:** Actualizar o completar MANUAL-USUARIO.md para que incluya todas las pantallas y flujos actuales (en particular pizarra, sala TV, transmisión en TV).
- **Script en base del servidor:** Ejecutar `scripts/servidor/alter_consulta_email_nullable.sql` en la base de datos del servidor (Donweb) para que el formulario de consulta en `/planes` funcione con solo teléfono (sin email) en producción.
- **Script borrar base entera:** Crear un script que elimine la base de datos completa para que, al reiniciar la app, Hibernate la pueda recrear desde cero (útil para desarrollo o reset total).

---

## Resumen: qué falta priorizar

1. **Calendario – Clic en alumno → ficha:** Implementar que al hacer clic en el nombre del alumno en una celda se abra `/profesor/alumnos/{id}`.
2. **Calendario – Día por excepción:** Diseñar e implementar "recuperar clase" / asignar un slot puntual a un alumno.
3. **Plan general:** Seguir con Fase 5 (acceso anónimo a hoja de rutina si se desea), Fase 7 (pantalla sala), Fase 8 (página pública) y ítems del checklist. Fase 6 (alumnos sin login) completada.
4. **Ficha alumno:** Mejoras de detalle según "Pendiente inmediato" del plan.
