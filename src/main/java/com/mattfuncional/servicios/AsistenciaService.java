package com.mattfuncional.servicios;

import com.mattfuncional.entidades.Asistencia;
import com.mattfuncional.entidades.GrupoMuscular;
import com.mattfuncional.entidades.Usuario;
import com.mattfuncional.repositorios.AsistenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AsistenciaService {
    @Autowired
    private AsistenciaRepository asistenciaRepository;

    public Asistencia registrarAsistencia(Usuario usuario, LocalDate fecha, boolean presente, String observaciones) {
        List<Asistencia> existentes = asistenciaRepository.findByUsuarioAndFecha(usuario, fecha);
        if (existentes != null && !existentes.isEmpty()) {
            return null;
        }
        Asistencia asistencia = new Asistencia(fecha, presente, observaciones, usuario);
        return asistenciaRepository.save(asistencia);
    }

    public List<Asistencia> obtenerAsistenciasPorUsuario(Usuario usuario) {
        return asistenciaRepository.findByUsuarioOrderByFechaDesc(usuario);
    }

    public List<Asistencia> obtenerAsistenciasPorUsuarioId(Long usuarioId) {
        if (usuarioId == null) {
            return java.util.Collections.emptyList();
        }
        return asistenciaRepository.findByUsuario_IdOrderByFechaDesc(usuarioId);
    }

    public List<Asistencia> obtenerAsistenciaPorUsuarioYFecha(Usuario usuario, LocalDate fecha) {
        return asistenciaRepository.findByUsuarioAndFecha(usuario, fecha);
    }

    /**
     * Crea o actualiza el registro de progreso/asistencia para un alumno en una fecha.
     * Si ya existe registro para ese usuario y fecha, actualiza presente, observaciones y grupos trabajados.
     */
    @Transactional
    public Asistencia guardarOActualizarProgreso(Usuario alumno, LocalDate fecha, boolean presente, String observaciones, Set<GrupoMuscular> gruposTrabajados) {
        List<Asistencia> existentes = asistenciaRepository.findByUsuarioAndFecha(alumno, fecha);
        Asistencia a;
        if (existentes != null && !existentes.isEmpty()) {
            a = existentes.get(0);
        } else {
            a = new Asistencia(fecha, presente, observaciones != null ? observaciones.trim() : null, alumno);
        }
        a.setPresente(presente);
        a.setObservaciones(observaciones != null && !observaciones.isBlank() ? observaciones.trim() : null);
        a.setGruposTrabajados(gruposTrabajados != null ? gruposTrabajados : new java.util.HashSet<>());
        return asistenciaRepository.save(a);
    }

    public boolean eliminarAsistenciaDeHoy(Usuario usuario) {
        List<Asistencia> existentes = asistenciaRepository.findByUsuarioAndFecha(usuario, LocalDate.now());
        if (existentes != null && !existentes.isEmpty()) {
            asistenciaRepository.deleteAll(existentes);
            return true;
        }
        return false;
    }

    /**
     * Registra ausente para (usuario, fecha) solo si aún no existe ningún registro.
     * No sobrescribe registros existentes (presente o ausente).
     */
    @Transactional
    public void registrarAusenteSiNoExiste(Usuario usuario, LocalDate fecha) {
        List<Asistencia> existentes = asistenciaRepository.findByUsuarioAndFecha(usuario, fecha);
        if (existentes == null || existentes.isEmpty()) {
            Asistencia a = new Asistencia(fecha, false, null, usuario);
            asistenciaRepository.save(a);
        }
    }

    /**
     * Devuelve un mapa clave "usuarioId_fecha" (ej. "5_2026-02-17") -> presente (true/false)
     * para todas las asistencias en el rango [inicio, fin]. Útil para pintar el calendario.
     * Usa query con JOIN FETCH de usuario para que el mapa se construya correctamente al volver a entrar.
     */
    @Transactional(readOnly = true)
    public Map<String, Boolean> getMapaPresentePorUsuarioYFecha(LocalDate inicio, LocalDate fin) {
        List<Asistencia> list = asistenciaRepository.findByFechaBetweenWithUsuario(inicio, fin);
        Map<String, Boolean> out = new HashMap<>();
        if (list != null) {
            for (Asistencia a : list) {
                if (a.getUsuario() != null && a.getUsuario().getId() != null && a.getFecha() != null) {
                    out.put(String.valueOf(a.getUsuario().getId()) + "_" + a.getFecha().toString(), a.isPresente());
                }
            }
        }
        return out;
    }
} 