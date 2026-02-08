package com.mattfuncional.repositorios;

import com.mattfuncional.entidades.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SerieRepository extends JpaRepository<Serie, Long> {

    // Buscar series por rutina ordenadas por orden
    List<Serie> findByRutinaIdOrderByOrdenAsc(Long rutinaId);

    // Buscar series por rutina
    List<Serie> findByRutinaId(Long rutinaId);

    // Contar series por rutina
    long countByRutinaId(Long rutinaId);

    // Buscar serie por nombre y rutina
    Serie findByNombreAndRutinaId(String nombre, Long rutinaId);

    // Buscar series plantilla por profesor
    List<Serie> findByProfesorIdAndEsPlantillaTrue(Long profesorId);

    // Buscar series plantilla por profesor y nombre
    List<Serie> findByProfesorIdAndEsPlantillaTrueAndNombreContainingIgnoreCase(Long profesorId, String nombre);

    // Buscar todas las series plantilla
    List<Serie> findByEsPlantillaTrue();

    // Buscar series plantilla por creador
    List<Serie> findByCreadorAndEsPlantillaTrue(String creador);

    List<Serie> findByProfesorId(Long profesorId);

    @Transactional
    @Modifying
    void deleteByRutinaId(Long rutinaId);
}