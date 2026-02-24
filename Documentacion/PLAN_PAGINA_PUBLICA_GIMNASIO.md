# Plan – Página pública del gimnasio (Fase 8)

**Referencia de estilo:** [RedFit](https://redfit.com.ar/) – diseño sencillo y vistoso, imágenes de fondo en carrusel, header oscuro, botón flotante de WhatsApp.  
**Objetivo:** Página de inicio pública (index) para Mattfuncional: promoción del gimnasio, servicios, contacto y redes. Quien entra a la app ve primero esta página; el acceso al login del profesor queda por ahora en un ícono/ruedita que después se puede quitar.

### Estado de implementación (Feb 2026)
- **Implementado:** La ruta `/` muestra la landing pública (`index-publica.html`). Hero con carrusel (video + imágenes en `/img/publica/`), navbar flotante con logo, Inicio/Servicios/Contacto e **ícono “Iniciar sesión”** que lleva a `/login`. Sección “Rasgos que nos caracterizan” con 3 columnas, bloque contacto, footer y botón flotante WhatsApp. Estilos en `css/publica.css`. Detalle en `CHANGELOG_UNIFICADO_FEB2026.md` §11.
- **Ajustes posteriores (§11.5):** Video escritorio `video-inicial.mp4` (2:1), móvil `video-movil.mp4` (9:16); poster `fondo-inicial.png`; logo navbar `/img/logo.png` en círculo; hero fijo 100vh sin scroll; módulo hero visible a los 5 s; navbar con textos blancos, logo + “MATTFUNCIONAL” a la izquierda también en móvil; carrusel 6 s entre slides y transición 1,5 s.
- **Pendiente (contenido):** Reemplazar placeholders de WhatsApp e Instagram por datos reales; opcional: dirección, horarios, formulario de contacto con backend.

---

## 1. Referencia: RedFit y puntos a tener en cuenta

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

*Documento creado Febrero 2026. Para Fase 8 – Página pública del gimnasio. Se irá actualizando con el avance del desarrollo.*
