package com.mattfuncional.repositorios;

import com.mattfuncional.entidades.SerieEjercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SerieEjercicioRepository extends JpaRepository<SerieEjercicio, Long> {

    @Modifying
    void deleteBySerieId(Long serieId);

    /** Elimina todos los SerieEjercicio (referencian ejercicios; para suplantar backup). */
    @Modifying
    @Query("DELETE FROM SerieEjercicio")
    int deleteAllWithExercise();
}