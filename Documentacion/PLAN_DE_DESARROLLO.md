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

---

## 3. Estado del plan general (según PLAN_MODIFICACIONES_MATTFUNCIONAL.md)

### 3.1 Hecho

| Ítem | Estado |
|------|--------|
| Optimización ejercicios predeterminados | Hecho: imágenes solo en `uploads/ejercicios/`, nombres 1–60, sin botón, auto-asegurar al abrir lista. |
| ABM de ejercicios en panel profesor | Ya existía; se mantiene. |
| Referencia “Cargar predeterminados” en plan | Actualizado conceptualmente: ya no es un botón manual, sino asegurar si no existen. |
| Fase 1 – Limpieza | Hecho: un único rol ADMIN, sin panel alumno ni registro, sin panel admin separado, navbar y security ajustados. |

### 3.2 Por hacer (resumen del plan)

- [x] **Fase 1 – Limpieza:** Renombrar proyecto a Mattfuncional. Eliminar panel admin, chat, WebSocket, ABM de profesores y lógica de múltiples profesores. Ajustar SecurityConfig y navbar.
- [ ] **Fase 2 – Un solo profesor:** Profesor único en arranque. Redirigir login al panel profesor. Quitar referencias a admin y lista de profesores.
- [ ] **Fase 3 – Ejercicios y series:** Ejercicios con “asegurar predeterminados” (hecho). Mantener ABM de ejercicios y ABM de series en panel profesor.
- [ ] **Fase 4 – Rutinas y asignación:** ABM de rutinas basadas en series. Asignación rutina → alumno. Enlace único por asignación.
- [ ] **Fase 5 – Vista rutina por enlace:** Página pública (sin login) con token/enlace que muestre la rutina en HTML. Enlace para WhatsApp.
- [ ] **Fase 6 – Alumnos sin login:** Alumno como ficha (física + online), sin usuario/contraseña. Calendario semanal y presentismo.
- [ ] **Fase 7 – Pantalla de sala:** Modo sala para TV, ruta de solo lectura, control desde panel profesor.
- [ ] **Fase 8 – Página pública:** Sitio institucional (presentación, servicios, horarios, contacto, promociones, productos).

### 3.3 Checklist rápido (actualizado)

- [ ] Renombrar app a **Mattfuncional** (pom, títulos, documentación).
- [x] Un único **panel: profesor** (no admin, no alumno).
- [x] **Ejercicios:** Predeterminados asegurados automáticamente desde `uploads/ejercicios/` (1–60); ABM de ejercicios en panel profesor.
- [ ] **Series y rutinas:** ABM y rutinas basadas en series.
- [ ] **Alumnos:** solo ficha (física + online), sin usuario/contraseña.
- [ ] **Asignación de rutinas** + **enlace para WhatsApp** + **vista HTML de rutina** sin login.
- [ ] **Calendario semanal** y presentismo.
- [ ] **Pantalla de entrenamiento en sala** (modo TV, control desde panel).
- [ ] **Página pública:** presentación, servicios, horarios, contacto, promociones, productos.
- [ ] Eliminar: panel alumno, chat, panel admin, creación de profesores, WebSocket, login alumno.

---

## 4. Próximos pasos sugeridos

1. **Commit actual:** Incluir cambios de ejercicios predeterminados + este documento y la documentación relacionada (ver sección 2.2).
2. **Siguiente fase:** Seguir con Fase 1 (limpieza) o Fase 3 (series) según prioridad; el plan detallado está en [PLAN_MODIFICACIONES_MATTFUNCIONAL.md](PLAN_MODIFICACIONES_MATTFUNCIONAL.md).

---

*Este archivo se puede ir actualizando en cada iteración para reflejar nuevos cambios y tachar ítems del checklist.*
