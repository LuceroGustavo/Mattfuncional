# Despliegue de Mattfuncional en el servidor Donweb

Guía para desplegar y gestionar la aplicación **Mattfuncional** en el VPS de Donweb. Incluye el uso por **Consola VNC** (sin copiar/pegar) y por SSH.

**Repositorio:** https://github.com/LuceroGustavo/Mattfuncional

---

## 1. Datos del servidor

| Dato   | Valor |
|--------|--------|
| IP     | `149.50.144.53` |
| Puerto aplicación | **8080** |
| SSH    | `ssh -p5638 root@149.50.144.53` |
| SO     | Ubuntu 24.04 |

**URL de la aplicación (una vez desplegada):**  
`http://149.50.144.53:8080`

---

## 2. Acceso cuando no podés usar PowerShell/SSH (Consola VNC)

Si en tu trabajo PowerShell está bloqueado, podés gestionar el servidor desde la **Consola VNC** que ofrece Donweb:

1. Entrá al panel de Donweb → tu servidor (detodoya.com / vps-5469468-x).
2. Hacé clic en **Consola VNC** (botón verde).
3. En la consola **no se puede copiar/pegar**; tenés que escribir los comandos a mano.

### 2.1 Entrar al menú de Mattfuncional (primera vez o si no existe la sesión)

Escribí en este orden (cada línea y Enter):

```text
cd /root/mattfuncional
```

```text
./iniciar-menu.sh
```

Si la sesión ya existía, te dirá algo como: *"La sesión 'mattfuncional' ya existe. Para entrar al menú ejecutá: screen -r mattfuncional"*. En ese caso pasá al paso 2.2.

Si se creó nueva, también te pedirá que entres con el comando del paso 2.2.

### 2.2 Conectarte a la sesión del menú

Escribí **exactamente** (con **espacio** entre `screen` y `-r`):

```text
screen -r mattfuncional
```

Importante: no escribas `screen-r` junto; tiene que ser `screen`, espacio, `-r`, espacio, `mattfuncional`.

### 2.3 Despliegue completo desde el menú

En el menú **MATTFUNCIONAL - MENÚ DE GESTIÓN** elegí la opción **5** (Despliegue completo).  
Eso hace: parar app → actualizar código (git pull) → compilar → iniciar.

Cuando termine, la app queda en: **http://149.50.144.53:8080**

### 2.4 Salir del menú sin cerrarlo

Para salir de la consola pero dejar el menú y la app corriendo:  
**Ctrl+A**, soltá, y después **D** (detach).  
La próxima vez que entres por VNC podés volver con: `screen -r mattfuncional`.

---

## 3. Opciones del menú (1-11)

| Opción | Acción |
|--------|--------|
| 1 | Parar aplicación Mattfuncional |
| 2 | Actualizar código (git pull) |
| 3 | Compilar aplicación Mattfuncional |
| 4 | Iniciar aplicación Mattfuncional |
| 5 | **Despliegue completo** (1 → 2 → 3 → 4) |
| 6 | Ver estado del sistema |
| 7 | Ver logs de la aplicación |
| 8 | Reiniciar aplicación Mattfuncional |
| 9 | Información del proyecto |
| 10 | Ver espacio en disco |
| 11 | Salir |

---

## 4. Acceso a la aplicación (usuario developer)

Una vez desplegada, entrá en el navegador a:

**http://149.50.144.53:8080**

Para el **usuario developer** (creado automáticamente al iniciar la app):

- **Usuario (correo):** `developer@mattfuncional.com`
- **Contraseña:** definida en el proyecto (ver `DataInitializer.java` o documentación interna).  
  Si no la cambiaste, es la que está en ese archivo (no se documenta aquí por seguridad).

---

## 5. Primera vez: preparar el servidor

Solo hace falta hacerlo una vez (o si reinstalás el servidor).

### 5.1 Base de datos MySQL

Crear la base y el usuario (por ejemplo desde SSH o desde la misma consola):

```bash
mysql -u root -p
```

Dentro de MySQL:

```sql
CREATE DATABASE IF NOT EXISTS mattfuncional
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'mattfuncional_user'@'localhost' IDENTIFIED BY 'Matt2026';
GRANT ALL PRIVILEGES ON mattfuncional.* TO 'mattfuncional_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### 5.2 Clonar el repositorio

```bash
cd /root
git clone https://github.com/LuceroGustavo/Mattfuncional.git mattfuncional
cd mattfuncional
```

Si el repo es privado, configurá token o SSH antes.

### 5.3 Variables de entorno para la base de datos

Para que el menú pueda iniciar la app correctamente:

```bash
echo 'export MATT_DB_USER=mattfuncional_user' >> ~/.bashrc
echo 'export MATT_DB_PASSWORD=Matt2026' >> ~/.bashrc
source ~/.bashrc
```

(Sustituí `Matt2026` si usaste otra contraseña en MySQL.)

### 5.4 Permisos de ejecución

```bash
chmod +x /root/mattfuncional/mattfuncional
chmod +x /root/mattfuncional/scripts/servidor/iniciar-menu.sh
```

### 5.5 Directorio de uploads (opcional)

```bash
mkdir -p /home/mattfuncional/uploads
```

Si preferís usar la raíz del proyecto: `mkdir -p /root/mattfuncional/uploads` y ajustar `mattfuncional.uploads.dir` en `application-donweb.properties` en el servidor.

---

## 6. Uso por SSH (alternativa)

Si tenés SSH disponible:

```bash
ssh -p5638 root@149.50.144.53
cd /root/mattfuncional
./iniciar-menu.sh
screen -r mattfuncional
```

Luego en el menú elegí la opción que necesites (por ejemplo **5** para despliegue completo).

---

## 7. Archivos relacionados

| Archivo | Descripción |
|---------|-------------|
| `mattfuncional` (raíz del proyecto) | Script del menú de gestión (ejecutar como `./mattfuncional`). |
| `scripts/servidor/iniciar-menu.sh` | Crea la sesión `screen` con el menú; después se entra con `screen -r mattfuncional`. |
| `src/main/resources/application-donweb.properties` | Perfil Spring para Donweb (puerto por defecto 8081; el menú usa 8080 con `MATT_APP_PORT`). |

---

## 8. Resumen rápido (Consola VNC)

1. Donweb → Consola VNC.
2. `cd /root/mattfuncional`
3. `./iniciar-menu.sh`
4. `screen -r mattfuncional` (con espacio entre `screen` y `-r`).
5. Opción **5** para despliegue completo.
6. Abrir en el navegador: **http://149.50.144.53:8080**
7. Salir del menú sin cerrar: **Ctrl+A**, luego **D**.

---

**Última actualización:** Febrero 2026
