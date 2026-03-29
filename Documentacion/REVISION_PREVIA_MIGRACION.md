# Revisión previa a migración – archivos y estructura

Revisión de duplicados y archivos innecesarios. Se eliminaron los archivos indicados en §2; **README.md** y **CHANGELOG.md** se dejan en la raíz (conviene para GitHub y convención).

---

## 1. Estructura actual del proyecto

```
Mattfuncional/
├── pom.xml, README.md, CHANGELOG.md, .gitignore, .gitattributes
├── mvnw, mvnw.cmd, .mvn/
├── mattfuncional          ← script menú servidor (bash)
├── .dockerignore
├── Documentacion/         ← documentación del proyecto (incl. servidor/)
├── scripts/servidor/      ← reset_db_mattfuncional.sh, reset_db_mattfuncional.sql
└── src/
    ├── main/java/... (controladores, servicios, entidades, config, etc.)
    └── main/resources/
        ├── application*.properties
        ├── static/ (css, js, img)
        └── templates/
```

**Documentacion/** (se mantiene íntegra):

- AYUDA_MEMORIA.md  
- DOCUMENTACION_UNIFICADA.md  
- LEEME_PRIMERO.md  
- PENDIENTES_FINALES.md  
- PLAN_DE_DESARROLLO_UNIFICADO.md  
- PLAN_MODIFICACION_VISTAS.md — alineación responsive/paleta con MiGymVirtual; **registro vivo** del trabajo en §4.2.1 (no duplicar en otros .md salvo acuerdo).  
- servidor/DESPLIEGUE-SERVIDOR.md  
- servidor/nginx-detodoya.conf  

No hay duplicados dentro de Documentacion.

---

## 2. Archivos que podrías eliminar o revisar (aviso, no eliminados)

| Archivo / carpeta | Motivo | Recomendación |
|-------------------|--------|----------------|
| **src/main/resources/templates/sala/sala.html.backup** | Copia de respaldo de `sala.html`. La versión en uso es `sala.html`. | **Eliminar** si ya no lo usás (queda solo `sala.html`). |
| **tatus** (en raíz) | Parece un typo de "status"; no es un comando estándar ni un script del proyecto. | **Eliminar** si no tiene uso (o renombrar a `status` si era intencional). |
| **debug.ps1** (raíz) | Script de depuración local (PowerShell). | Opcional: eliminar si no lo usás, o dejar si lo usás para depurar. |
| **debug.bat** (raíz) | Script de depuración local (Windows). | Igual que debug.ps1. |
| **tarea_actual.md** (raíz) | Notas de tarea/commit. | Opcional: eliminar si ya está todo commiteado; si lo usás como recordatorio, dejalo. |

No se encontraron otros `.bak`, `.old` o `.orig` en el repo (salvo el `.backup` de sala).

---

## 3. Carpetas/archivos que no conviene tocar

- **target/** – Generado por Maven; no se versiona (está en .gitignore). No eliminar del repo (ya está ignorado).
- **Documentacion/** – Toda la carpeta se mantiene para la migración y referencia.
- **scripts/servidor/** – Contiene scripts usados en el servidor (reset BD); la documentación los referencia.
- **mattfuncional** (raíz) – Script del menú de gestión en el servidor; necesario para despliegue.

---

## 4. Referencias en documentación a archivos que no están en el repo

En **Documentacion/servidor/DESPLIEGUE-SERVIDOR.md** se mencionan:

- **scripts/servidor/iniciar-menu.sh** – No existe en el repo. En su lugar, en el servidor se usa el script de raíz **`./mattfuncional`** (y sesión `screen -r mattfuncional`). Conviene actualizar la doc para indicar que el menú se ejecuta con `./mattfuncional` desde la raíz del proyecto.
- **limpiar_duplicados_slot_config.sql**, **consultar_duplicados_usuario.sql**, **alter_consulta_email_nullable.sql** – Citados en “Solución de problemas” pero no están en `scripts/servidor/`. Opciones: (a) crearlos y subirlos al repo según lo que describe DESPLIEGUE-SERVIDOR.md, o (b) actualizar la doc indicando que esos scripts se crean bajo demanda cuando haga falta.

En **CHANGELOG** y **tarea_actual.md** se menciona **COMMIT_PENDIENTE.md** y **Documentacion/MANUAL-USUARIO.md** / **AVANCES_DEL_APP.md**; no están en el árbol actual (el manual está como `profesor/manual-usuario.html`). No es necesario “eliminar” nada; solo tener en cuenta que esas rutas en la doc pueden estar desactualizadas.

---

## 5. Resumen de acciones sugeridas (solo después de tu OK)

1. **Eliminar** (si estás de acuerdo):  
   `src/main/resources/templates/sala/sala.html.backup`  
   `tatus` (raíz)

2. **Opcional:**  
   `debug.ps1`, `debug.bat`, `tarea_actual.md` – según si los seguís usando.

3. **No eliminar:**  
   Ningún archivo de Documentacion; ni `mattfuncional`, ni `scripts/servidor/`.

4. **Documentación:**  
   Actualizar DESPLIEGUE-SERVIDOR.md para que el menú sea `./mattfuncional` (y no `iniciar-menu.sh`), y aclarar qué hacer con los SQL de problemas (crearlos o documentar que son bajo demanda). Eso se puede hacer dentro del plan **migrar_servidor_cliente.md**.

Cuando confirmes qué querés borrar, se puede ejecutar solo eso y dejar el resto como está.

---

## 6. Estado: crear y modificar alumno (implementado)

- **Misma vista** `profesor/nuevoalumno.html` para **alta** (`GET/POST /profesor/alumnos/nuevo`) y **edición** (`GET/POST /profesor/alumnos/editar/{id}`), con `editMode` cuando corresponde.
- **Horarios de asistencia:** selector visual (grilla); en móvil/entorno 2 cabeceras **LUN–DOM**, `data-dia` y JSON siguen usando enum (`LUNES`, …). Precarga en edición vía `horariosExistentesJson` + `cargarHorariosExistentes()`.
- **Formulario de alumno:** sin bloque opcional de historial físico en alta/edición; mediciones se registran desde la **ficha del alumno** (`POST /profesor/alumnos/{id}/medicion/nueva`).
- **Enlaces a editar:** desde `profesor/alumno-detalle.html` (botones “Editar alumno” / “Editar alumno completo”).
- **Calendario semanal profesor:** en vista ≤992px, encabezados de día abreviados y columnas equilibradas (`DiaSemana.getAbrevCalendario()` + CSS en `calendario/semanal-profesor.html`).

Para el detalle de vistas MiGymVirtual y seguimiento visual, ver también **PLAN_MODIFICACION_VISTAS.md** (§4.2.1): **ficha del alumno**, **crear/modificar serie**, **crear rutina**, **hoja / ver rutina**, **ver serie**, **Mis ejercicios** (`ejercicios-lista.html`), **grupos musculares** (`grupos-musculares-lista.html`), **login** y panel con footer de marca oculto donde aplica — documentado en ese plan (Mar 2026).

---

## 7. Registro de sesión — 28 mar 2026

### Hecho en código

1. **Asignar rutina** (`templates/profesor/asignar-rutina.html` + `ProfesorController` GET `/profesor/asignar-rutina/{id}`)  
   - Vista alineada a referencia: **tabla** de plantillas, **búsqueda**, **modal** (Ver / Modificar / Seleccionar), responsive móvil.  
   - Modelo **`nombresRutinasAsignadasAlAlumno`** para marcar rutinas ya asignadas por **nombre** (copias con distinto `id`).  
   - Limpieza de logs por consola; uso de **`logger`** en errores.  
   - Detalle técnico: **PLAN_MODIFICACION_VISTAS.md** §4.2.1 (bloque “Vista asignar rutina”).

2. **Ficha del alumno — modal “Detalle de rutina”** (`profesor/alumno-detalle.html`)  
   - Botón **Copiar enlace** en móvil: **`buildFullHojaUrl`**, **`clipboard.writeText` con `.catch` → `fallbackCopy`**, y mensaje si no hay token/enlace público (evita fallos silenciosos en **HTTP/LAN**).

### Pendiente — próxima sesión (29 mar 2026)

- **Módulo de Administración** del panel profesor (`/profesor/administracion` y vistas relacionadas: consultas, página pública, etc.): revisar alineación con MiGymVirtual (responsive, paleta, UX) sin romper comportamiento propio de Mattfuncional.

### Sugerencia de mensaje de commit

```
feat(profesor): asignar rutina con tabla/modal y arreglo copiar enlace en modal alumno

- Reemplazar tarjetas por tabla+búsqueda+modal en asignar-rutina (paridad referencia)
- nombresRutinasAsignadasAlAlumno para detectar plantillas ya asignadas por nombre
- Quitar System.out del GET asignar-rutina; log de errores con logger
- Modal detalle rutina en alumno-detalle: clipboard con fallback y URL unificada
```

*(Ajustá el prefijo `feat` si preferís `fix` solo para el modal, o partí en dos commits si querés historial más fino.)*
