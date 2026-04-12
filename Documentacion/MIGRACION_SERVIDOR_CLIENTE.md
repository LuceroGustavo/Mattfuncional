# Migración al servidor del cliente (Mattfuncional)

Plan operativo para llevar **Mattfuncional** del entorno de referencia (VPS Donweb / **detodoya.com.ar**) al **servidor definitivo del cliente** y publicarla en el dominio **`mattfuncional.com.ar`**.

**Documentación de apoyo (ya en el repo):**

| Documento | Uso |
|-----------|-----|
| [servidor/DESPLIEGUE-SERVIDOR.md](servidor/DESPLIEGUE-SERVIDOR.md) | Despliegue Donweb (SSH, puerto, menú, Nginx, MySQL). **§2.1–§2.6:** SSH sin contraseña (`authorized_keys`), formulario DonWeb, método manual, PowerShell, agente Cursor. |
| **Anexo A** (final de este archivo) | Checklist, comandos de primera instalación, Nginx y mantenimiento (unificado desde el antiguo `migrar_servidor_cliente.md`). |
| [servidor/nginx-detodoya.conf](servidor/nginx-detodoya.conf) | Ejemplo Nginx **actual** (detodoya.com.ar). |
| [servidor/nginx-mattfuncional-com-ar.conf](servidor/nginx-mattfuncional-com-ar.conf) | **Plantilla** para el nuevo dominio (copiar y ajustar certificados tras Certbot). |
| [servidor/nginx-mattfuncional-http-only.conf](servidor/nginx-mattfuncional-http-only.conf) | **Solo HTTP (puerto 80)** → proxy a `:8080`; usar hasta DNS + Certbot (VPS cliente Abr 2026). |
| [LEEME_PRIMERO.md](LEEME_PRIMERO.md) | URLs, acceso a la app. |

**Nombre informal del VPS del cliente:** en conversación se lo llama **mattfuncional** (host / etiqueta en el panel del proveedor). El **dominio público** será **`mattfuncional.com.ar`** (y opcionalmente `www.mattfuncional.com.ar` redirigido al dominio canónico).

---

## 0. Servidor del cliente — datos registrados (panel Dattaweb, Abril 2026)

Información tomada del panel **Software y accesos** del Cloud Server contratado para la migración. **No guardar la contraseña de `root` en el repositorio**; usar gestor de secretos o variables locales.

### Software (pestaña SOFTWARE)

| Campo | Valor |
|--------|--------|
| Plataforma | Linux |
| Sistema operativo | **Ubuntu 24.04** (64 bits) |
| Imagen / stack | Instalación mínima con **UEFI** — `Ubuntu2404-uefi-min` |

### Accesos SSH (pestaña de credenciales del Cloud Server)

| Campo | Valor |
|--------|--------|
| Proveedor | **Dattaweb** (hostname panel: `vps-5861213-x.dattaweb.com`) |
| **IP pública** | `200.58.127.154` |
| Usuario SSH | `root` |
| **Puerto SSH** | **5344** (no estándar; hay que indicarlo siempre con `-p`) |
| Comando típico | `ssh -p 5344 root@200.58.127.154` |
| Alternativa | **Consola VNC** (botón en el panel) si SSH no está disponible |

**Ejemplos con el puerto correcto:**

```bash
ssh -p 5344 root@200.58.127.154
scp -P 5344 archivo local root@200.58.127.154:/ruta/remota/
```

> **DNS:** cuando configures **mattfuncional.com.ar**, el registro **A** debe apuntar a **`200.58.127.154`** (salvo que el proveedor cambie la IP tras un reprovisionamiento; revisar el panel antes del corte).

---

## 1. Qué documenta el servidor anterior (detodoya / Donweb)

Estos datos están detallados en `DESPLIEGUE-SERVIDOR.md`; sirven como **referencia**, no se copian tal cual al cliente.

| Concepto | Servidor de referencia (Donweb) |
|----------|----------------------------------|
| IP (ejemplo) | `149.50.144.53` |
| SSH | `ssh -p 5638 root@149.50.144.53` (Donweb usa a veces puerto distinto de 22) |
| SO | Ubuntu 24.04 |
| App Spring Boot | Puerto **8080** (el menú fuerza `MATT_APP_PORT=8080`) |
| Perfil Spring | `donweb` → `application-donweb.properties` |
| Código en servidor | `/root/mattfuncional` |
| Uploads | `/root/mattfuncional/uploads` (`ejercicios/`, `promociones-publicas/`) |
| Proxy | **Nginx** 80/443 → `http://127.0.0.1:8080` |
| Dominio anterior | **detodoya.com.ar** → mismo VPS; certificado Let's Encrypt |
| Límite subidas Nginx | `client_max_body_size 50M` (backups ZIP) |

