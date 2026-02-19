# Ayuda memoria – Cosas a mejorar

**Para contexto del proyecto (sobre todo desde otra PC):** [LEEME_PRIMERO.md](LEEME_PRIMERO.md).

Lista de mejoras pendientes para implementar después. Se van agregando aquí para no olvidarlas.

---

## Pendientes

### Creación de rutina – Orden de las series

- **Qué falta:** En la creación/edición de rutina, poder **cambiar el orden** de las series (reordenar). Es decir, que el usuario pueda definir en qué posición aparece cada serie dentro de la rutina (subir/bajar, arrastrar y soltar, etc.) y que ese orden se guarde y se refleje al ver la rutina.
- **Estado:** **Implementado.** En crear rutina: lista "Series seleccionadas" con botones Subir/Bajar para reordenar antes de guardar. En editar rutina: botones Subir/Bajar en cada serie. El campo `orden` en `Serie` se persiste; la hoja de rutina y la vista al editar muestran las series en ese orden.

### Calendario semanal – Dar presente / falta desde el calendario

- **Qué falta:** Poder **dar el presente** (o marcar falta) al usuario directamente desde el calendario semanal. En cada celda/slot donde aparece un alumno, mostrar un indicador visual: **punto verde** cuando tiene el presente dado ese día y **punto rojo** cuando faltó. Incluir la acción para marcar presente o falta desde el propio calendario (clic en el alumno o en un botón del slot).
- **Estado:** **Implementado.** Puntos verde/rojo/gris por alumno y slot; clic en el punto alterna presente/ausente vía API. Vista Mis Alumnos unificada: columna Presente con fecha, tres estados (Pendiente/Ausente/Presente), mismo endpoint. Ver `Documentacion/CAMBIOS-ASISTENCIA-CALENDARIO-Y-VISTA-ALUMNOS.md`.

### Calendario semanal – Acceso al detalle del usuario desde el botón del usuario

- **Qué falta:** Desde el **botón del usuario** (el recuadro verde con el nombre del alumno en cada celda del calendario semanal), poder **ingresar al detalle del usuario** (ficha del alumno). Es decir, que al hacer clic en el nombre del alumno en el calendario se abra o redirija a la vista de detalle de ese alumno (`/profesor/alumnos/{id}`).
- **Estado:** **Implementado (backend).** Falta mejorar el frontend al final.

### Calendario semanal – Día por excepción para un alumno (recuperar clase)

- **Qué falta:** Definir un **método** para que el profesor pueda **agregar a un alumno un día por excepción** en el calendario semanal. Por ejemplo, cuando el alumno tiene que **recuperar una clase** en un día/horario que no es su horario habitual. Es decir: asignar temporalmente a un alumno a un slot concreto (día + hora) sin que forme parte de su asistencia habitual, y que se refleje en el calendario.
- **Estado:** **Implementado (backend).** Falta mejorar el frontend al final.

### Asistencia – Marcar ausente automáticamente si pasó el horario sin presente

- **Qué falta:** Una vez **pasado el día y el horario** de un slot de asistencia, si el alumno **no tiene dado el presente**, debería quedar registrado como **ausente** de forma automática. Hoy solo se registra cuando el profesor da el presente; no hay registro explícito de “ausente” cuando no se marcó presente después de que pasó la hora.
- **Consideraciones técnicas:** Ver si hace falta:
  - **Métodos nuevos** en servicio de asistencia (por ejemplo: “registrar ausentes para día/hora ya pasados”).
  - Un **proceso programado** (cron/scheduled) que, por ejemplo cada noche o al inicio del día, revise los slots ya pasados y cree registros de ausente para quienes no tienen presente.
  - O bien calcular “ausente” al **mostrar** el calendario/historial (sin guardar registro), comparando día/hora del slot con la fecha actual y la existencia de registro de asistencia.
  - Definir modelo de datos si hace falta un estado explícito “ausente” (o si se infiere por “no hay registro de presente”).
- **Estado:** **Implementado (backend, on-demand).** Falta mejorar frontend al final. Opcional a futuro: cron nocturno.

---

*Se irán sumando más ítems a este archivo.*
