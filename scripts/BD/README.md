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
| 0 (opcional) | `00_limpiar_datos_prueba_matt.sql` | Borra solo lo generado por estos scripts (no toca ejercicios ni usuarios reales) |
| 1 | `01_alumnos_prueba_15.sql` | **15** alumnos (`test_matt_pf_*@mattfuncional.test`) |
| 2 | `02_series_prueba_15.sql` | **15** series **plantilla** (sin `rutina_id`), con ejercicios del sistema |
| 3 | `03_rutinas_prueba_10.sql` | **10** rutinas plantilla + `rutina_categoria` + copia de series dentro de cada rutina |

Ejecutá **solo el paso que necesites** repetir tras limpiar (p. ej. después de `00`, volver **01 → 02 → 03** en orden).

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
- Correos de prueba: `test_matt_pf_01@mattfuncional.test` … `test_matt_pf_15@mattfuncional.test`.
- Tokens de rutina: hash MD5 fijo de 32 caracteres (único por índice).

## Notas

- Inspirado en `APP referencia/Migymvirtual/scripts/BD/`, adaptado a tablas/columnas de **Mattfuncional** (`categoria` con `profesor_id` NULL para sistema, tabla `rutina_categoria`, etc.).
- Si falla el paso 2: comprobá que existan al menos **15** filas en `exercise` con `profesor_id IS NULL` (predeterminados).
- **Alumnos en el panel:** el listado por profesor ya no depende de caché (los scripts SQL no la invalidaban y el dashboard podía seguir mostrando **0** con datos en `usuario`). Con la app actualizada, un **refresh** basta tras cargar `01_…`.
