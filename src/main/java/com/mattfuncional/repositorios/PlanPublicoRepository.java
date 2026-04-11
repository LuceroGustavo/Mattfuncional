package com.mattfuncional.repositorios;

import com.mattfuncional.entidades.PlanPublico;
import com.mattfuncional.enums.TipoPlanPublico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlanPublicoRepository extends JpaRepository<PlanPublico, Long> {
    List<PlanPublico> findByActivoTrueOrderByOrdenAsc();
    List<PlanPublico> findAllByOrderByOrdenAsc();

    @Query("SELECT MAX(p.orden) FROM PlanPublico p")
    Integer findMaxOrden();

    @Modifying
    @Query("UPDATE PlanPublico p SET p.tipo = :def WHERE p.tipo IS NULL")
    int setDefaultTipoWhereNull(@Param("def") TipoPlanPublico def);
}
