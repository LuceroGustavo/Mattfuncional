# Exportación de alumnos a Excel – columnas definidas

Lista final de columnas para el Excel de exportación de alumnos (según ajustes del usuario).

---

## 1. Título y cabecera

- **Primera fila:** "Exportación de alumnos fecha dd/MM/yyyy" (fecha del día de exportación).
- **Fila en blanco** y luego la fila de cabecera con los nombres de columna.

---

## 2. Datos del alumno (Usuario)

| Columna | Atributo | Notas |
|---------|----------|--------|
| Nombre | nombre | Nombre completo |
| Correo | correo | Email |
| Celular | celular | Teléfono |
| Edad | edad | Años |
| Sexo | sexo | M/F u otro |
| Estado | estadoAlumno | ACTIVO / INACTIVO |
| **Fecha de alta** | fechaAlta | Fecha en que se dio de alta al alumno |
| Fecha baja | fechaBaja | Solo si la tiene |
| Tipo de asistencia | tipoAsistencia | **Virtual** (ONLINE), **Presencial**, **Semipresencial** |
| Días y horarios | diasHorariosAsistencia | Solo si tipo = Presencial o Semipresencial. Ej: "Lunes 10:00-11:00, Martes 13:00-14:00" |
| Objetivos personales | objetivosPersonales | Texto libre |
| Restricciones médicas | restriccionesMedicas | Texto libre |
| Notas profesor | notasProfesor | Notas internas |

**No se exportan:** Peso actual, Detalle asistencia, Contacto emergencia nombre, Contacto emergencia teléfono, contraseña, avatar, historial.

---

## 3. Cantidad de asignaciones

| Columna | Cálculo |
|---------|--------|
| Cantidad de asignaciones | Número de rutinas asignadas al alumno (no plantillas). No se exporta el listado de rutinas. |

---

## 4. Último trabajo

Una sola columna al final: **Último trabajo**.

- **Origen:** Último registro de asistencia/progreso del alumno (fecha, grupos musculares trabajados, observaciones), el mismo que se muestra en la tarjeta "Último trabajo" en la ficha del alumno.
- **Formato en la celda:**  
  - Primera línea: fecha del último trabajo (dd/MM/yy, ej. 11/03/26).  
  - Segunda línea: grupos y observaciones (ej. "CARDIO - CORE - trabajo muy bien").  
- Si el alumno no tiene ningún registro de progreso con datos, la celda queda vacía.
- La celda tiene ajuste de texto (wrap) para que se vean las dos líneas.

---

## Orden de columnas en el Excel

1. Nombre  
2. Correo  
3. Celular  
4. Edad  
5. Sexo  
6. Estado  
7. Fecha de alta  
8. Fecha baja  
9. Tipo de asistencia  
10. Días y horarios  
11. Objetivos personales  
12. Restricciones médicas  
13. Notas profesor  
14. Cantidad de asignaciones  
15. Último trabajo  

---

*Documento de referencia para la exportación a Excel en el panel de backup. Implementado en `AlumnoExportService`.*
