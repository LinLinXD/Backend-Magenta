package com.magenta.controller;

import com.magenta.dto.AppointmentNotificationDTO;
import com.magenta.service.AppointmentNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador para la gestión de notificaciones de citas.
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final AppointmentNotificationService notificationService; // Usar el service en lugar del repositorio directamente

    /**
     * Obtiene las notificaciones no leídas de un usuario.
     *
     * @param username el nombre de usuario
     * @return una lista de notificaciones no leídas
     */
    @GetMapping("/unread/{username}")
    public ResponseEntity<List<AppointmentNotificationDTO>> getUnreadNotifications(
            @PathVariable String username) {
        try {
            List<AppointmentNotificationDTO> notifications = notificationService.getUnreadNotifications(username);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtiene el conteo de notificaciones no leídas de un usuario.
     *
     * @param username el nombre de usuario
     * @return el conteo de notificaciones no leídas
     */
    @GetMapping("/count/{username}")
    public ResponseEntity<Long> getUnreadNotificationCount(@PathVariable String username) {
        try {
            long count = notificationService.getUnreadNotificationCount(username);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Marca una notificación como leída.
     *
     * @param notificationId el ID de la notificación
     * @return una respuesta vacía indicando éxito o error
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long notificationId) {
        try {
            notificationService.markNotificationAsRead(notificationId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Marca todas las notificaciones de un usuario como leídas.
     *
     * @param username el nombre de usuario
     * @return una respuesta vacía indicando éxito o error
     */
    @PutMapping("/user/{username}/read-all")
    public ResponseEntity<Void> markAllNotificationsAsRead(@PathVariable String username) {
        try {
            notificationService.markAllNotificationsAsRead(username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}