package com.xolmos.gestionvacantes.model.enums;

public enum Role {
    ASPIRANTE("Aspirante - Busco empleo"),
    EMPLEADOR("Empleador - Ofrezco empleo"),
    ADMIN("Administrador");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    Role() {
        this.displayName = this.name();
    }

    public String getDisplayName() {
        return displayName;
    }
    }
