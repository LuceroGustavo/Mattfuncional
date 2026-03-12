# Restricción de acceso a "Administrar sistema" para rol AYUDANTE

**Fecha:** Febrero 2026.

---

## Objetivo

El usuario con rol **AYUDANTE** no debe poder acceder al panel **Administrar sistema**. Debe ver el botón en el dashboard pero, al hacer clic, recibir un mensaje claro; y si intenta entrar por URL directa, ser redirigido al panel con un aviso.

---

## Comportamiento implementado

### 1. En el Panel del Profesor (dashboard)

- **ADMIN y DEVELOPER:** La tarjeta "Administrar sistema" sigue siendo un enlace a `/profesor/administracion`.
- **AYUDANTE (y cualquier otro rol distinto de ADMIN/DEVELOPER):** La tarjeta "Administrar sistema" se muestra con el mismo aspecto, pero al hacer clic **no navega**. Se muestra un mensaje: *"Debe ser usuario administrador para poder ingresar."*

### 2. Acceso directo por URL

- Si un usuario con rol AYUDANTE escribe en el navegador una URL del panel de administración (por ejemplo `http://.../profesor/administracion`):
  - El controlador **no muestra** la página de administración.
  - Redirige al **panel del profesor** del usuario (`/profesor/{id}`).
  - Envía un mensaje flash que se muestra en el dashboard: *"Acceso restringido. Debe ser usuario administrador para poder ingresar a Administrar sistema."* (alert amarillo, cerrable).

### 3. Rutas ya protegidas

Todas las acciones del `AdminPanelController` (backup, exportar/importar, usuarios del sistema, página pública, etc.) siguen comprobando que el usuario sea **ADMIN** o **DEVELOPER**. Un AYUDANTE que intente acceder por URL a cualquiera de esos endpoints es redirigido al dashboard; no puede usar el panel de administración por enlace directo.

---

## Archivos modificados

| Archivo | Cambio |
|---------|--------|
| `AdminPanelController.java` | `panelAdministracion`: recibe `RedirectAttributes`; si el usuario no es ADMIN ni DEVELOPER, redirige a `/profesor/{id}` con flash `mensajeRestriccionAdmin=true`. |
| `profesor/dashboard.html` | Tarjeta "Administrar sistema": condicional por rol. Si ADMIN o DEVELOPER → enlace; si no → `<span>` con mismo estilo que al hacer clic muestra `alert('Debe ser usuario administrador...')`. Bloque de alerta Bootstrap que se muestra cuando existe `mensajeRestriccionAdmin` (acceso por URL bloqueado). |

---

## Cómo probar

1. **Crear/usar un usuario con rol AYUDANTE** (desde Usuarios del sistema, con usuario ADMIN o DEVELOPER).
2. Iniciar sesión con ese usuario y abrir el Panel del Profesor.
3. Hacer clic en la tarjeta **"Administrar sistema"** → debe aparecer el mensaje y no navegar.
4. Escribir en el navegador `http://localhost:8080/profesor/administracion` (o la URL del servidor) → debe redirigir al panel y mostrar el aviso amarillo de acceso restringido.

---

*Documento de referencia para la restricción de acceso al panel de administración por rol.*
