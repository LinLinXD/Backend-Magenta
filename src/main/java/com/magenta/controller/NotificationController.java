package com.magenta.controller;

import com.magenta.dto.AppointmentNotificationDTO;
import com.magenta.service.AppointmentNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final AppointmentNotificationService notificationService;

    // Obtener notificaciones no leídas de un usuario
    @GetMapping("/unread/{username}")
    public ResponseEntity<List<AppointmentNotificationDTO>> getUnreadNotifications(
            @PathVariable String username) {
        try {
            List<AppointmentNotificationDTO> notifications =
                    notificationService.getUnreadNotifications(username);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Error al obtener las notificaciones: ", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // Marcar una notificación como leída
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long notificationId) {
        try {
            notificationService.markNotificationAsRead(notificationId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al marcar la notificación como leída: ", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // Marcar todas las notificaciones de un usuario como leídas
    @PutMapping("/user/{username}/read-all")
    public ResponseEntity<Void> markAllNotificationsAsRead(@PathVariable String username) {
        try {
            notificationService.markAllNotificationsAsRead(username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al marcar todas las notificaciones como leídas: ", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtener el contador de notificaciones no leídas
    @GetMapping("/count/{username}")
    public ResponseEntity<Long> getUnreadNotificationCount(@PathVariable String username) {
        try {
            long count = notificationService.getUnreadNotificationCount(username);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error al obtener el contador de notificaciones: ", e);
            return ResponseEntity.badRequest().build();
        }
    }
}