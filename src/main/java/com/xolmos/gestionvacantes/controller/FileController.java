package com.xolmos.gestionvacantes.controller;

import com.xolmos.gestionvacantes.model.Solicitud;
import com.xolmos.gestionvacantes.service.SolicitudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class FileController {

    @Autowired
    private SolicitudService solicitudService;

    @GetMapping("/descargar-cv/{solicitudId}")
    public ResponseEntity<Resource> descargarCV(@PathVariable Long solicitudId) {
        try {
            System.out.println("🔍 Intentando descargar CV de solicitud: " + solicitudId);

            Solicitud solicitud = solicitudService.obtenerPorId(solicitudId);

            if (solicitud == null) {
                System.out.println("❌ Solicitud no encontrada");
                return ResponseEntity.notFound().build();
            }

            String rutaCV = solicitud.getCvAdjunto();
            System.out.println("📂 Ruta del CV: " + rutaCV);

            Path path = Paths.get(rutaCV);
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() && resource.isReadable()) {
                String filename = path.getFileName().toString();
                System.out.println("✅ CV encontrado: " + filename);

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(resource);
            } else {
                System.out.println("❌ El archivo no existe o no es legible");
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            System.err.println("❌ Error al descargar CV: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}