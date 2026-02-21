# Despliegue de Mattfuncional en servidor Donweb

**Objetivo:** Desplegar la app Mattfuncional en el mismo VPS Donweb (detodoya.com / 149.50.144.53) y gestionarla con un menú similar al de Detodoya.

**Referencia:** Ver `Configuracion-Servidor-Donweb.md` para datos del servidor (IP, SSH, firewall, etc.).

---

## 1. Datos del servidor (resumen)

| Dato        | Valor                    |
|------------|---------------------------|
| IP         | `149.50.144.53`           |
| SSH        | `ssh -p5638 root@149.50.144.53` |
| SO         | Ubuntu 24.04              |
| Software   | Java 17, Maven, MySQL 8, Nginx |

**Puertos en uso:**
- **8080** – Detodoya (u otra app ya desplegada)
- **8081** – Mattfuncional (configurado para no chocar; puedes cambiar a 8080 si paras la otra app)

---

## 2. Primera vez: preparar el servidor para Mattfuncional

### 2.1 Conectar por SSH

```bash
ssh -p5638 root@149.50.144.53
```

### 2.2 Crear base de datos y usuario MySQL (recomendado)

```bash
mysql -u root -p
```

En MySQL:

```sql
CREATE DATABASE IF NOT EXISTS mattfuncional
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'mattfuncional_user'@'localhost' IDENTIFIED BY 'TU_PASSWORD_SEGURO';
GRANT ALL PRIVILEGES ON mattfuncional.* TO 'mattfuncional_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

Guarda la contraseña; la usarás en variables de entorno o en `application-donweb.properties` (mejor no subir la contraseña al repo).

### 2.3 Clonar el repositorio de Mattfuncional

Repositorio: **https://github.com/LuceroGustavo/Mattfuncional**

```bash
cd /root
git clone https://github.com/LuceroGustavo/Mattfuncional.git mattfuncional
cd mattfuncional
```

Si el repo es privado, configura credenciales (token o SSH) antes del `git pull`/`clone`.

### 2.4 Configurar contraseña de BD para el perfil donweb

Opción A – Variables de entorno (recomendado):

```bash
echo 'export MATT_DB_USER=mattfuncional_user' >> ~/.bashrc
echo 'export MATT_DB_PASSWORD=TU_PASSWORD_SEGURO' >> ~/.bashrc
source ~/.bashrc
```

Opción B – Archivo local en el servidor (no versionado):

Crear o editar en el servidor algo como `/root/mattfuncional/config/donweb.env` y cargar esas variables antes de ejecutar el JAR, o sobrescribir en `application-donweb.properties` solo en el servidor (y no hacer commit de ese archivo).

### 2.5 Directorio de uploads (opcional)

El perfil `donweb` usa `/home/mattfuncional/uploads`. Puedes crear el directorio y dar permisos:

```bash
mkdir -p /home/mattfuncional/uploads
chown -R root:root /home/mattfuncional
```

Si prefieres que la app corra como root y escribir en `/root/mattfuncional/uploads`:

```bash
mkdir -p /root/mattfuncional/uploads
```

y en el servidor cambia en `application-donweb.properties` (copia local) la propiedad `mattfuncional.uploads.dir` a `/root/mattfuncional/uploads`.

### 2.6 Dar permisos de ejecución al script de menú

```bash
chmod +x /root/mattfuncional/scripts/servidor/menu-mattfuncional.sh
```

---

## 3. Despliegue y uso del menú de gestión

### 3.1 Ejecutar el menú

Desde el directorio del proyecto en el servidor:

```bash
cd /root/mattfuncional
./scripts/servidor/menu-mattfuncional.sh
```

(O desde cualquier sitio: `/root/mattfuncional/scripts/servidor/menu-mattfuncional.sh`; el script detecta la raíz del proyecto.)

### 3.2 Opciones del menú (igual que el estilo Detodoya)

| Opción | Acción |
|--------|--------|
| 1 | Parar aplicación Mattfuncional |
| 2 | Actualizar código (git pull) |
| 3 | Compilar aplicación (Maven) |
| 4 | Iniciar aplicación Mattfuncional |
| 5 | **Despliegue completo** (1 → 2 → 3 → 4) |
| 6 | Ver estado del sistema |
| 7 | Ver logs de la aplicación |
| 8 | Reiniciar aplicación |
| 9 | Información del proyecto |
| 10 | Ver espacio en disco |
| 11 | Salir |

### 3.3 Primera puesta en marcha

1. En el menú, elige **5** (Despliegue completo).  
   O bien: **3** (Compilar) y luego **4** (Iniciar).

2. Comprueba que la app responda:

   ```text
   http://149.50.144.53:8081
   ```

   (Si configuraste dominio o Nginx, usa la URL que hayas definido.)

### 3.4 Cambiar el puerto (por ejemplo a 8080)

Por defecto Mattfuncional usa **8081**. Para usar **8080** (y dejar de usar Detodoya en ese puerto):

```bash
export MATT_APP_PORT=8080
./scripts/servidor/menu-mattfuncional.sh
```

Luego inicia con la opción 4 (o 5). Puedes poner `export MATT_APP_PORT=8080` en `~/.bashrc` para que quede fijo.

---

## 4. Archivos del proyecto relacionados

| Archivo | Descripción |
|---------|-------------|
| `src/main/resources/application-donweb.properties` | Perfil Spring para Donweb (puerto 8081, BD, uploads, sin devtools). |
| `scripts/servidor/menu-mattfuncional.sh` | Menú de gestión (parar, git pull, compilar, iniciar, despliegue completo, logs, etc.). |

---

## 5. Resumen de comandos útiles (sin menú)

```bash
# Conectar
ssh -p5638 root@149.50.144.53

# Ir al proyecto
cd /root/mattfuncional

# Despliegue rápido (desde el proyecto)
./scripts/servidor/menu-mattfuncional.sh
# Luego opción 5

# O manual:
./scripts/servidor/menu-mattfuncional.sh  # opción 1 (parar), 2 (pull), 3 (mvn), 4 (iniciar)
```

---

## 6. Notas

- **Detodoya** sigue en el mismo servidor (puerto 8080); **Mattfuncional** por defecto en **8081** para que puedan convivir.
- El script usa `target/mattfuncional-0.0.1-SNAPSHOT.jar`; si cambias la versión en el `pom.xml`, ajusta `JAR_NAME` en `menu-mattfuncional.sh`.
- Logs del servidor: `logs/mattfuncional.log` dentro del directorio del proyecto (el script los crea si no existen).
- Firewall Donweb: si usas otro puerto (ej. 8081), agrega una regla TCP para ese puerto en el panel de Donweb, igual que para el 8080.

**Última actualización:** Febrero 2026
