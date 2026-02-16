package com.mattfuncional.servicios;

import com.mattfuncional.entidades.Asistencia;
import com.mattfuncional.entidades.GrupoMuscular;
import com.mattfuncional.entidades.Usuario;
import com.mattfuncional.repositorios.AsistenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
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
} 