Los datos concretos del VPS del cliente están en **§0** y repasados en **§6**.

---

## 2. Objetivos de la migración

1. Tener la app **Mattfuncional** estable en el VPS del cliente.
2. Servir la aplicación en **`https://mattfuncional.com.ar`** (HTTPS recomendado desde el primer día público).
3. Mantener **misma arquitectura** que ya funciona: Java 17, MySQL, Nginx como reverse proxy, perfil `donweb` o equivalente, carpeta de uploads fuera del JAR.
4. **Migrar datos** si aplica: backup ZIP/JSON desde el servidor viejo e import en el nuevo, o volcado MySQL (según acuerdo operativo).

---

## 3. Fases del plan (orden sugerido)

### Fase A — Información y acceso (antes de tocar producción)

- [ ] **A.1** Anotar IP pública del VPS del cliente, **usuario SSH** (ej. `root`) y **puerto SSH** (22 u otro).
- [ ] **A.2** Configurar **clave SSH** en el servidor (`~/.ssh/authorized_keys`) para no depender solo de contraseña (recomendado para despliegues y para uso con Cursor/terminal).
- [ ] **A.3** Confirmar que el dominio **mattfuncional.com.ar** está en el panel del registrante y que podés crear/editar registros **DNS** (registro **A** o **AAAA** hacia la IP del VPS).

### Fase B — DNS hacia el nuevo servidor

- [ ] **B.1** Crear registro **A** `mattfuncional.com.ar` → IP del VPS del cliente.
- [ ] **B.2** (Opcional) Registro **A** `www.mattfuncional.com.ar` → misma IP, o **CNAME** `www` → `mattfuncional.com.ar` (según permita el panel).
- [ ] **B.3** Esperar propagación (minutos a horas). Comprobar con `ping` / `nslookup` desde tu PC.

> **Nota:** Mientras probás solo por IP (`http://IP:8080`) podés avanzar con la app; el dominio solo afecta Nginx, Certbot y enlaces públicos.

### Fase C — Preparar el servidor (Ubuntu)

- [ ] **C.1** `apt update && apt upgrade -y`
- [ ] **C.2** Instalar: `openjdk-17-jdk-headless`, `git`, `mysql-server` (o MariaDB), `nginx`, `certbot`, `python3-certbot-nginx`
- [ ] **C.3** Crear base `mattfuncional` y usuario MySQL con contraseña segura; variables `MATT_DB_USER` y `MATT_DB_PASSWORD` en `~/.bashrc` del usuario que ejecutará la app
- [ ] **C.4** Clonar el repo en una ruta estable, ej. `/root/mattfuncional` (o la que definan con el cliente)
- [ ] **C.5** `chmod +x mattfuncional` (script menú); `mkdir -p .../uploads` (y la app creará subcarpetas)
- [ ] **C.6** Compilar: `./mvnw clean package -DskipTests` y arrancar con `./mattfuncional` (opción 4 o 5 según `DESPLIEGUE-SERVIDOR.md`)
- [ ] **C.7** Verificar local: `curl -I http://127.0.0.1:8080`

### Fase D — Nginx y dominio mattfuncional.com.ar

- [ ] **D.1** Copiar plantilla [servidor/nginx-mattfuncional-com-ar.conf](servidor/nginx-mattfuncional-com-ar.conf) al servidor, por ejemplo:  
  `/etc/nginx/sites-available/mattfuncional.com.ar`  
  Ajustar si usás **solo HTTP** al principio (bloques temporales) o ya **HTTPS** tras Certbot.
- [ ] **D.2** `server_name` debe incluir **`mattfuncional.com.ar`** (y `www` si aplica). `proxy_pass http://127.0.0.1:8080;` igual que en detodoya.
- [ ] **D.3** Incluir **`client_max_body_size 50M;`** en el bloque que hace proxy (backups).
- [ ] **D.4** Activar sitio: `ln -sf ... /etc/nginx/sites-enabled/` → `nginx -t` → `systemctl reload nginx`

### Fase E — HTTPS (Let's Encrypt)

- [ ] **E.1** Cuando el DNS apunte bien al VPS:  
  `certbot --nginx -d mattfuncional.com.ar -d www.mattfuncional.com.ar`  
  (ajustar dominios según lo configurado.)
- [ ] **E.2** Tras Certbot, **revisar** que siga `client_max_body_size 50M` en el bloque `server` que hace `proxy_pass` (Certbot a veces duplica bloques).
- [ ] **E.3** Probar `https://mattfuncional.com.ar` en navegador.

### Fase F — Datos y corte

