# Configuración del Servidor Donweb - Oriola Indumentaria

**Fecha de creación:** 15 de enero de 2025  
**Servidor:** Donweb - Buenos Aires, Argentina  
**Propósito:** Servidor de producción para aplicación Spring Boot  
**Estado:** ✅ **APLICACIÓN FUNCIONANDO**

---

## 🖥️ **DATOS DEL SERVIDOR**

### **Información Básica:**
- **IP Pública:** `149.50.144.53` ✅
- **Hostname:** `vps-5469468-x.dattaweb.com` ✅
- **Puerto SSH:** `5638` ✅ (No es el puerto estándar 22)
- **Ubicación:** Buenos Aires, Argentina 🇦🇷
- **Sistema Operativo:** ✅ **Ubuntu 24.04 UEFI** (Instalado)

### **Especificaciones:**
- **CPU:** 1 vCore
- **RAM:** 2 GB
- **Almacenamiento:** 50 GB SSD
- **Arquitectura:** UEFI
- **Tipo:** Cloud Server

---

## 🛠️ **SOFTWARE PREINSTALADO**

### **Versiones Detectadas:**
- **Git:** ✅ 2.43.0 (Ya instalado)
- **Linux Kernel:** 6.8.0
- **Perl:** 5.38.2
- **Postfix:** 3.8.6 (Servidor de correo)
- **Python:** 3.12.3

### **Software Instalado:**
- [x] Java 17 (OpenJDK) ✅
- [x] MySQL 8.0 ✅
- [x] Maven 3.8+ ✅
- [x] Nginx (proxy reverso) ✅

---

## 🔐 **CONTRASEÑAS Y ACCESOS**

### **Acceso SSH al Servidor:**
- **Usuario:** `root`
- **Método:** ✅ **Contraseña** (Configurado inicialmente)
- **Contraseña:** `Qbasic.1977.server`
- **Puerto SSH:** `5638` (⚠️ No es el puerto estándar 22)
- **Comando:** `ssh -p5638 root@149.50.144.53`
- **Hostname alternativo:** `ssh -p5638 root@vps-5469468-x.dattaweb.com`
- **Estado SSH Key:** ⏳ Pendiente de configurar manualmente después
- **Nota:** Se configurará la clave SSH después de crear el servidor

### **MySQL - Usuario Administrador:**
- **Usuario:** `root`
- **Contraseña:** `[CONFIGURAR]` (puede ser la misma que LightNode: `OriolaMySQL2025!`)
- **Uso:** Administración de MySQL

### **MySQL - Usuario de Aplicación:**
- **Usuario:** `oriola_user`
- **Contraseña:** `OriolaDB2025!` (mantener igual para compatibilidad)
- **Base de datos:** `orioladenim`
- **Uso:** Conexión desde Spring Boot

---

## 🗄️ **BASE DE DATOS MYSQL**

### **Configuración:**
- **Base de datos:** `orioladenim` (a crear)
- **Estado:** ⏳ Pendiente de creación
- **Tablas:** Se crean automáticamente con Spring Boot
- **Configuración:** `spring.jpa.hibernate.ddl-auto=update`

---

## 🌐 **CONFIGURACIÓN DE DOMINIO**

### **Dominio:**
- **URL Principal:** `orioladenim.com.ar`
- **URL WWW:** `www.orioladenim.com.ar`
- **Registrado en:** NIC Argentina

### **DNS (A actualizar después de obtener IP):**
- **A Record:** `orioladenim.com.ar` → `[IP_DONWEB]`
- **CNAME:** `www.orioladenim.com.ar` → `orioladenim.com.ar`

---

## 💰 **INFORMACIÓN DE COSTOS**

### **Precio mensual:**
- **Servidor Cloud:** `[PENDIENTE]` ARS
- **Moneda:** Pesos Argentinos (ARS)
- **Ubicación:** Buenos Aires, Argentina

---

## 🚀 **PRIMEROS PASOS (Según Panel Donweb)**

1. ✅ **Acceder a través de la consola SSH** al Cloud Server
2. ✅ **Configurar el Firewall UFW** del servidor (puertos: 5638, 80, 443, 8080)
3. ✅ **Configurar el Firewall Donweb** en el panel (puerto 8080) ⚠️ **CRÍTICO**
4. ⏳ **Configurar una red LAN** (opcional, para escalar horizontalmente)
5. ⏳ **Crear Snapshots** (para backups y pruebas)
6. ⏳ **Gestionar recursos** (escalar si es necesario)

## ⚠️ **IMPORTANTE: Firewall de Donweb**

**El firewall del panel de Donweb es independiente del firewall UFW del servidor.**

