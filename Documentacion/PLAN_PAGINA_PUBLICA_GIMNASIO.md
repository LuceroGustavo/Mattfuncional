# Plan – Página pública del gimnasio (Fase 8)

**Referencia de estilo:** [RedFit](https://redfit.com.ar/) – diseño sencillo y vistoso, imágenes de fondo en carrusel, header oscuro, botón flotante de WhatsApp.  
**Referencia página Planes:** `Pagina de referencia/Plan.htm` – cards de planes con precios, formulario de consulta, sección "Consultanos".  
**Objetivo:** Página de inicio pública (index) para Mattfuncional: promoción del gimnasio, planes, contacto y redes. Quien entra a la app ve primero esta página; el acceso al login del profesor queda por ahora en un ícono/ruedita que después se puede quitar.

### Estado de implementación (Feb 2026)
- **Implementado:** La ruta `/` muestra la landing pública (`index-publica.html`). Hero con carrusel (video + imágenes en `/img/publica/`), navbar flotante con logo, Inicio/Servicios/Contacto e **ícono “Iniciar sesión”** que lleva a `/login`. Sección “Rasgos que nos caracterizan” con 3 columnas, bloque contacto, footer y botón flotante WhatsApp. Estilos en `css/publica.css`. Detalle en `CHANGELOG_UNIFICADO_FEB2026.md` §11.
- **Ajustes posteriores (§11.5):** Video escritorio `video-inicial.mp4` (2:1), móvil `video-movil.mp4` (9:16); poster `fondo-inicial.png`; logo navbar `/img/logo.png` en círculo; hero fijo 100vh sin scroll; módulo hero visible a los 5 s; navbar con textos blancos, logo + “MATTFUNCIONAL” a la izquierda también en móvil; carrusel 6 s entre slides y transición 1,5 s.
- **Pendiente (contenido):** Reemplazar placeholders de WhatsApp e Instagram por datos reales.
- **Implementado (Feb 2026):** Página Planes (`/planes`), administración desde panel (`/profesor/pagina-publica`), backend con entidades `PlanPublico` y `ConfiguracionPaginaPublica`. Navbar: "Servicios" reemplazado por "Planes". Imagen de fondo `contacto 2 .png` en header de Planes (estilo RedFit). Ver §13 en CHANGELOG_UNIFICADO_FEB2026.md.

---

## 0. Nueva fase: Página Planes y administración pública

### 0.1 Cambio en el navbar ✅

- **Reemplazar "Servicios" por "Planes"** en el navbar de la página pública.
- El enlace lleva a `/planes` (nueva ruta pública).

### 0.2 Nueva página Planes (`/planes`) ✅

Estructura inspirada en `Pagina de referencia/Plan.htm`, adaptada a Mattfuncional. Imagen de fondo `contacto 2 .png` detrás del navbar (larga, poco alto).

| Sección | Contenido |
|---------|------------|
| **Planes (cards)** | Tarjetas con nombre del plan, precio, descripción. Por defecto: 4 planes semanales (1 vez/semana, 2 veces, 3 veces, opción libre). El profesor puede agregar más desde el panel. |
| **Servicios** | Resumen de lo que ofrece el gimnasio (entrenamiento personalizado, presencial/virtual, etc.). |
| **Días y horarios** | Texto o lista editable desde el panel (ej. "Lunes a Viernes 7:00–21:00"). |
| **Formulario de consulta** | Nombre, email, mensaje. Envía a endpoint público; opcional: guardar en BD o enviar mail. |

### 0.3 Administración desde el panel (sin developer) ✅

**Objetivo:** El profesor puede cambiar precios, planes, datos de contacto, etc., sin necesidad de que un developer modifique código.

**Ubicación en el panel:** En `/profesor/usuarios-sistema` (Administración de usuarios del sistema) hay:
- Mi perfil
- Crear usuario
- Listado de usuarios
- **Backups y descargas** (pendiente de implementar)

Se agrega una nueva sección: **"Administrar página pública"** (o nombre similar), que puede ir junto a Backups o como card separada.

**Datos editables desde el panel:**

| Dato | Uso | Origen |
|------|-----|--------|
| Teléfono / WhatsApp | Enlaces wa.me, footer, botón flotante | BD o config |
| Instagram | URL del perfil | BD o config |
| Dirección | Footer, sección contacto | BD o config (ya tenemos Aconcagua 17) |
| Días y horarios | Texto en página Planes | BD |
| Planes | Nombre, precio, descripción, veces/semana | BD (entidad `PlanPublico` o similar) |

### 0.4 Modelo de datos para planes

**Entidad `PlanPublico` (o `PlanPaginaPublica`):**
- id, nombre (ej. "1 vez por semana"), descripcion (opcional), precio, vecesPorSemana (1, 2, 3, null=libre), orden (para ordenar las cards), activo.

**Entidad `ConfiguracionPaginaPublica` (o tabla clave-valor):**
- Claves: `whatsapp`, `instagram`, `direccion`, `dias_horarios`, `telefono`.
- Valores: texto libre. Se consultan desde la página pública.

### 0.5 Información solicitada al gimnasio

| Dato | Estado | Uso |
|------|--------|-----|
| Teléfono de contacto | Pendiente | WhatsApp, footer |
| Instagram | Pendiente | Enlaces, footer |
| Dirección | ✅ Aconcagua 17, Ramos Mejía | Footer, Google Maps |
| Días y horarios | Pendiente | Página Planes |
| Tipo de planes | Mensuales, semanales, anuales | Clasificación de planes |
| Planes: veces/semana + precio | Pendiente (4 iniciales: 1x, 2x, 3x, libre) | Cards en /planes |

**Nota:** Se puede empezar el desarrollo con datos placeholder; el gimnasio completa después y se actualizan desde el panel.

---

Ideas tomadas de la página de referencia [redfit.com.ar](https://redfit.com.ar/):

| Aspecto | Qué nos sirve |
|--------|----------------|
| **Hero con carrusel** | Imágenes que van pasando de fondo; texto superpuesto (título + bullets con checkmarks). Si no hay fotos propias, usar imágenes placeholder y avisar para cambiarlas después. |
| **Estilo** | Sencillo y vistoso: header oscuro, tipografía clara, secciones bien separadas, botón flotante de WhatsApp (esquina inferior derecha). |
| **Navegación** | Links claros: Home, Quiénes somos / Servicios, Contacto. En nuestro caso: Inicio, Servicios, Contacto. |
| **Secciones** | Hero → Rasgos/servicios → Sedes o info → Contacto. Nosotros: Hero → Servicios (entrenamiento personalizado, presencial/virtual, etc.) → Contacto + redes. |
| **Footer** | Oscuro, enlaces, copyright. Incluir WhatsApp e Instagram. |

---

## 2. Recopilación de datos (antes o durante el desarrollo)

Reunir para armar contenido real de la página:

| Dato | Uso en la página | Estado |
|------|------------------|--------|
| **Fotos del gimnasio** | Hero (carrusel de fondo), sección servicios o “conocenos”. Si no hay, usar placeholders (avisar al usuario para cambiarlas después). | Por definir |
| **Dirección** | Footer y/o sección “Dónde estamos” / contacto. | Por definir |
| **Teléfono** | Link a WhatsApp (wa.me/549XXXXXXXX) y opcional en footer. | Por definir |
| **Instagram** | URL del perfil (ej. instagram.com/mattfuncional). Botón/link en header o footer. | Por definir |
| **WhatsApp** | Número con código de país para wa.me. Botón flotante + enlace en contacto. | Por definir |
| **Texto “Quiénes somos”** (opcional) | Una o dos frases para la marca. | Por definir |
| **Horarios** (opcional) | Si se muestran en la página pública. | Por definir |

### 2.1 Imágenes para la página (mientras el cliente recolecta las propias)

Para poder arrancar sin fotos del gimnasio, alcanza con **imágenes de la web** (stock, con licencia de uso). Después se reemplazan por las que mande el cliente en la misma carpeta.

| Uso | Cantidad | Formato sugerido | Dónde van |
|-----|----------|------------------|-----------|
| **Carrusel del hero** (fondo que va pasando) | **3 a 5** | Horizontal (landscape), ej. 1920×1080 o 1600×900 px. JPG o WebP. | En la carpeta `src/main/resources/static/img/` con nombres tipo `public-hero-1.jpg`, `public-hero-2.jpg`, … (o `hero1.jpg`, `hero2.jpg`). En el código se referencian como `/img/public-hero-1.jpg`, etc. |
| **Opcional – sección servicios** | 0 a 2 | Si se usan fotos en las cards de servicios, 1 imagen por bloque o 1 compartida. Mismo tamaño aproximado. | Misma carpeta `img/`. |

**Resumen:** Con **3 a 5 imágenes** para el carrusel del hero alcanza para arrancar. Buscá en Unsplash, Pexels o Pixabay términos como “gym”, “fitness”, “entrenamiento”, “pesas”, “functional training” (en horizontal). Cuando el cliente pase las fotos del gimnasio, se reemplazan esos archivos (mismo nombre) o se actualizan las rutas en el HTML.

---

## 3. Puntos a relatar (contenido a destacar)

Mensajes clave para las secciones de la página:

1. **Entrenamiento personalizado**  
   Planes hechos a medida, seguimiento profesional, objetivos personales.

2. **Multi-horarios y asistencia**  
   Múltiples horarios para entrenar; flexibilidad.

3. **Presencial y virtual**  
   Podés entrenar en el gimnasio o de forma virtual (rutinas, seguimiento online).

4. **Rutinas y series personalizadas**  
   Rutinas y series armadas para cada alumno (enlazar con lo que ya hace la app).

5. **Distintos planes**  
   Diferentes opciones de planes o modalidades (texto preparado para cuando se definan).

Estos puntos se llevan a **bloques o cards** en la sección “Servicios” o “Qué ofrecemos”, con íconos o imágenes si hay.

---

## 4. Diseño y estructura propuesta

### 4.1 Estilo general

- **Estética:** Sencilla y vistosa (como la referencia): fondos oscuros en header/footer, hero con impacto visual.
- **Responsive:** Móvil, tablet y desktop (menú hamburguesa en móvil si hace falta).
- **Imágenes de fondo:** Carrusel en el hero (imágenes que van pasando). Si no hay fotos propias, usar placeholders y avisar para reemplazarlas después.

### 4.2 Estructura de la página (index)

| Orden | Sección | Contenido |
|-------|---------|-----------|
| 1 | **Header** | Logo Mattfuncional (clic → inicio), nav: Inicio, Servicios, Contacto. Ícono/ruedita para **acceso al login** (por ahora visible; después se quita). |
| 2 | **Hero** | Carrusel de imágenes de fondo; título + subtítulo o bullets (ej. entrenamiento personalizado, presencial/virtual). Botón “Conocé más” que baje a Servicios. |
| 3 | **Servicios / Qué ofrecemos** | Cards o bloques: entrenamiento personalizado, multi-horarios, presencial y virtual, rutinas y series personalizadas, distintos planes. |
| 4 | **Contacto** | Formulario (nombre, correo, mensaje) + dirección si hay. Enlaces visibles a WhatsApp e Instagram. |
| 5 | **Footer** | Texto breve, año, enlaces a WhatsApp e Instagram. Opcional: dirección. |
| — | **Flotante** | Botón fijo tipo WhatsApp (esquina inferior derecha) como en la referencia. |

### 4.3 Acceso al login (por ahora)

- En la **página index pública** hay un ícono o “ruedita” (ej. en el header) que lleva al **login** del profesor (`/login` o la ruta que use la app).
- Se deja así por ahora; cuando no se quiera mostrar, se oculta o se quita ese elemento.

### 4.4 Ruta y comportamiento al iniciar

- **Al entrar a la app** (raíz `/` o la URL base): se muestra la **página index pública** (landing), no el login.
- El login queda accesible solo mediante el ícono/ruedita (y luego como se decida: por URL directa, etc.).

---

## 5. Tareas técnicas (para implementación)

| # | Tarea | Notas |
|---|--------|--------|
| 1 | Definir ruta pública | Ej. `/` → index público; o `/public`, `/inicio`. Revisar `SecurityConfig` y controlador para que `/` sea público. |
| 2 | Template Thymeleaf | Crear `templates/public/index.html` (o `inicio.html`) con la estructura anterior. |
| 3 | Logo y estáticos | Usar logo existente (`/img/logo.png`); CSS/JS en `static/` o dentro del template. |
| 4 | Carrusel hero | HTML/CSS/JS para imágenes de fondo que rotan; si no hay fotos, placeholders y comentario en código para cambiarlas. |
| 5 | Ícono de login | En el header del index, link a `/login` con ícono (ruedita o engranaje); después se puede ocultar por config o quitarlo. |
| 6 | Formulario de contacto | Campos + `action` hacia endpoint público (ej. `POST /public/contacto`). Opcional en una segunda etapa: guardar en BD o enviar mail. |
| 7 | WhatsApp e Instagram | Enlaces con número y perfil como variables o placeholders; reemplazar por datos reales cuando estén. |
| 8 | Responsive y accesibilidad | Probar en móvil/tablet; menú colapsable si hace falta; contraste y textos legibles. |

---

## 6. Checklist antes de implementar

- [ ] Recopilar: fotos (o decidir placeholders), dirección, teléfono, Instagram, WhatsApp.
- [ ] Confirmar textos cortos para hero y servicios (o dejarlos genéricos para editar después).
- [ ] Revisar en `SecurityConfig` que la ruta de la index pública sea `permitAll`.
- [ ] Decidir si el formulario de contacto solo muestra mensaje de “enviado” o si habrá backend (mail/BD) en esta etapa.

---

## 7. Orden sugerido de implementación

1. **Ruta y controlador**  
   Ruta `/` (o `/public`) que devuelva la vista de la index pública; `SecurityConfig` actualizado.

2. **Template base**  
   Estructura HTML (header, hero, servicios, contacto, footer) con contenido estático y estilo sencillo (referencia RedFit).

3. **Hero con carrusel**  
   Carrusel de imágenes de fondo (placeholders si no hay fotos); avisar en doc o comentario que se cambian después.

4. **Logo, nav y ícono de login**  
   Logo en header, enlaces Inicio/Servicios/Contacto, ícono/ruedita a `/login`.

5. **Sección servicios**  
   Cards con: entrenamiento personalizado, multi-horarios, presencial/virtual, rutinas y series personalizadas, distintos planes.

6. **Contacto y redes**  
   Formulario + enlaces WhatsApp e Instagram; botón flotante de WhatsApp.

7. **Ajustes responsive y contenido final**  
   Reemplazar placeholders por fotos y datos reales cuando estén; revisar en distintos dispositivos.

---

---

## 8. Plan de desarrollo – Página Planes y administración

### Fase A: Backend y modelo de datos (se puede empezar ya)

| # | Tarea | Dependencias |
|---|--------|---------------|
| A1 | Crear entidad `PlanPublico` (id, nombre, descripcion, precio, vecesPorSemana, orden, activo) | Ninguna |
| A2 | Crear entidad/tabla `ConfiguracionPaginaPublica` (clave, valor) para whatsapp, instagram, direccion, dias_horarios, telefono | Ninguna |
| A3 | Crear `PlanPublicoRepository`, `ConfiguracionPaginaPublicaRepository` | A1, A2 |
| A4 | Crear `PlanPublicoService`, `ConfiguracionPaginaPublicaService` | A3 |
| A5 | Seed inicial: 4 planes (1x, 2x, 3x, libre) con precios placeholder | A4 |
| A6 | Seed inicial: config con direccion actual, placeholders para whatsapp/instagram | A4 |

### Fase B: Panel de administración

| # | Tarea | Dependencias |
|---|--------|---------------|
| B1 | En `usuarios-sistema.html`: agregar card "Administrar página pública" (o sección junto a Backups) | Ninguna |
| B2 | Crear `PaginaPublicaController` (o extender existente) con rutas `/profesor/pagina-publica` | A4 |
| B3 | Vista para editar planes: listar, crear, editar, eliminar (orden, nombre, precio, descripción) | B1, B2 |
| B4 | Vista para editar configuración: WhatsApp, Instagram, dirección, días y horarios, teléfono | B1, B2 |

### Fase C: Página pública Planes

| # | Tarea | Dependencias |
|---|--------|---------------|
| C1 | Cambiar navbar: "Servicios" → "Planes", enlace a `/planes` | Ninguna |
| C2 | Crear `planes-publica.html` (o `planes.html`) con estructura de `Plan.htm` | A4 |
| C3 | Sección cards de planes: iterar desde BD con `PlanPublicoService` | C2, A4 |
| C4 | Sección servicios: texto resumen (puede ser estático o desde config) | C2 |
| C5 | Sección días y horarios: texto desde `ConfiguracionPaginaPublica` | C2, A4 |
| C6 | Formulario de consulta: POST a `/public/consulta` (guardar en BD o solo mensaje de éxito) | C2 |
| C7 | `PortalControlador` o `PublicoController`: GET `/planes` que devuelve la vista con datos | C2 |
| C8 | SecurityConfig: permitir `/planes` y `/public/**` sin autenticación | C7 |

### Fase D: Integración y datos reales

| # | Tarea | Dependencias |
|---|--------|---------------|
| D1 | Reemplazar en index-publica y planes los enlaces hardcodeados por datos de `ConfiguracionPaginaPublica` | B4, C2 |
| D2 | El profesor completa WhatsApp, Instagram, días/horarios desde el panel | B4 |
| D3 | El profesor ajusta precios de planes desde el panel | B3 |

### ¿Se puede empezar aunque falten datos?

**Sí.** Se puede arrancar con:
- Planes con precios placeholder (ej. $X, $Y) que el profesor cambia después.
- Config con placeholders para WhatsApp e Instagram.
- Dirección ya tenemos (Aconcagua 17).
- Días y horarios: texto genérico hasta que el gimnasio lo complete.

Orden sugerido para empezar: **A1 → A2 → A3 → A4 → A5 → A6** (backend y seed), luego **C1 → C2 → C7 → C8** (página Planes básica con datos de BD), y después **B1 → B2 → B3 → B4** (panel de administración).

---

*Documento creado Febrero 2026. Para Fase 8 – Página pública del gimnasio. Se irá actualizando con el avance del desarrollo.*