- [ ] **F.1** Exportar desde el entorno anterior (panel **Administración → Backup** o mysqldump) según lo acordado.
- [ ] **F.2** Importar en el nuevo servidor con la app **parada** o siguiendo el flujo de importación del panel.
- [ ] **F.3** Cambiar contraseñas por defecto (usuario developer, etc.) y comunicar al cliente solo por canal seguro.
- [ ] **F.4** Actualizar **DNS** del dominio viejo o dejar de usarlo si el corte es definitivo (solo si aplica).

---

## 4. Configuración de aplicación en el nuevo servidor

- **Perfil:** seguir **`donweb`** como en `DESPLIEGUE-SERVIDOR.md`, o crear **`application-cliente.properties`** si hace falta otra ruta de uploads/puerto.
- **Uploads:** `MATT_UPLOADS_DIR` o `mattfuncional.uploads.dir` en **ruta absoluta** (ej. `/root/mattfuncional/uploads` o `/opt/mattfuncional/uploads` si estandarizan con `application-prod`).
- **Puerto interno:** debe coincidir con el `proxy_pass` de Nginx (típicamente **8080**).

No hace falta que el nombre del VPS en el panel coincida con el dominio; lo crítico es **DNS + Nginx + app escuchando**.

---

## 5. Capturas y panel del proveedor (rellenar en la migración)

Usá esta sección junto con las capturas que vayas pasando: anotá **dónde** clickeaste y **qué valor** pusiste.

| # | Tema | ¿Hecho? | Notas / captura |
|---|------|---------|-----------------|
| 1 | Listado del VPS (nombre **mattfuncional**, IP) | ☐ | |
| 2 | Reglas de firewall / puertos 22, 80, 443 | ☐ | |
| 3 | DNS: registro A para **mattfuncional.com.ar** | ☐ | |
| 4 | DNS: **www** (si aplica) | ☐ | |
| 5 | Primera conexión SSH exitosa | ☐ | |
| 6 | Clave pública añadida a `authorized_keys` | ☐ | |
| 7 | Nginx: archivo de sitio activo | ☐ | |
| 8 | Certbot emitió certificado | ☐ | |
| 9 | Prueba HTTPS login / planes públicos | ☐ | |

---

## 6. Hoja de datos del servidor del cliente

| Campo | Valor |
|-------|--------|
| Proveedor VPS | **Dattaweb** |
| Hostname panel | `vps-5861213-x.dattaweb.com` |
| IP pública | `200.58.127.154` |
| Puerto SSH | **5344** |
| Usuario SSH | `root` |
| SO | Ubuntu 24.04 (mínima, UEFI) |
| Ruta del proyecto en el servidor | `/root/mattfuncional` (objetivo; crear al clonar) |
| Ruta uploads (`MATT_UPLOADS_DIR`) | `/root/mattfuncional/uploads` (perfil `donweb`) |
| Usuario MySQL | `mattfuncional_user` (definir al crear BD; contraseña **no** en el repo) |
| Dominio canónico | `https://mattfuncional.com.ar` (pendiente DNS/Certbot) |

---

## 7. Después de la migración

- Monitorear **logs** (`./mattfuncional` opción 7 o `logs/mattfuncional.log`) las primeras 24–48 h.
- **Backup** periódico de MySQL y de la carpeta `uploads` (política del cliente).
- Actualizar **LEEME_PRIMERO** o manual interno con la URL definitiva del cliente si lo usan como documentación viva.

---

## 8. Referencia rápida: detodoya vs cliente

| | Antes (referencia) | Servidor del cliente (registrado) |
|--|-------------------|-----------------------------------|
| Dominio público | detodoya.com.ar | **mattfuncional.com.ar** (DNS pendiente) |
| Proveedor / VPS | Donweb — `149.50.144.53`, SSH `-p 5638` | **Dattaweb** — `200.58.127.154`, SSH **`-p 5344`** |
| Config Nginx en repo | `nginx-detodoya.conf` | `nginx-mattfuncional-com-ar.conf` |

---

*Plan creado para la migración al servidor final del cliente. Abril 2026. Complementa [servidor/DESPLIEGUE-SERVIDOR.md](servidor/DESPLIEGUE-SERVIDOR.md); el **Anexo A** sustituye el documento separado de migración genérica.*

**Historial de este documento:** 2026-04 — Alta §0 y §6 con datos reales del panel (Ubuntu 24.04 UEFI mínima, Dattaweb, IP y puerto SSH). 2026-04 — Unificación: anexo con checklist y comandos (antes archivo aparte).

---

## Anexo A — Checklist, primera instalación y mantenimiento