Para que la aplicación sea accesible desde Internet, debes configurar **AMBOS**:
1. ✅ Firewall UFW en el servidor (ya configurado)
2. ✅ Firewall Donweb en el panel (ya configurado)

**Configuración del Firewall Donweb:**
- Acceder a: https://micuenta.donweb.com/es-ar/servicios/cloud-iaas/vps/5469468/configurar/firewall
- Agregar regla TCP:
  - Puerto: 8080
  - IPv4: `0.0.0.0/0`
  - IPv6: `::/0`

---

## ⚙️ **CONFIGURACIÓN DE LA APLICACIÓN**

### **Archivo de configuración:**
- **`application-donweb.properties`** - Configuración específica para Donweb

### **Configuración de base de datos:**
- **URL:** `jdbc:mysql://localhost:3306/orioladenim`
- **Usuario:** `oriola_user`
- **Contraseña:** `OriolaDB2025!`

### **Configuración de archivos:**
- **Uploads:** `/home/oriola/uploads`
- **Backups:** `/home/oriola/backups`
- **Persistencia:** Archivos se guardan en el servidor

### **Perfil activo:**
- **Comando:** `--spring.profiles.active=donweb`

### **Configuración crítica de red:**
- **`server.address=0.0.0.0`** en `application-donweb.properties` ✅
  - Permite que Spring Boot escuche en todas las interfaces de red
  - Sin esto, la aplicación solo escucha en localhost y no es accesible desde fuera

---

## 📊 **COMPARACIÓN CON SERVIDOR ANTERIOR**

| Aspecto | LightNode (Anterior) | Donweb (Nuevo) |
|--------|---------------------|----------------|
| **IP Pública** | 149.104.92.116 | 149.50.144.53 ✅ |
| **Ubicación** | Buenos Aires, AR | Buenos Aires, AR |
| **Sistema Operativo** | Ubuntu 22.04.5 LTS | Ubuntu 24.04 UEFI |
| **CPU** | 1 vCore | 1 vCore |
| **RAM** | 2 GB | 2 GB |
| **Almacenamiento** | 50 GB SSD | 50 GB SSD |
| **Git** | 2.34.1 | 2.43.0 (preinstalado) |
| **Costo** | $7.71 USD/mes | [PENDIENTE] ARS |
| **Moneda** | USD | ARS (Pesos) |

---

## 📞 **CONTACTO Y SOPORTE**

### **Donweb:**
- **Sitio web:** https://micuenta.donweb.com/
- **Soporte:** Disponible en español
- **Panel de control:** Acceso desde https://micuenta.donweb.com/

### **Documentación del proyecto:**
- **Repositorio:** https://github.com/LuceroGustavo/OriolaIndumentaria
- **Rama principal:** `master`

---

## ✅ **ESTADO ACTUAL**

**Última actualización:** 15 de noviembre de 2025  
**Estado:** ✅ **APLICACIÓN FUNCIONANDO**  
**URL:** `http://149.50.144.53:8080`

### **Completado:**
- [x] Servidor Cloud creado exitosamente en Donweb ✅
- [x] Sistema Operativo Ubuntu 24.04 UEFI instalado ✅
- [x] Usuario root configurado (contraseña: `Qbasic.1977.server`) ✅
- [x] IP Pública obtenida: `149.50.144.53` ✅
- [x] Hostname: `vps-5469468-x.dattaweb.com` ✅
- [x] Puerto SSH: `5638` ✅
- [x] Git 2.43.0 preinstalado ✅
- [x] Conexión SSH exitosa ✅
- [x] Sistema Ubuntu 24.04.3 LTS confirmado ✅
- [x] Firewall UFW configurado ✅
- [x] **Firewall Donweb configurado (puerto 8080)** ✅ **CRÍTICO**
- [x] Java 17 instalado ✅
- [x] MySQL 8.0 instalado y configurado ✅
- [x] Maven instalado ✅
- [x] Nginx instalado ✅
- [x] Base de datos `orioladenim` creada ✅
- [x] Usuario `oriola_user` creado ✅
- [x] Repositorio clonado ✅
- [x] `application-donweb.properties` creado ✅
- [x] `server.address=0.0.0.0` configurado ✅
- [x] Aplicación compilada ✅
- [x] Aplicación desplegada y funcionando ✅
- [x] **Aplicación accesible desde Internet** ✅

### **Pendiente:**
- [ ] Configurar Nginx como proxy reverso
- [ ] Configurar SSL con Let's Encrypt
- [ ] Actualizar DNS (orioladenim.com.ar → 149.50.144.53)
- [ ] Migrar datos desde LightNode (base de datos y archivos)
- [ ] Configurar clave SSH (opcional, mejorar seguridad)

---

**Nota:** Este archivo contiene información sensible. Mantenerlo seguro y actualizado conforme se avance en la configuración.

