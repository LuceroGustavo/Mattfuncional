package com.mattfuncional.repositorios;

import com.mattfuncional.entidades.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SerieRepository extends JpaRepository<Serie, Long> {

    /** Carga una serie con sus SerieEjercicios y Exercise para evitar LazyInitializationException al editar. */
    @Query("SELECT s FROM Serie s LEFT JOIN FETCH s.serieEjercicios se LEFT JOIN FETCH se.exercise WHERE s.id = :id")
    Optional<Serie> findByIdWithSerieEjercicios(@Param("id") Long id);

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