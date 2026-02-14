# Changelog – Ejercicios (panel) y hoja de rutina pública (Febrero 2026)

Documentación de los últimos cambios: panel de ejercicios (Mis Ejercicios), vista “Ver” en modal y acceso público al enlace de la rutina.

---

## 1. Panel de ejercicios (Mis Ejercicios)

### 1.1 Ejercicios predeterminados editables y eliminables
- **Antes:** Los ejercicios predeterminados se mostraban como “Solo lectura” y no tenían botones Editar/Eliminar.
- **Ahora:** Todos los ejercicios (predeterminados y propios) pueden **editarse** y **eliminarse** desde el panel.
- **Código:** En `ProfesorController.eliminarEjercicioProfesor` se quitó la validación que impedía eliminar si `ejercicio.isPredeterminado()`. Solo se comprueba que el ejercicio pertenezca al profesor cuando tiene profesor asignado.
- Tarjeta de estadísticas: el subtítulo de “Predeterminados” pasó de “Solo lectura” a **“Editables”**.

### 1.2 Indicador de predeterminado
- **Antes:** Badge con texto “Predeterminado” y estrella junto al nombre de cada ejercicio.
- **Ahora:** Solo una **estrellita azul** (`fa-star text-primary`) junto al nombre. Arriba de la tabla se añadió una **leyenda**: “La estrellita azul indica ejercicios predeterminados del sistema.”

### 1.3 Botones de acción (Ver, Editar, Eliminar)
- Estilo unificado para todos los ejercicios:
  - **Ver:** botón azul (btn-primary) con ícono ojo y texto “Ver”.
  - **Editar:** botón amarillo (btn-warning) con ícono lápiz y texto “Editar”.
  - **Eliminar:** botón rojo (btn-danger) solo con ícono papelera.
- Contenedor flex (`d-flex flex-nowrap justify-content-center gap-1`) para que no se superpongan los iconos.

### 1.4 Ver ejercicio en modal (overlay)
- **Antes:** El botón “Ver” llevaba a una página nueva (`/profesor/mis-ejercicios/ver/{id}`).
- **Ahora:** Al hacer clic en “Ver” se abre un **modal/overlay en la misma página** (como en la hoja de rutina al hacer clic en un ejercicio):
  - Fondo oscurecido (rgba(0,0,0,0.65)).
  - Ventana central con imagen del ejercicio (grande), grupo muscular, nombre y descripción.
  - **Cerrar:** clic fuera del modal o tecla Escape.
- Implementación en `ejercicios-lista.html`: datos del ejercicio en `data-*` en cada fila; botón “Ver” es un `<button>` que abre un overlay y modal creados por JavaScript; no se navega a otra URL.
- La ruta `/profesor/mis-ejercicios/ver/{id}` y la plantilla `profesor/ver-ejercicio.html` se mantienen por si se quieren usar más adelante (enlace directo o nueva pestaña).

---

## 2. Hoja de rutina accesible sin login

### 2.1 Problema
- Al compartir el enlace de la rutina (reemplazando localhost por la IP de la PC para abrirlo desde el celular), la aplicación pedía **loguearse**.
- La hoja de rutina (`/rutinas/hoja/{token}`) debe ser **pública**: cualquiera con el enlace puede verla sin tener cuenta.

### 2.2 Solución
- En **SecurityConfig** se añadió una regla **antes** de la que exige rol ADMIN para `/rutinas/**`:
  - **`.requestMatchers("/rutinas/hoja/**").permitAll()`**
- Con esto, `/rutinas/hoja/{token}` es accesible sin autenticación; el resto de rutas bajo `/rutinas/**` (crear, editar, listar, etc.) siguen requiriendo ADMIN.

---

## 3. Archivos modificados (resumen)

| Archivo | Cambio |
|--------|--------|
| **SecurityConfig.java** | `permitAll()` para `/rutinas/hoja/**`. |
| **ProfesorController.java** | Eliminar restricción de eliminación de ejercicios predeterminados; GET `/mis-ejercicios/ver/{id}` (opcional, para vista en página). |
| **profesor/ejercicios-lista.html** | Leyenda estrella; estrellita azul en nombre; botones Ver (modal)/Editar/Eliminar para todos; contenedor flex en Acciones; overlay/modal con JS para “Ver”. |
| **profesor/ver-ejercicio.html** | Creado para vista “Ver” en página (opcional). |

---

## 4. Cambio previo relacionado (ficha alumno)

- **Ficha completa para alumnos del seed:** En `UsuarioService` se añadió `getUsuarioByIdParaFicha(id)` que carga el alumno con `findByIdWithAllRelations` e inicializa `diasHorariosAsistencia` con `Hibernate.initialize`, para que la ficha muestre también “Horarios de Asistencia”. En la entidad `Usuario` se definió `@CollectionTable` y `@AttributeOverrides` para la tabla `usuario_dias_horarios_asistencia` y columnas del embeddable, alineado con el script `seed_alumnos_mattfuncional.sql`.

---

*Documento para commit – Febrero 2026.*
