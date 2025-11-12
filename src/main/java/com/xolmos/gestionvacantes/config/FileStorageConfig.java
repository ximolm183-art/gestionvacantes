package com.xolmos.gestionvacantes.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;

public class FileStorageConfig {

    @Value("${app.upload.dir:uploads/cv}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        // Crear el directorio si no existe
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                System.out.println("✅ Directorio de uploads creado: " + directory.getAbsolutePath());
            } else {
                System.err.println("❌ No se pudo crear el directorio de uploads");
            }
        } else {
            System.out.println("✅ Directorio de uploads existe: " + directory.getAbsolutePath());
        }
    }

    public String getUploadDir() {
        return uploadDir;
    }
}
