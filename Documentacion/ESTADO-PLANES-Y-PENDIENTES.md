# Dónde están los planes y qué falta

**Contexto:** Para entrar en tema del proyecto ver [LEEME_PRIMERO.md](LEEME_PRIMERO.md). Todo lo implementado está en [AVANCES_DEL_APP.md](AVANCES_DEL_APP.md).

---

## Estructura actual (reorganizada)

| Documento | Ruta | Contenido |
|----------|------|-----------|
| **Avances del app** | `Documentacion/AVANCES_DEL_APP.md` | **Todo lo implementado** en un solo archivo. |
| **Plan de desarrollo unificado** | `Documentacion/PLAN_DE_DESARROLLO_UNIFICADO.md` | **Todos los planes en uno:** visión, fases, lo que se elimina, checklist, pendientes. |
| **Ayuda memoria** | `Documentacion/AYUDA_MEMORIA.md` | Lista de mejoras (ítem por ítem, estado por implementar / implementado). |
| **Documentación asistencia** | `Documentacion/CAMBIOS-ASISTENCIA-CALENDARIO-Y-VISTA-ALUMNOS.md` | Detalle técnico de asistencia en calendario y columna Presente. |
| **Plan grupos musculares** | `Documentacion/PLAN_GRUPOS_MUSCULARES_ENTIDAD.md` | Grupos musculares como entidad (ya implementado; referencia). |

Los antiguos `PLAN_DE_DESARROLLO.md` y `PLAN_MODIFICACIONES_MATTFUNCIONAL.md` fueron eliminados; su contenido está en el plan unificado y en avances.

---

## Lo que ya está hecho (y conviene actualizar en AYUDA_MEMORIA)

1. **Calendario semanal – Dar presente / falta desde el calendario**  
   - **Estado en AYUDA_MEMORIA:** "Por implementar".  
   - **Realidad:** Implementado. Puntos verde/rojo/gris, clic para alternar, API única, persistencia y vista Mis Alumnos unificada. Ver `Documentacion/CAMBIOS-ASISTENCIA-CALENDARIO-Y-VISTA-ALUMNOS.md`.

2. **Asistencia – Marcar ausente si pasó el horario sin presente**  
   - **Estado en AYUDA_MEMORIA:** "Por implementar".  
   - **Realidad:** Parcialmente implementado. Al **abrir el calendario** se ejecuta `registrarAusentesParaSlotsPasados`: para cada slot ya pasado de la semana se crea registro "ausente" solo si no existe ninguno (no hay cron; se hace on-demand al cargar la vista).

---

## Lo que falta según AYUDA_MEMORIA

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
| **Fase 6** | Alumnos sin login: alumno solo como ficha (física + online), sin usuario/contraseña. Calendario y presentismo (gran parte ya hecha). | **Implementado (backend).** Falta pulir frontend |
| **Fase 7** | Pantalla de entrenamiento en sala (modo TV, solo lectura, control desde panel). | Pendiente |
| **Fase 8** | Página pública del gimnasio: presentación, servicios, horarios, contacto, promociones, productos. | Pendiente |

### Checklist general (ítems sin marcar)

- [ ] Renombrar app a **Mattfuncional** (pom, títulos, documentación).
- [ ] **Alumnos:** solo ficha (física + online), sin usuario/contraseña. (**Backend OK; frontend pendiente**)
- [ ] **Calendario semanal y presentismo** (marcar como hecho el punto "dar presente/ausente desde calendario y vista alumnos").
- [ ] **Pantalla de entrenamiento en sala** (modo TV, control desde panel).
- [ ] **Página pública:** presentación, servicios, horarios, contacto, promociones, productos.
- [ ] Eliminar: panel alumno, chat, panel admin, creación de profesores, WebSocket, login alumno.

### Pendiente inmediato (próxima sesión) según PLAN_DE_DESARROLLO

- **Ficha de detalle del alumno:** Mejorar vista de detalle (organización, legibilidad, accesos rápidos, historial y progreso más claros).

---

## Resumen: qué falta priorizar

1. **Actualizar AYUDA_MEMORIA.md:** Cambiar a "Implementado" el ítem "Calendario semanal – Dar presente / falta desde el calendario" y anotar que "Asistencia – Marcar ausente automáticamente" está cubierto al abrir el calendario (y opcionalmente cron más adelante).
2. **Calendario – Clic en alumno → ficha:** Implementar que al hacer clic en el nombre del alumno en una celda se abra `/profesor/alumnos/{id}`.
3. **Calendario – Día por excepción:** Diseñar e implementar "recuperar clase" / asignar un slot puntual a un alumno.
4. **Plan general:** Seguir con Fase 5 (acceso anónimo a hoja de rutina si se desea), Fase 6 (alumnos sin login), Fase 7 (pantalla sala), Fase 8 (página pública) y ítems del checklist (renombrar app, eliminar módulos no usados).
5. **Ficha alumno:** Mejoras de detalle según "Pendiente inmediato" del plan.
