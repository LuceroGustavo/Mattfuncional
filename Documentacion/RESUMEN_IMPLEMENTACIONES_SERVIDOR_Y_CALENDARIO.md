# Resumen: implementaciones servidor y calendario

Referencia rápida de lo que está implementado y qué scripts usar. Para más detalle: [DESPLIEGUE-SERVIDOR.md](servidor/DESPLIEGUE-SERVIDOR.md), [AYUDA_MEMORIA.md](AYUDA_MEMORIA.md).

---

## Menú del servidor (`./mattfuncional`)

| Opción | Acción | Estado |
|--------|--------|--------|
| **12** | Ver logs en vivo (streaming, `tail -f`) | ✅ Implementado |
| **13** | Instalar MySQL Workbench | ✅ Implementado |
| **14** | Ejecutar MySQL Workbench (requiere X11 si es remoto) | ✅ Implementado |

Desde casa (SSH sin restricciones): opción 12 para logs; 13 para instalar Workbench; 14 para abrir Workbench (con `ssh -X` si querés la ventana en tu PC).

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
