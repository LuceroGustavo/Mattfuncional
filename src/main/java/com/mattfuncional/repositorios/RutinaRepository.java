package com.mattfuncional.repositorios;

import com.mattfuncional.entidades.Rutina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutinaRepository extends JpaRepository<Rutina, Long> {

    List<Rutina> findByUsuarioId(Long usuarioId); // ← ESTA ES LA CLAVE

    List<Rutina> findByProfesorId(Long profesorId);

    List<Rutina> findByUsuarioIdAndEstado(Long usuarioId, String estado);

    List<Rutina> findByProfesorIdAndEstado(Long profesorId, String estado);

    List<Rutina> findByEstado(String estado);

    // Buscar rutinas plantilla por profesor
    List<Rutina> findByProfesorIdAndEsPlantillaTrue(Long profesorId);

    // Buscar rutinas plantilla por profesor y categoría
    List<Rutina> findByProfesorIdAndEsPlantillaTrueAndCategoria(Long profesorId, String categoria);

    // Buscar rutinas plantilla por profesor y nombre
    List<Rutina> findByProfesorIdAndEsPlantillaTrueAndNombreContainingIgnoreCase(Long profesorId, String nombre);

    // Buscar todas las rutinas plantilla
    List<Rutina> findByEsPlantillaTrue();

    // Buscar rutinas plantilla por creador
    List<Rutina> findByCreadorAndEsPlantillaTrue(String creador);

    // Buscar rutinas asignadas a usuarios por profesor
    List<Rutina> findByProfesorIdAndEsPlantillaFalse(Long profesorId);

    // Buscar rutinas asignadas a un usuario específico (no plantillas)
    List<Rutina> findByUsuarioIdAndEsPlantillaFalse(Long usuarioId);
}
