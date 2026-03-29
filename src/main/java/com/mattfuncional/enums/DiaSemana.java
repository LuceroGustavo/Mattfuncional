package com.mattfuncional.enums;

public enum DiaSemana {
    LUNES,
    MARTES,
    MIERCOLES,
    JUEVES,
    VIERNES,
    SABADO,
    DOMINGO;

    /** Etiqueta corta para el calendario en vista móvil / entorno 2 (≤991px). */
    public String getAbrevCalendario() {
        return switch (this) {
            case LUNES -> "LUN";
            case MARTES -> "MAR";
            case MIERCOLES -> "MIE";
            case JUEVES -> "JUE";
            case VIERNES -> "VIE";
            case SABADO -> "SAB";
            case DOMINGO -> "DOM";
        };
    }
}