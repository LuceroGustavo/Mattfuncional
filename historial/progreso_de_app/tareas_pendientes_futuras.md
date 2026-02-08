# 📋 Tareas Pendientes - Implementaciones Futuras

## 🎯 **Tareas Priorizadas para Próximas Versiones**

---

## 1. 🔄 **Completar Sistema de Backup de Ejercicios**

### **Ubicación:** 
- Dashboard del Admin → Panel "Gestionar Ejercicios"

### **Estado Actual:**
- ✅ Botón "Cargar Ejercicios Predeterminados" - **FUNCIONANDO**
- ✅ Botón "Asignar Ejercicios Predeterminados" - **FUNCIONANDO**
- ❌ **PENDIENTE:** Métodos de backup/exportación de ejercicios

### **Funcionalidades a Implementar:**
- [ ] **Exportar Ejercicios a JSON/CSV**
- [ ] **Backup Automático de Base de Datos**
- [ ] **Restaurar Ejercicios desde Backup**
- [ ] **Sincronización entre Profesores**
- [ ] **Versionado de Ejercicios**

### **Archivos Involucrados:**
- `src/main/java/com/migym/servicios/ExerciseBackupService.java`
- `src/main/resources/templates/admin/ejercicios-gestion.html`
- `src/main/java/com/migym/controladores/EjerciciosGestionController.java`

---

## 2. 🎨 **Sistema de Colores y Tipos para Rutinas**

### **Objetivo:**
Crear un sistema visual que permita a los profesores categorizar y organizar mejor sus rutinas mediante colores y tipos predefinidos.

### **Funcionalidades a Implementar:**

#### **2.1 Gestión de Tipos de Rutina**
- [ ] **Crear entidad `TipoRutina`** con:
  - ID único
  - Nombre corto (ej: "Fuerza", "Cardio", "FullBody")
  - Color hexadecimal asociado
  - Descripción opcional
  - Profesor propietario

#### **2.2 Paleta de Colores Predefinida**
- [ ] **10 colores base** para elegir:
  - 🔴 Rojo - Fuerza
  - 🔵 Azul - Cardio  
  - 🟢 Verde - Resistencia
  - 🟡 Amarillo - Flexibilidad
  - 🟣 Púrpura - Hipertrofia
  - 🟠 Naranja - Potencia
  - ⚫ Negro - CrossFit
  - ⚪ Gris - Recuperación
  - 🟤 Marrón - Funcional
  - 💙 Cian - Acuático

#### **2.3 Asignación de Tipos**
- [ ] **Al crear rutina:** Profesor selecciona tipo y color
- [ ] **Al editar rutina:** Cambiar tipo/color existente
- [ ] **Visualización:** Tarjetas de rutina muestran color del tipo
- [ ] **Filtrado:** Buscar rutinas por tipo/color

#### **2.4 Reutilización de Tipos**
- [ ] **Tipos compartidos:** Profesor puede usar tipos ya creados
- [ ] **Personalización:** Crear nuevos tipos con colores personalizados
- [ ] **Organización:** Agrupar rutinas por tipo en el dashboard

### **Archivos a Crear/Modificar:**

#### **Entidades:**
- `src/main/java/com/migym/entidades/TipoRutina.java`
- `src/main/java/com/migym/entidades/Rutina.java` (agregar relación)

#### **Repositorios:**
- `src/main/java/com/migym/repositorios/TipoRutinaRepository.java`

#### **Servicios:**
- `src/main/java/com/migym/servicios/TipoRutinaService.java`

#### **Controladores:**
- `src/main/java/com/migym/controladores/TipoRutinaController.java`

#### **Templates:**
- `src/main/resources/templates/profesor/crear-rutina.html` (selector de tipo)
- `src/main/resources/templates/profesor/editar-rutina.html` (cambio de tipo)
- `src/main/resources/templates/profesor/dashboard.html` (visualización por color)

#### **CSS:**
- `src/main/resources/static/css/rutina-colors.css` (estilos de colores)

---

## 🚀 **Orden de Implementación Recomendado:**

### **Fase 1: Sistema de Colores (Más Impacto Visual)**
1. Crear entidad `TipoRutina`
2. Implementar CRUD básico de tipos
3. Modificar creación/edición de rutinas
4. Aplicar colores en visualización

### **Fase 2: Backup de Ejercicios (Más Técnico)**
1. Completar `ExerciseBackupService`
2. Implementar exportación JSON/CSV
3. Crear sistema de restauración
4. Agregar interfaz de usuario

---

## 💡 **Beneficios Esperados:**

### **Sistema de Colores:**
- ✅ **Mejor organización visual** de rutinas
- ✅ **Búsqueda rápida** por tipo/categoría
- ✅ **Experiencia de usuario mejorada**
- ✅ **Profesionalización** de la interfaz

### **Sistema de Backup:**
- ✅ **Seguridad de datos** de ejercicios
- ✅ **Portabilidad** entre instalaciones
- ✅ **Respaldo** ante pérdida de datos
- ✅ **Sincronización** entre profesores

---

## 📝 **Notas de Desarrollo:**

- **Prioridad Alta:** Sistema de colores (impacto visual inmediato)
- **Prioridad Media:** Backup de ejercicios (funcionalidad técnica)
- **Compatibilidad:** Mantener con versiones existentes
- **Testing:** Probar en entorno de desarrollo antes de producción

---

*Documento creado: 2025*  
*Última actualización: 2025*  
*Estado: Pendiente de implementación*
