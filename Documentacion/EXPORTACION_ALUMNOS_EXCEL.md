# Exportación de alumnos a Excel – columnas definidas

Lista final de columnas para el Excel de exportación de alumnos (según ajustes del usuario).

---

## 1. Datos del alumno (Usuario)

| Columna | Atributo | Notas |
|---------|----------|--------|
| Nombre | nombre | Nombre completo |
| Correo | correo | Email |
| Celular | celular | Teléfono |
| Edad | edad | Años |
| Sexo | sexo | M/F u otro |
| Peso actual | peso | kg |
| Estado | estadoAlumno | ACTIVO / INACTIVO |
| **Fecha inicio** | fechaInicio | Se exporta |
| **Fecha baja** | fechaBaja | Solo si la tiene |
| **Tipo de asistencia** | tipoAsistencia | **Virtual** (ONLINE), **Presencial**, **Semipresencial** |
| Detalle asistencia | detalleAsistencia | Texto libre que puede llevar ya un resumen (ej: "Pase libre"). |
| Días y horarios | diasHorariosAsistencia | **Solo si tipo = Presencial o Semipresencial.** Días y franjas horarias en que asiste. Ejemplos de formato: *"Lunes, jueves y viernes 10:00-11:00"* o *"Lunes 10:00, Martes 13:00, Jueves 15:00"* (según tenga mismo horario todos los días o horarios distintos por día). Se arma a partir de `diasHorariosAsistencia` (dia + horaEntrada + horaSalida). No se exportan presentes/ausentes. |
| Objetivos personales | objetivosPersonales | Texto libre |
| Restricciones médicas | restriccionesMedicas | Texto libre |
| Notas profesor | notasProfesor | Notas internas |
| Contacto emergencia nombre | contactoEmergenciaNombre | |
| Contacto emergencia teléfono | contactoEmergenciaTelefono | |

**No se exporta**
- Fecha de alta
- Asistencias presentes o ausentes (ningún conteo ni historial de asistencia)
- Password, rol, avatar, historialEstado, historialAsistencia

---

## 2. Cantidad de asignaciones

| Columna | Cálculo |
|---------|--------|
| Cantidad de asignaciones | Número de rutinas asignadas al alumno (no plantillas). No se exporta el listado de rutinas. |

---

## 3. Últimas 3 evoluciones (MedicionFisica)

Por cada una de las últimas 3 mediciones (orden por fecha descendente):

- **Ev1** (más reciente): Ev1_fecha, Ev1_peso, Ev1_altura, Ev1_cintura, Ev1_pecho, Ev1_cadera, Ev1_biceps, Ev1_muslo  
- **Ev2**: Ev2_fecha, Ev2_peso, Ev2_altura, Ev2_cintura, Ev2_pecho, Ev2_cadera, Ev2_biceps, Ev2_muslo  
- **Ev3**: Ev3_fecha, Ev3_peso, Ev3_altura, Ev3_cintura, Ev3_pecho, Ev3_cadera, Ev3_biceps, Ev3_muslo  

Si hay menos de 3 mediciones, las columnas de Ev2/Ev3 quedan vacías.

---

## Orden sugerido de columnas en el Excel

1. Nombre  
2. Correo  
3. Celular  
4. Edad  
5. Sexo  
6. Peso actual  
7. Estado  
8. Fecha inicio  
9. Fecha baja  
10. Tipo de asistencia (Virtual / Presencial / Semipresencial)  
11. Detalle asistencia  
12. Días y horarios (para Presencial/Semipresencial: ej. "Lunes, jueves y viernes 10:00-11:00" o "Lunes 10:00, Martes 13:00, Jueves 15:00")  
13. Objetivos personales  
14. Restricciones médicas  
15. Notas profesor  
16. Contacto emergencia nombre  
17. Contacto emergencia teléfono  
18. Cantidad de asignaciones  
19. Ev1_fecha … Ev1_muslo  
20. Ev2_fecha … Ev2_muslo  
21. Ev3_fecha … Ev3_muslo  

---

*Documento de referencia para implementar la exportación a Excel en el panel de backup.*
