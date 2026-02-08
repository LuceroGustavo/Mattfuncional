# Sistema de Configuración por Perfiles - MiGym

## 🎯 **Objetivo**

Este documento explica cómo funciona el nuevo sistema de configuración por perfiles que **elimina conflictos** entre desarrollo local y Railway.

## 📁 **Archivos de Configuración**

### **1. `application.properties` (Archivo Principal)**
- **Propósito**: Configuración básica y valores por defecto
- **Uso**: Siempre se carga, independientemente del perfil
- **Contenido**: Configuración común a todos los entornos

### **2. `application-dev.properties` (Desarrollo Local)**
- **Propósito**: Configuración específica para desarrollo local
- **Uso**: Se carga cuando `SPRING_PROFILES_ACTIVE=dev`
- **Contenido**: Logging detallado, archivos grandes, pool de conexiones amplio

### **3. `application-railway.properties` (Railway)**
- **Propósito**: Configuración específica para Railway
- **Uso**: Se carga cuando `SPRING_PROFILES_ACTIVE=railway`
- **Contenido**: Logging mínimo, archivos pequeños, pool de conexiones reducido

## 🔄 **Cómo Funciona el Flujo**

### **Desarrollo Local**
```bash
# Ejecutar con perfil dev (por defecto)
./mvnw spring-boot:run

# O explícitamente
./mvnw spring-boot:run -Dspring.profiles.active=dev
```

**Resultado**: Se cargan `application.properties` + `application-dev.properties`

### **Railway**
```bash
# Railway automáticamente usa
SPRING_PROFILES_ACTIVE=railway
```

**Resultado**: Se cargan `application.properties` + `application-railway.properties`

## ✅ **Beneficios del Nuevo Sistema**

### **1. Sin Conflictos de Configuración**
- ❌ **Antes**: Credenciales hardcodeadas causaban conflictos
- ✅ **Ahora**: Cada entorno tiene su configuración específica

### **2. Despliegue Automático Seguro**
- ❌ **Antes**: Push a GitHub podía romper Railway
- ✅ **Ahora**: Railway siempre usa su configuración específica

### **3. Desarrollo Local Optimizado**
- ❌ **Antes**: Configuración de producción en desarrollo
- ✅ **Ahora**: Desarrollo con logging detallado y archivos grandes

### **4. Railway Optimizado**
- ❌ **Antes**: Configuración de desarrollo en producción
- ✅ **Ahora**: Railway con logging mínimo y recursos optimizados

## 🔧 **Variables de Entorno por Entorno**

### **Desarrollo Local**
```env
# No necesitas configurar nada - usa valores por defecto
# O puedes crear un archivo .env.local si quieres personalizar
```

### **Railway**
```env
# Variables OBLIGATORIAS en Railway
SPRING_PROFILES_ACTIVE=railway
DATABASE_URL=jdbc:mysql://tu-host:puerto/railway?...
DB_USERNAME=tu_usuario
DB_PASSWORD=tu_password
PORT=8080

# Variables OPCIONALES
JAVA_OPTS=-Xmx512m -Xms256m
```

## 🚀 **Flujo de Trabajo Recomendado**

### **1. Desarrollo Local**
```bash
# Hacer cambios en el código
# Probar localmente
./mvnw spring-boot:run
```

### **2. Subir a GitHub**
```bash
git add .
git commit -m "feat: Nueva funcionalidad X"
git push origin main
```

### **3. Railway se Actualiza Automáticamente**
- Railway detecta el push
- Ejecuta build con Dockerfile
- Usa `SPRING_PROFILES_ACTIVE=railway`
- Carga configuración específica de Railway
- **NO se sobrescriben las credenciales**

## 🧪 **Verificación del Sistema**

### **Verificar Perfil Activo**
```bash
# Local
curl http://localhost:8080/actuator/env | grep "spring.profiles.active"

# Railway
curl https://tu-app.railway.app/actuator/env | grep "spring.profiles.active"
```

### **Verificar Configuración de Base de Datos**
```bash
# Local
curl http://localhost:8080/actuator/env | grep "spring.datasource"

# Railway
curl https://tu-app.railway.app/actuator/env | grep "spring.datasource"
```

## 🚨 **Reglas Importantes**

### **✅ HACER**
- Usar variables de entorno para credenciales
- Configurar `SPRING_PROFILES_ACTIVE=railway` en Railway
- Mantener configuración básica en `application.properties`
- Usar perfiles específicos para cada entorno

### **❌ NO HACER**
- Hardcodear credenciales en ningún archivo
- Modificar `application-railway.properties` para desarrollo
- Modificar `application-dev.properties` para Railway
- Committear archivos `.env` o con credenciales

## 🔍 **Troubleshooting**

### **Problema**: Railway no se conecta a la base de datos
**Solución**: Verificar que `SPRING_PROFILES_ACTIVE=railway` esté configurado

### **Problema**: Desarrollo local no funciona
**Solución**: Verificar que no haya variables de entorno conflictivas

### **Problema**: Configuración no se aplica
**Solución**: Verificar que el perfil esté activo con `/actuator/env`

## 📋 **Resumen**

- **`application.properties`**: Configuración básica común
- **`application-dev.properties`**: Desarrollo local optimizado
- **`application-railway.properties`**: Railway optimizado
- **Variables de entorno**: Credenciales y configuración específica
- **Sin conflictos**: Cada entorno mantiene su configuración
- **Despliegue automático**: Seguro y predecible

---

**Estado**: ✅ **Implementado y funcionando**
**Última actualización**: 2025-01-27
**Autor**: Sistema de configuración por perfiles
