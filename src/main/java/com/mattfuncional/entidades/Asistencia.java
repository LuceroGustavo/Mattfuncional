package com.mattfuncional.entidades;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Asistencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;
    private boolean presente;
    private String observaciones;

    @ManyToOne
    private Usuario usuario;

    public Asistencia() {}

    public Asistencia(LocalDate fecha, boolean presente, String observaciones, Usuario usuario) {
        this.fecha = fecha;
        this.presente = presente;
        this.observaciones = observaciones;
        this.usuario = usuario;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public boolean isPresente() { return presente; }
    public void setPresente(boolean presente) { this.presente = presente; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
} 