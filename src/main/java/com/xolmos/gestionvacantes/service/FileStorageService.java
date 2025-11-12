package com.xolmos.gestionvacantes.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    @Value("${app.upload.dir:uploads/cv}")
    private String uploadDir;

    /**
     * Guardar archivo CV
     */
    public String guardarCV(MultipartFile file) throws IOException {
        // Validar que el archivo no esté vacío
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        // Validar que sea PDF
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new IllegalArgumentException("El archivo debe ser un PDF");
        }

        // Crear directorio si no existe
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            System.out.println(created ? "✅ Directorio creado: " + directory.getAbsolutePath()
                    : "❌ No se pudo crear directorio");
        }

        // Generar nombre único para el archivo
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName = System.currentTimeMillis() + "_" +
                (originalFilename != null ? originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_") : "cv.pdf");

        // Ruta completa del archivo
        Path filePath = Paths.get(uploadDir, fileName);

        // Copiar el archivo
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        System.out.println("✅ Archivo guardado en: " + filePath.toAbsolutePath());

        // Retornar la ruta relativa
        return uploadDir + "/" + fileName;
    }

    /**
     * Eliminar archivo CV
     */
    public boolean eliminarCV(String rutaArchivo) {
        try {
            if (rutaArchivo == null || rutaArchivo.isEmpty()) {
                return false;
            }

            Path path = Paths.get(rutaArchivo);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            System.err.println("❌ Error al eliminar archivo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtener ruta absoluta del directorio de uploads
     */
    public String getUploadDirAbsolutePath() {
        return new File(uploadDir).getAbsolutePath();
    }
}