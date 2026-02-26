package com.mattfuncional.servicios;

import com.mattfuncional.entidades.PlanPublico;
import com.mattfuncional.repositorios.PlanPublicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlanPublicoService {

    @Autowired
    private PlanPublicoRepository planPublicoRepository;

    public List<PlanPublico> getPlanesActivosParaPublica() {
        return planPublicoRepository.findByActivoTrueOrderByOrdenAsc();
    }

    public List<PlanPublico> getAllPlanes() {
        return planPublicoRepository.findAllByOrderByOrdenAsc();
    }

    public PlanPublico getById(Long id) {
        return planPublicoRepository.findById(id).orElse(null);
    }

    @Transactional
    public PlanPublico guardar(PlanPublico plan) {
        return planPublicoRepository.save(plan);
    }

    @Transactional
    public void eliminar(Long id) {
        planPublicoRepository.deleteById(id);
    }

    /** Crea los 4 planes iniciales si no existen. */
    @Transactional
    public void asegurarPlanesIniciales() {
        if (planPublicoRepository.count() > 0) {
            return;
        }
        crearPlan("1 vez por semana", "Acceso una vez por semana.", 15000.0, 1, 0);
        crearPlan("2 veces por semana", "Acceso dos veces por semana.", 25000.0, 2, 1);
        crearPlan("3 veces por semana", "Acceso tres veces por semana.", 35000.0, 3, 2);
        crearPlan("Opción libre", "Acceso libre sin restricción de días.", 45000.0, null, 3);
    }

    private void crearPlan(String nombre, String descripcion, Double precio, Integer vecesPorSemana, int orden) {
        PlanPublico p = new PlanPublico();
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setPrecio(precio);
        p.setVecesPorSemana(vecesPorSemana);
        p.setOrden(orden);
        p.setActivo(true);
        planPublicoRepository.save(p);
    }
}