> Contenido integrado desde la guía genérica de migración. Para el **servidor del cliente actual** seguí primero las **§0–§8** de este mismo archivo; este anexo sirve como receta de comandos y para otros despliegues.

### A.1 Checklist previo a la migración

- [ ] Cliente tiene (o va a contratar) un VPS con **Ubuntu 24.04** (o 22.04 LTS).
- [ ] Cliente tiene (o va a registrar) un **dominio** que apuntará al servidor (ej. `midominio.com`).
- [ ] Repositorio de Mattfuncional accesible (GitHub público o acceso para el cliente/equipo).
- [ ] Decidir credenciales de BD y usuario developer en el servidor del cliente (no reutilizar las de producción actual).
- [ ] Tener a mano: [servidor/DESPLIEGUE-SERVIDOR.md](servidor/DESPLIEGUE-SERVIDOR.md) y las fases **§3** de arriba.

### A.2 Pasos para el servidor (primera vez)

#### A.2.1 Preparar el servidor

1. **SSH** al VPS (puerto según proveedor, ej. `ssh -p 22 root@IP` o el puerto del panel).
2. **Actualizar:** `apt update && apt upgrade -y`
3. **Java y Git:** `apt install -y openjdk-17-jdk-headless git` (el proyecto usa `./mvnw`; hace falta JDK para compilar y ejecutar el JAR).
4. **MySQL:** `apt install -y mysql-server` → `mysql_secure_installation` → crear BD y usuario (utf8mb4), `MATT_DB_USER` / `MATT_DB_PASSWORD` en `~/.bashrc`.
5. **Clonar** el repo (ej. `cd /root && git clone … mattfuncional && cd mattfuncional`).
6. **Permisos:** `chmod +x ./mattfuncional` y **`chmod +x ./mvnw`** (en Linux el wrapper debe ser ejecutable o fallará `mvn: command not found` si no hay Maven global).
7. **Uploads:** `mkdir -p …/uploads`
8. **Perfil Spring:** el menú usa `donweb` por defecto; `application-donweb.properties` y puerto **8080** vía script (`MATT_APP_PORT`) para alinear con Nginx.

#### A.2.2 Compilar y arrancar

```bash
cd /root/mattfuncional
./mvnw clean package -DskipTests -q
./mattfuncional
```

Opción **4** (iniciar) o **5** (despliegue completo). Comprobar: `curl -I http://127.0.0.1:8080`.

#### A.2.3 Nginx y dominio

- Instalar `nginx`; sitio con `proxy_pass http://127.0.0.1:8080;`, `client_max_body_size 50M;`.
- Plantillas en [servidor/](servidor/): `nginx-mattfuncional-http-only.conf` (solo HTTP hasta Certbot), `nginx-mattfuncional-com-ar.conf`, `nginx-detodoya.conf` (ejemplo).
- DNS: registro **A** al VPS. HTTPS: `certbot --nginx -d …` y revisar que siga `client_max_body_size 50M` en el bloque que hace proxy.

#### A.2.4 Datos iniciales y backup

- Si la BD está vacía, `DataInitializer` crea usuarios de desarrollo (documentar y cambiar contraseñas).
- Export/import desde **Administración → Backup** (ZIP, JSON alumnos); volcado MySQL si aplica.

### A.3 Mantenimiento

- **Actualizar:** `./mattfuncional` → **5** (git pull, compilar, iniciar).
- **Logs:** opción **7** o `tail -f …/logs/mattfuncional.log`.
- **Reinicio solo app:** opción **8**.

### A.4 Tabla equivalencias (servidor de referencia vs cliente)

| Aspecto | Referencia (Donweb / detodoya) | Servidor del cliente |
|---------|------------------------------|----------------------|
| IP | 149.50.144.53 | La que asigne el proveedor |
| Puerto SSH | 5638 (ejemplo) | 22 u otro (ej. **5344** Dattaweb) |
| Dominio | detodoya.com.ar | mattfuncional.com.ar u otro |
| SSL | Certbot | Certbot en el dominio del cliente |

### A.5 Resumen rápido (orden)

1. VPS Ubuntu, IP y SSH.  
2. Java 17, MySQL, Git; BD + `MATT_DB_*`.  
3. Clonar; `chmod +x mattfuncional mvnw`; `mkdir uploads`.  
4. `./mvnw clean package -DskipTests`; `./mattfuncional` (4 o 5).  
5. Nginx → 8080, 50M body; DNS.  
6. Opcional: HTTPS.  
7. Probar dominio; credenciales seguras.  
8. Migrar datos por panel Backup si aplica.

---

*Anexo: contenido base feb–abr 2026; alineado con `DESPLIEGUE-SERVIDOR.md` y script `mattfuncional`.*
