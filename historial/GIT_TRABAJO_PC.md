# 🌿 **GIT - Sincronización en PC del Trabajo**

## 📅 **Fecha de Creación:** 
- **Día:** Hoy
- **Hora:** Creado en la sesión actual

---

## 🎯 **OBJETIVO**

Sincronizar la PC del trabajo con la **rama de desarrollo** `feature/sistema-exportacion-ejercicios` para continuar trabajando en el sistema de exportación/importación de ejercicios.

---

## ⚠️ **IMPORTANTE - NO HACER ESTO:**

```bash
# ❌ NO hacer esto - Te traerá solo main, NO tu rama de desarrollo
git pull origin main
```

**¿Por qué no?**
- **Main** solo tiene commits hasta donde estaba cuando creaste la rama
- **NO incluye** tu trabajo en `feature/sistema-exportacion-ejercicios`
- **Perderías** todo el progreso de hoy

---

## ✅ **PASOS CORRECTOS (Copia y Pega):**

### **🔄 PASO 1: Verificar Estado Actual**
```bash
# Ver en qué rama estás y qué ramas existen
git status
git branch -a
```

**Resultado esperado:** Deberías ver que estás en `main` y que no existe `feature/sistema-exportacion-ejercicios`

---

### **🔄 PASO 2: Traer Todas las Ramas Remotas**
```bash
# Actualizar información de todas las ramas remotas
git fetch origin
```

**Resultado esperado:** Deberías ver que se descargan las ramas remotas

---

### **🔄 PASO 3: Crear y Cambiar a tu Rama de Desarrollo**
```bash
# Crear la rama local desde la rama remota
git checkout -b feature/sistema-exportacion-ejercicios origin/feature/sistema-exportacion-ejercicios
```

**Resultado esperado:** Deberías cambiar a la rama `feature/sistema-exportacion-ejercicios`

---

### **🔄 PASO 4: Verificar que Estés en la Rama Correcta**
```bash
# Confirmar que estás en la rama correcta
git branch
```

**Resultado esperado:** Debería mostrar `* feature/sistema-exportacion-ejercicios`

---

### **🔄 PASO 5: Sincronizar con los Últimos Cambios**
```bash
# Traer los últimos cambios de tu rama
git pull origin feature/sistema-exportacion-ejercicios
```

**Resultado esperado:** Deberías ver que se descargan los commits más recientes

---

## 🚀 **COMANDO COMPLETO (Copia y Pega Todo Junto):**

```bash
git fetch origin
git checkout -b feature/sistema-exportacion-ejercicios origin/feature/sistema-exportacion-ejercicios
git pull origin feature/sistema-exportacion-ejercicios
```

---

## 📊 **VERIFICACIÓN FINAL:**

### **✅ Deberías Ver:**
- **Rama activa:** `feature/sistema-exportacion-ejercicios`
- **Archivos actualizados:** `tarea_actual.md` con el plan de desarrollo
- **Commits recientes:** Incluyendo "Commit 1 - Desarrollo Implementacion de plan de trabajo"

### **❌ Si NO Funciona:**
- Verificar que tengas acceso al repositorio
- Verificar que la rama se haya subido correctamente desde tu PC de casa
- Contactar al administrador del repositorio si es necesario

---

## 🎯 **DESPUÉS DE SINCRONIZAR:**

### **✅ Estarás Listo Para:**
1. **Continuar** con el desarrollo del sistema de exportación
2. **Implementar** el PASO 1: Método de exportación JSON
3. **Trabajar** en la rama de desarrollo

### **📝 Al Terminar tu Sesión de Trabajo:**
```bash
# Hacer commit de tus cambios
git add .
git commit -m "feat: [descripción de lo que implementaste]"

# Subir cambios a la rama remota
git push origin feature/sistema-exportacion-ejercicios
```

---

## 🔍 **SOLUCIÓN DE PROBLEMAS:**

### **❌ Error: "Branch not found"**
```bash
# Verificar que la rama existe remotamente
git branch -r

# Si no aparece, verificar que se haya subido desde casa
```

### **❌ Error: "Already exists"**
```bash
# La rama ya existe localmente, solo cambiar a ella
git checkout feature/sistema-exportacion-ejercicios
git pull origin feature/sistema-exportacion-ejercicios
```

### **❌ Error: "Permission denied"**
- Verificar credenciales de Git
- Verificar permisos en el repositorio

---

## 📱 **RESUMEN RÁPIDO:**

**Para PC del trabajo (5 commits atrasada):**
1. `git fetch origin`
2. `git checkout -b feature/sistema-exportacion-ejercicios origin/feature/sistema-exportacion-ejercicios`
3. `git pull origin feature/sistema-exportacion-ejercicios`

**¡Listo para continuar trabajando!** 🚀

---

## 📞 **EN CASO DE EMERGENCIA:**

Si algo no funciona:
1. **NO hacer commit** en main
2. **NO hacer pull** de main
3. **Contactar** al equipo de desarrollo
4. **Verificar** que la rama se haya subido correctamente desde casa

---

**🎯 ¡Con estos pasos estarás sincronizado y listo para continuar el desarrollo!**
