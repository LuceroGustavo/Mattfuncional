package com.mattfuncional.repositorios;

import com.mattfuncional.entidades.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByProfesorIsNullOrderByNombreAsc();

    List<Categoria> findByProfesorIdOrderByNombreAsc(Long profesorId);

    @Query("SELECT c FROM Categoria c WHERE c.profesor IS NULL OR c.profesor.id = :profesorId ORDER BY c.nombre")
    List<Categoria> findDisponiblesParaProfesor(@Param("profesorId") Long profesorId);

    Optional<Categoria> findFirstByNombreAndProfesorIsNull(String nombre);

    Optional<Categoria> findFirstByNombreAndProfesorId(String nombre, Long profesorId);

    void deleteByProfesor_Id(Long profesorId);

    @Query("SELECT COUNT(DISTINCT c.nombre) FROM Categoria c WHERE c.profesor IS NULL AND c.nombre IN :nombres")
    long countDistinctNombreSistemaPresentes(@Param("nombres") Collection<String> nombres);
}
