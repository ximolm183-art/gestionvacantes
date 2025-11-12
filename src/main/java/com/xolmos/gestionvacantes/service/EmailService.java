package com.xolmos.gestionvacantes.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.from-name}")
    private String fromName;

    /**
     * Notificar a aspirante que su solicitud fue ACEPTADA
     */
    public boolean notificarSolicitudAceptada(String emailAspirante, String nombreAspirante,
                                              String tituloVacante, String empresaEmpleador) {
        String asunto = "¡Felicidades! Tu solicitud fue aceptada - " + tituloVacante;

        String contenidoHTML = construirEmailSolicitudAceptada(nombreAspirante, tituloVacante, empresaEmpleador);

        return enviarEmailHTML(emailAspirante, asunto, contenidoHTML);
    }

    /**
     * Notificar a aspirante que su solicitud fue RECHAZADA
     */
    public boolean notificarSolicitudRechazada(String emailAspirante, String nombreAspirante,
                                               String tituloVacante, String empresaEmpleador) {
        String asunto = "Actualización de tu solicitud - " + tituloVacante;

        String contenidoHTML = construirEmailSolicitudRechazada(nombreAspirante, tituloVacante, empresaEmpleador);

        return enviarEmailHTML(emailAspirante, asunto, contenidoHTML);
    }

    /**
     * Enviar email HTML (con formato)
     */
    private boolean enviarEmailHTML(String destinatario, String asunto, String contenidoHTML) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(contenidoHTML, true);

            mailSender.send(message);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error al enviar email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String construirEmailSolicitudAceptada(String nombreAspirante, String tituloVacante, String empresa) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
            </head>
            <body style="font-family: Arial, sans-serif; padding: 20px;">
                <div style="max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px;">
                    <h1 style="color: #28a745;">🎉 ¡Felicidades!</h1>
                    
                    <p>Estimado/a <strong>%s</strong>,</p>
                    
                    <div style="background: #d4edda; padding: 15px; border-radius: 5px; margin: 20px 0;">
                        <h3 style="color: #155724;">✅ Tu solicitud ha sido ACEPTADA</h3>
                    </div>
                    
                    <div style="background: #f8f9fa; padding: 15px; border-radius: 5px; margin: 15px 0;">
                        <h3 style="color: #667eea;">%s</h3>
                        <p><strong>Empresa:</strong> %s</p>
                    </div>
                    
                    <p>Nos complace informarte que tu perfil ha sido seleccionado. El empleador se pondrá en contacto contigo próximamente.</p>
                    
                    <p style="margin-top: 30px;">
                        ¡Mucho éxito!<br>
                        <strong>Equipo de Gestión de Vacantes</strong>
                    </p>
                </div>
            </body>
            </html>
            """.formatted(nombreAspirante, tituloVacante, empresa);
    }

    private String construirEmailSolicitudRechazada(String nombreAspirante, String tituloVacante, String empresa) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
            </head>
            <body style="font-family: Arial, sans-serif; padding: 20px;">
                <div style="max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px;">
                    <h1 style="color: #6c757d;">📋 Actualización de Solicitud</h1>
                    
                    <p>Estimado/a <strong>%s</strong>,</p>
                    
                    <p>Gracias por tu interés en formar parte de nuestro equipo.</p>
                    
                    <div style="background: #f8f9fa; padding: 15px; border-radius: 5px; margin: 15px 0;">
                        <h3 style="color: #667eea;">%s</h3>
                        <p><strong>Empresa:</strong> %s</p>
                    </div>
                    
                    <p>En esta ocasión hemos decidido continuar con otros candidatos. Te invitamos a seguir explorando otras oportunidades.</p>
                    
                    <p style="margin-top: 30px;">
                        Te deseamos mucho éxito.<br>
                        <strong>Equipo de Gestión de Vacantes</strong>
                    </p>
                </div>
            </body>
            </html>
            """.formatted(nombreAspirante, tituloVacante, empresa);
    }
}