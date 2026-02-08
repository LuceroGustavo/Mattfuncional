package com.mattfuncional.servicios;

import com.mattfuncional.entidades.Asistencia;
import com.mattfuncional.entidades.Usuario;
import com.mattfuncional.repositorios.AsistenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class AsistenciaService {
    @Autowired
    private AsistenciaRepository asistenciaRepository;

    public Asistencia registrarAsistencia(Usuario usuario, LocalDate fecha, boolean presente, String observaciones) {
        // Validar si ya existe asistencia para ese usuario y fecha
        List<Asistencia> existentes = asistenciaRepository.findByUsuarioAndFecha(usuario, fecha);
        if (existentes != null && !existentes.isEmpty()) {
            // Ya existe, no registrar duplicado
            return null;
        }
        Asistencia asistencia = new Asistencia(fecha, presente, observaciones, usuario);
        return asistenciaRepository.save(asistencia);
    }

    public List<Asistencia> obtenerAsistenciasPorUsuario(Usuario usuario) {
        return asistenciaRepository.findByUsuario(usuario);
    }

    public List<Asistencia> obtenerAsistenciaPorUsuarioYFecha(Usuario usuario, LocalDate fecha) {
        return asistenciaRepository.findByUsuarioAndFecha(usuario, fecha);
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