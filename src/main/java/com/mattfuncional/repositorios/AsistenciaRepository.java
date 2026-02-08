package com.mattfuncional.repositorios;

import com.mattfuncional.entidades.Asistencia;
import com.mattfuncional.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    List<Asistencia> findByUsuario(Usuario usuario);
    List<Asistencia> findByUsuarioAndFecha(Usuario usuario, LocalDate fecha);
} 