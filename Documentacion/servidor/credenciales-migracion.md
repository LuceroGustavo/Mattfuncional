# Credenciales y Cuentas - Migración de Servidor

**Fecha de creación:** Enero 2025  
**Propósito:** Documentación de cuentas y credenciales creadas para la migración del servidor  
**Estado:** 🔄 En proceso

---

## 📧 **CUENTAS DE CORREO ELECTRÓNICO**

### **1. Gmail para GitHub**

**Propósito:** Crear cuentas en GitHub para el proyecto

- **Email:** `oriola.app@gmail.com`
- **Contraseña:** `Oriola.2025.app`
- **Plataforma:** Gmail
- **Uso:** Creación de cuentas en GitHub y otros servicios relacionados

---

## 🖥️ **CUENTA DE LIGHTNODE (SERVIDOR)**

**Plataforma:** [LightNode Console](https://console.lightnode.com/)  
**Propósito:** Servidor VPS para alojar la aplicación ORIOLA

### **Información que el Cliente debe Proporcionar**

Una vez que el cliente cree la cuenta en LightNode, necesitamos los siguientes datos:

#### **1. Credenciales de Acceso a LightNode**
- **Email de la cuenta:** `_________________` *(pendiente - cliente debe proporcionar)*
- **Contraseña:** `_________________` *(pendiente - cliente debe proporcionar)*
- **URL del panel:** `https://console.lightnode.com/`

#### **2. Información del Servidor VPS**
- **IP del servidor:** `_________________` *(pendiente - cliente debe proporcionar)*
- **Usuario SSH:** `_________________` *(típicamente `root` o `ubuntu`)*
- **Contraseña SSH:** `_________________` *(pendiente - cliente debe proporcionar)*
- **Puerto SSH:** `_________________` *(típicamente `22`)*
- **Sistema operativo:** `_________________` *(ej: Ubuntu 22.04 LTS)*

#### **3. Información Adicional del Servidor**
- **Región/Datacenter:** `_________________` *(pendiente - cliente debe proporcionar)*
- **Plan/Especificaciones:** `_________________` *(ej: 2 vCPU, 4GB RAM, 80GB SSD)*
- **Costo mensual:** `_________________` *(pendiente - cliente debe proporcionar)*

#### **4. Acceso Root/Administrador**
- **¿Tiene acceso root?** ☐ Sí ☐ No
- **Método de acceso:** ☐ SSH con contraseña ☐ SSH con clave ☐ Otro: `_________________`

### **Instrucciones para el Cliente**

**Paso 1: Crear cuenta en LightNode**
1. Ir a https://console.lightnode.com/
2. Hacer clic en "Sign Up" o "Registrarse"
3. Completar el formulario de registro:
   - Email (usar un email corporativo o del cliente)
   - Contraseña segura
   - Verificar email
4. Completar el proceso de verificación

**Paso 2: Crear/Contratar Servidor VPS**
1. Una vez dentro del panel, buscar la opción para crear un nuevo servidor/VPS
2. Seleccionar:
   - **Ubuntu 22.04 LTS** (o la versión más reciente disponible)
   - **Plan adecuado** (recomendado: mínimo 2 vCPU, 4GB RAM, 80GB SSD)
   - **Región** (preferiblemente cercana a Argentina)
3. Configurar:
   - Contraseña root/administrador (guardarla de forma segura)
   - Nombre del servidor (ej: "oriola-production")
4. Esperar a que el servidor se cree (puede tardar unos minutos)

**Paso 3: Obtener Información del Servidor**
1. Una vez creado el servidor, anotar:
   - **IP pública del servidor**
   - **Usuario** (típicamente `root` o `ubuntu`)
   - **Contraseña** configurada
   - **Puerto SSH** (típicamente 22)

**Paso 4: Proporcionar Datos al Desarrollador**
- Enviar todos los datos solicitados en la sección "Información que el Cliente debe Proporcionar" arriba

---

## 🔐 **NOTAS DE SEGURIDAD**

⚠️ **IMPORTANTE:** Este archivo contiene información sensible. Asegúrate de:
- Mantener este archivo en un lugar seguro
- No compartir estas credenciales públicamente
- Cambiar las contraseñas periódicamente
- Usar un gestor de contraseñas si es posible

---

## 🐙 **CUENTA DE GITHUB**

### **Información de la Cuenta**

- **Email asociado:** `oriola.app@gmail.com`
- **Contraseña:** `Oriola.2025.app`
- **Usuario/Nombre de cuenta:** `_________________` *(pendiente de crear)*
- **URL del perfil:** `https://github.com/_________________` *(pendiente de crear)*

### **Repositorio del Proyecto**

- **Nombre del repositorio:** `_________________` *(pendiente de crear)*
- **URL del repositorio:** `https://github.com/_________________/_________________` *(pendiente de crear)*
- **Visibilidad:** ☐ Público ☐ Privado *(seleccionar)*

### **Personal Access Token (PAT)**

**¿Qué es?** Un token de acceso personal que permite autenticarse en GitHub sin usar contraseña. Se usa para:
- Clonar repositorios privados
- Hacer push/pull desde el servidor
- Integraciones con CI/CD
- Acceso desde aplicaciones externas

**Información del Token:**
- **Token:** `[TOKEN_REMOVIDO_POR_SEGURIDAD]` *(guardado en lugar seguro fuera del repositorio)*
- **Fecha de creación:** `2025-01-15`
- **Fecha de expiración:** `Sin expiración` *(configurado como "No expiration")*
- **Permisos/Scopes:** 
  - ✅ `repo` (acceso completo a repositorios) - *seleccionado automáticamente con workflow*
  - ✅ `workflow` (acceso a GitHub Actions) - *seleccionado manualmente*

**Nota:** ⚠️ **El token real está guardado en un lugar seguro fuera del repositorio por razones de seguridad. GitHub bloquea automáticamente los tokens en los commits.**

### **SSH Keys (Opcional pero Recomendado)**

**¿Qué es?** Una clave SSH permite autenticarse en GitHub sin usar contraseña o token.

- **Clave SSH pública:** `_________________` *(pendiente de generar)*
- **Clave SSH privada:** *(guardar en lugar seguro, no compartir)*
- **Fecha de creación:** `_________________`

---

## 📝 **INSTRUCCIONES PARA CREAR LA CUENTA DE GITHUB**

### **1. Crear la Cuenta**
1. Ir a https://github.com/signup
2. Ingresar email: `oriola.app@gmail.com`
3. Crear contraseña: `Oriola.2025.app`
4. Elegir nombre de usuario (ej: `oriola-app` o `oriola-indumentaria`)
5. Verificar email

### **2. Crear el Repositorio**
1. Ir a "New repository"
2. Nombre: `OriolaIndumentaria` (o el nombre que prefieras)
3. Descripción: "Sistema de gestión de indumentaria ORIOLA"
4. Visibilidad: Privado (recomendado)
5. NO inicializar con README, .gitignore o licencia (si ya existe el proyecto)

### **3. Crear Personal Access Token (PAT)**
1. Ir a Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Click en "Generate new token (classic)"
3. Nombre: `Oriola-Server-Migration` o similar
4. Expiración: Elegir (recomendado: 90 días o sin expiración)
5. Seleccionar scopes:
   - ✅ `repo` (acceso completo a repositorios)
   - ✅ `workflow` (si usas GitHub Actions)
6. Click en "Generate token"
7. **COPIAR EL TOKEN INMEDIATAMENTE** (solo se muestra una vez)
8. Guardar en este documento

### **4. Generar SSH Key (Opcional)**
```bash
# En el servidor, ejecutar:
ssh-keygen -t ed25519 -C "oriola.app@gmail.com"

# Copiar la clave pública:
cat ~/.ssh/id_ed25519.pub

# Agregar en GitHub: Settings → SSH and GPG keys → New SSH key
```

---

## 📝 **PRÓXIMOS PASOS**

### **Tareas Completadas ✅**
- [x] Crear cuenta en GitHub con el correo `oriola.app@gmail.com`
- [x] Generar Personal Access Token (PAT) ✅
- [x] Documentar credenciales de GitHub en este archivo ✅

### **Tareas Pendientes - Cliente**
- [ ] Cliente debe crear cuenta en LightNode (https://console.lightnode.com/)
- [ ] Cliente debe crear/contratar servidor VPS en LightNode
- [ ] Cliente debe proporcionar credenciales y datos del servidor

### **Tareas Pendientes - Desarrollador**
- [ ] Crear repositorio del proyecto en GitHub
- [ ] Recibir y documentar credenciales de LightNode del cliente
- [ ] Configurar acceso SSH al servidor
- [ ] Configurar repositorio en el servidor
- [ ] Realizar migración completa del proyecto

---

---

## 📧 **COMUNICACIÓN CON EL CLIENTE**

### **Mensaje para Enviar al Cliente**

```
Hola [Nombre del Cliente],

Para proceder con la migración del servidor, necesito que crees una cuenta en LightNode 
y contrates un servidor VPS. A continuación te detallo los pasos:

1. Crear cuenta en LightNode:
   - Ir a: https://console.lightnode.com/
   - Registrarse con un email corporativo
   - Verificar el email

2. Contratar/Crear Servidor VPS:
   - Una vez dentro del panel, crear un nuevo servidor
   - Sistema operativo: Ubuntu 22.04 LTS (o la versión más reciente)
   - Plan recomendado: Mínimo 2 vCPU, 4GB RAM, 80GB SSD
   - Región: Preferiblemente cercana a Argentina
   - Configurar una contraseña segura para el acceso root

3. Proporcionarme los siguientes datos:
   - Email de la cuenta de LightNode
   - IP del servidor
   - Usuario SSH (típicamente "root" o "ubuntu")
   - Contraseña SSH
   - Puerto SSH (típicamente 22)
   - Especificaciones del plan contratado

Una vez que tengas estos datos, envíamelos de forma segura para proceder con la migración.

Saludos.
```

**Última actualización:** Enero 2025

