# Ayuda memoria – Contenido unificado

**El contenido de este archivo se unificó con el plan de desarrollo.**

Para ver la lista de mejoras pendientes e implementadas (ítem por ítem), el checklist y los pendientes detallados, consultá:

**[PLAN_DE_DESARROLLO_UNIFICADO.md](PLAN_DE_DESARROLLO_UNIFICADO.md)** – Sección 9: Pendientes detallados (ítem por ítem).

---

## Para mañana (lista rápida) – ✅ Completado Feb 2026

1. ~~**Formulario crear alumno – Correo opcional:**~~ ✅ Implementado. Campo correo opcional; script `alter_usuario_correo_nullable.sql`.
2. ~~**Alumno → inactivo:**~~ ✅ Implementado. Al dar de baja, se inactivan todas las rutinas asignadas.
3. ~~**Detalle alumno – Rutinas:**~~ ✅ Implementado. Iconos, reseña con texto truncado, acciones centradas.
4. ~~**Volver al origen tras guardar rutina:**~~ ✅ Implementado. Parámetros `alumnoId` y `returnTab` en editar rutina.
5. ~~**Modal de progreso:**~~ ✅ Implementado. Checkbox asistencia eliminado; asistencia se gestiona en panel/calendario.
6. ~~**Formulario modificar rutina:**~~ ✅ Implementado. Nuevo layout: Series a seleccionar (izq, 2 por fila) | Series seleccionadas (der) | Detalles abajo.
7. ~~**Vista alumnos:**~~ ✅ Implementado. Botón "Crear alumno" en título de Mis Alumnos.
8. ~~**Lista rutinas asignadas:**~~ ✅ Implementado. Textos abreviados, iconos estado, acciones centradas.

---

## Pendientes – Ejercicios y vistas

1. **Mejorar HTML crear y modificar ejercicios:**
   - Dejar los formularios de **crear ejercicio** y **modificar ejercicio** acordes al resto de HTML de creaciones (misma estructura, estilos y convenciones).
   - Respetar **colores por módulo** según la interpretación ya usada en el proyecto (ej.: revisar qué color tiene asignado "rutina" en los parámetros/tabs con los que se trabaja y aplicar esa lógica; los HTML de ejercicios deben usar los colores del módulo ejercicios).
   - En la **creación de ejercicios**, incluir un **acceso directo** a la creación de grupos musculares (enlace o botón a `/profesor/mis-grupos-musculares/nuevo` o similar).
2. **Mejorar modal que muestra el ejercicio** en las vistas de ejercicios (lista/ver ejercicios): diseño y contenido del modal más claros y alineados con el resto de la interfaz.

---

## Verificar / reparar – Eliminar usuario y rutinas asignadas

- **Problema:** Al eliminar todos los usuarios y luego ir a "Rutinas asignadas", al abrir una rutina aparecía que no se podía ver porque solo se pueden ver rutinas asignadas a usuarios (al no existir el usuario, la rutina quedaba huérfana).
- **Lógica a seguir:** Al **eliminar un usuario**, deben **eliminarse también todas sus rutinas asignadas** (activas e inactivas). Así, si se eliminan todos los usuarios, no debe haber rutinas asignadas.
- **Implementado:** En `UsuarioService.eliminarUsuario` se eliminan las rutinas del alumno con `rutinaService.eliminarRutina(id)` en lugar de solo desasignarlas (antes se hacía `setUsuario(null)`).
- **A futuro (backups/exportación):** Cuando se implemente el sistema de backups y exportación, explicar que si se quiere **mantener el historial del usuario** antes de eliminarlo, debe **exportar su historial** antes de borrarlo (a revisar cuando esté el módulo de backup).

---

*Última actualización: Marzo 2026. AYUDA_MEMORIA y PLAN_DE_DESARROLLO se unificaron en un solo documento.*
