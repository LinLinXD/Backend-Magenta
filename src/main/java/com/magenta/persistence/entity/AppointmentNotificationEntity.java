package com.magenta.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad para una notificación de cita.
 */
@Entity
@Table(name = "appointment_notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentNotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador de la notificación

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private AppointmentEntity appointment; // Cita asociada a la notificación

    @Column(nullable = false)
    private LocalDateTime notificationTime; // Hora de la notificación

    @Column(name = "is_sent", nullable = false)
    private boolean sent; // Indica si la notificación ha sido enviada

    @Column(name = "is_read", nullable = false)
    private boolean read; // Indica si la notificación ha sido leída

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type; // Tipo de notificación
}