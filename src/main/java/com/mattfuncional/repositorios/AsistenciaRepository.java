package com.mattfuncional.repositorios;

import com.mattfuncional.entidades.Asistencia;
import com.mattfuncional.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    List<Asistencia> findByUsuarioOrderByFechaDesc(Usuario usuario);
    List<Asistencia> findByUsuario_IdOrderByFechaDesc(Long usuarioId);
    List<Asistencia> findByUsuarioAndFecha(Usuario usuario, LocalDate fecha);
    List<Asistencia> findByFechaBetween(LocalDate inicio, LocalDate fin);

    /** Carga asistencias con usuario inicializado (evita LazyInitialization al construir el mapa). */
    @Query("SELECT a FROM Asistencia a LEFT JOIN FETCH a.usuario WHERE a.fecha BETWEEN :inicio AND :fin")
    List<Asistencia> findByFechaBetweenWithUsuario(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
} 