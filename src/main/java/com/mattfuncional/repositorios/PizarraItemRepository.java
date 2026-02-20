package com.mattfuncional.repositorios;

import com.mattfuncional.entidades.PizarraItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PizarraItemRepository extends JpaRepository<PizarraItem, Long> {

    @Query("SELECT pi FROM PizarraItem pi LEFT JOIN FETCH pi.exercise e LEFT JOIN FETCH e.imagen WHERE pi.columna.id = :columnaId ORDER BY pi.orden ASC")
    List<PizarraItem> findByColumnaIdOrderByOrdenAsc(@Param("columnaId") Long columnaId);
}
