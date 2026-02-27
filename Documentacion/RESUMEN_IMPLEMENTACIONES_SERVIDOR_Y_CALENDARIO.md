# Resumen: implementaciones servidor y calendario

Referencia rápida de lo que está implementado y qué scripts usar. Para más detalle: [DESPLIEGUE-SERVIDOR.md](servidor/DESPLIEGUE-SERVIDOR.md), [PLAN_DE_DESARROLLO_UNIFICADO.md](PLAN_DE_DESARROLLO_UNIFICADO.md).

---

## Menú del servidor (`./mattfuncional`)

Opciones 1–11: parar, actualizar, compilar, iniciar, despliegue completo, estado, logs, reiniciar, información, espacio en disco, salir. Ver [DESPLIEGUE-SERVIDOR.md](servidor/DESPLIEGUE-SERVIDOR.md) para la lista completa. *No se modificará por ahora el menú (sin Workbench ni scripts adicionales).*

---

## Scripts SQL en `scripts/servidor/`

| Script | Uso |
|--------|-----|
| **limpiar_duplicados_slot_config.sql** | Elimina duplicados en `slot_config` (mismo dia + hora_inicio). Ejecutar si el calendario falla por duplicados en esa tabla. |
| **consultar_duplicados_usuario.sql** | Solo lectura: lista correos con más de un registro en `usuario`. Sirve para detectar duplicados antes de corregir desde la app o a mano. |

Ejemplo en el servidor (después de `cd /root/mattfuncional`):

```bash
mysql -u mattfuncional_user -p mattfuncional < scripts/servidor/limpiar_duplicados_slot_config.sql
mysql -u mattfuncional_user -p mattfuncional < scripts/servidor/consultar_duplicados_usuario.sql
```

---

## Capacidad máxima por slot (calendario semanal)

- **Lectura:** `SlotConfigService.getCapacidadMaxima` usa `findFirstByDiaAndHoraInicio` → no falla si hay duplicados en BD.
- **Escritura:** `SlotConfigService.setCapacidadMaxima` busca todos los registros para (dia, horaInicio); actualiza el primero, guarda y **elimina el resto**. Así se evita crear nuevos duplicados y se limpian al guardar.
- Repositorio: `findFirstByDiaAndHoraInicio` y `findAllByDiaAndHoraInicio` en `SlotConfigRepository`.

---

## Aviso de duplicados en la app

- En **Usuarios del sistema** (logueado como developer o admin): si hay correos duplicados en `usuario` o `profesor`, se muestra un aviso amarillo con la lista.
- Al arrancar la app se escribe un WARN en log si existen esos duplicados.

---

*Última actualización: Feb 2026.*
