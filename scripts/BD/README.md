# Scripts SQL — datos de prueba progresivos (Mattfuncional)

Para probar **backup**, **panel de alumnos**, **rutinas** y **series** sin cargar a mano todo el escenario.

## Requisitos previos

1. Base de datos **`mattfuncional`** (según `application.properties`).
2. **Arrancar la aplicación al menos una vez** tras un DB vacío (`ddl-auto=update`) para que existan:
   - tablas;
   - usuario/profesor inicial (`profesor@mattfuncional.com` + entidad `profesor`);
   - **60 ejercicios** predeterminados, **grupos** y **categorías** de sistema (DataInitializer + carga de ejercicios).

## Orden de ejecución (progresivo)

| Paso | Archivo | Qué carga |
|------|---------|-----------|
| 0 (opcional) | `00_limpiar_datos_prueba_matt.sql` | Borra solo lo generado por estos scripts (Matt PF rutinas/series + alumnos test; no toca ejercicios ni cuenta admin) |
| 0b (opcional) | `98_borrar_todos_alumnos.sql` | Borra **todos** los usuarios `rol = ALUMNO` (y FK: asistencias, rutinas asignadas, mediciones, etc.). **No** borra ADMIN/AYUDANTE/DEVELOPER. Útil antes de recargar `01` desde cero. |
| 1 | `01_alumnos_prueba_15.sql` | **30** alumnos (`test_matt_pf_01` … `30`) con `tipo_asistencia`, fechas, contacto emergencia, `detalle_asistencia` + horarios para **calendario**. Incluye `SQL_SAFE_UPDATES=0` para Workbench. El `…_08` sigue **INACTIVO** sin horarios. |
| 2 | `02_series_prueba_15.sql` | **15** series **plantilla** (sin `rutina_id`), con ejercicios del sistema |
| 3 | `03_rutinas_prueba_10.sql` | **10** rutinas plantilla + `rutina_categoria` + copia de series dentro de cada rutina |
| 5 (reparación) | `05_reparar_serie_ejercicios_nulos.sql` | **Solo borra** filas con `exercise_id IS NULL` — **no** crea enlaces. Si todo era NULL, tras 05 el panel mostrará **0** ejercicios; volvé a ejecutar **02** (y **03**) con la app ya habiendo cargado ejercicios |

Ejecutá **solo el paso que necesites** repetir tras limpiar (p. ej. después de `00`, volver **01 → 02 → 03** en orden).

**Importante — paso 2:** tiene que haber filas en `exercise`. Arrancá la app al menos una vez y esperá la carga inicial (≈60 ejercicios con `ddl-auto=update`). `@ex_min` usa primero `profesor_id IS NULL` y, si no hay ninguno, `MIN(id)` de **toda** la tabla `exercise`. Si `exercise` está **vacía**, `@ex_min` queda NULL: **no ejecutes** el `02` hasta tener ejercicios. Al terminar el `02`, el resultado `serie_ejercicio_MATT_PF_con_exercise_id_NULL` **debe ser 0**.

**Importante — paso 5:** solo **elimina** filas con `exercise_id` NULL; **no** crea enlaces. Si corrés `05` cuando **todas** esas filas eran NULL, es normal que en «Mis series» veas **0** ejercicios en todo: hay que **volver a ejecutar `02`** (y **`03`** si aplica), con datos válidos en `exercise`; muchas veces conviene **`00`** antes para no duplicar series Matt PF.

**Backup:** no uses **solo «Series» + Suplantar** sin **Ejercicios**; las series quedan sin vínculos válidos.

## Cómo ejecutar (MySQL)

**Workbench / DBeaver / CLI:** abrir cada `.sql` y ejecutar el archivo completo.

```bash
mysql -u root -p mattfuncional < scripts/BD/01_alumnos_prueba_15.sql
mysql -u root -p mattfuncional < scripts/BD/02_series_prueba_15.sql
mysql -u root -p mattfuncional < scripts/BD/03_rutinas_prueba_10.sql
```

Ajustar usuario/clave si no usás `root`/`root`.

## Convenciones

- Prefijo de negocio: nombres **`Matt PF Serie …`**, **`Matt PF Rutina …`**.
- Correos de prueba: `test_matt_pf_01@mattfuncional.test` … `test_matt_pf_30@mattfuncional.test`.
- Tokens de rutina: hash MD5 fijo de 32 caracteres (único por índice).

## Notas

- Inspirado en scripts de prueba de un fork tipo MiGymVirtual (histórico), adaptado a tablas/columnas de **Mattfuncional** (`categoria` con `profesor_id` NULL para sistema, tabla `rutina_categoria`, etc.).
- **Si falla el paso 2 con mensaje de error FK:** Asegurate que la tabla `exercise` NO esté vacía. El script ahora valida esto al inicio y lanza error claro si `exercise` está vacía. Ver solución abajo.
- **Alumnos en el panel:** el listado por profesor ya no depende de caché (los scripts SQL no la invalidaban y el dashboard podía seguir mostrando **0** con datos en `usuario`). Con la app actualizada, un **refresh** basta tras cargar `01_…`.

## ⚠️ FIXES APLICADOS (abril 2026)

### Import ZIP (aplicación)
Al restaurar desde backup ZIP, si el archivo incluye **`categorias.json`**, la app importa categorías **aunque no marques** Rutinas ni Series (útil al importar solo ejercicios). Ver `CHANGELOG.md` **[2026-04-08]**.

### Paso 2 - Validación de ejercicios
El script `02_series_prueba_15.sql` ahora valida que la tabla `exercise` tenga datos **antes** de crear variables. Si está vacía, se detiene con error claro:
```
ERROR: La tabla exercise está vacía. DEBES ejecutar la app primero para que genere los 60 ejercicios predeterminados.
```

**Solución:** Si ves este error, simplemente arrancar Spring una vez e intenta de nuevo.

### Paso 3 - Garantizar categorías
El script `03_rutinas_prueba_10.sql` ahora recrea las 5 categorías de sistema al inicio (con `INSERT IGNORE`). Esto evita errores FK si alguien borra categorías manualmente.

### Paso 0 - Orden de borrado mejorado
El script `00_limpiar_datos_prueba_matt.sql` ahora respeta el orden correcto de constraints FK al borrar:
1. `serie_ejercicio` → `serie`
2. `rutina_categoria` → `rutina`
3. `serie` (sueltas)
4. `rutina`

---

## Verificar series tras SQL o backup

Si en **`/series/ver/{id}`** ves **«0 ejercicios»** pero la serie debería tener filas:

1. **Datos:** comprobar que existan vínculos en `serie_ejercicio` para ese `serie_id` (los scripts `02_…` insertan 3–4 ejercicios por serie de biblioteca):

   ```sql
   SELECT COUNT(*) FROM serie_ejercicio WHERE serie_id = 92;
   SELECT se.*, e.name FROM serie_ejercicio se JOIN exercise e ON e.id = se.exercise_id WHERE se.serie_id = 92;
   ```

   Si el `COUNT` es **0**, el problema es de datos (orden de scripts, import incompleto, o `00_limpiar` sin volver a cargar `02`). Si el `COUNT` es **> 0** y la app seguía mostrando 0, era un **bug de carga JPA** (consulta con doble `JOIN FETCH` sin `DISTINCT`), corregido en `SerieRepository.findByIdWithSerieEjercicios`.

2. **Reiniciar** la app tras cambios en código; recargar la página con Ctrl+F5.
