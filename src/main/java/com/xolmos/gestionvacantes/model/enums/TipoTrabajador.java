package com.xolmos.gestionvacantes.model.enums;

public enum TipoTrabajador {
    TIEMPO_COMPLETO("Tiempo Completo"),
    MEDIO_TIEMPO("Medio Tiempo"),
    PRACTICAS("Prácticas"),
    CONTRATO("Contrato"),
    FREELANCE("Freelance");

    private final String displayName;

    TipoTrabajador(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
