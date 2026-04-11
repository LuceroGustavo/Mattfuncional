package com.mattfuncional.entidades;

import com.mattfuncional.enums.TipoPlanPublico;
import jakarta.persistence.*;

@Entity
@Table(name = "plan_publico")
public class PlanPublico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** PLAN = tarifa habitual; PROMOCION = título + imagen + texto (ej. merchandising). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoPlanPublico tipo = TipoPlanPublico.PLAN;

    /** Ruta servida por la app, ej. /media/promociones/abc.webp (solo PROMOCION). */
    @Column(name = "ruta_imagen", length = 500)
    private String rutaImagen;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    /** En PLAN es el precio del plan; en PROMOCION puede ser 0 o precio opcional a mostrar. */
    @Column(nullable = false)
    private Double precio = 0d;

    /** Texto libre para mostrar (ej. Gratis, Consultar, $15.000). Si está vacío se usa {@link #precio} formateado. */
    @Column(name = "precio_etiqueta", length = 160)
    private String precioEtiqueta;

    /** Veces por semana (1, 2, 3) o null para "opción libre". */
    @Column(name = "veces_por_semana")
    private Integer vecesPorSemana;

    @Column(nullable = false)
    private int orden = 0;

    @Column(nullable = false)
    private boolean activo = true;

    public PlanPublico() {
    }

    public TipoPlanPublico getTipo() {
        return tipo;
    }

    public void setTipo(TipoPlanPublico tipo) {
        this.tipo = tipo != null ? tipo : TipoPlanPublico.PLAN;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getPrecioEtiqueta() {
        return precioEtiqueta;
    }

    public void setPrecioEtiqueta(String precioEtiqueta) {
        this.precioEtiqueta = precioEtiqueta;
    }

    public Integer getVecesPorSemana() {
        return vecesPorSemana;
    }

    public void setVecesPorSemana(Integer vecesPorSemana) {
        this.vecesPorSemana = vecesPorSemana;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
