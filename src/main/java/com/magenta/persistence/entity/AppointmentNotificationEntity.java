package com.magenta.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment_notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentNotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private AppointmentEntity appointment;

    @Column(nullable = false)
    private LocalDateTime notificationTime;

    @Column(name = "is_sent", nullable = false)  // Cambiado de 'sent' a 'is_sent'
    private boolean sent;

    @Column(name = "is_read", nullable = false)  // Cambiado de 'read' a 'is_read'
    private boolean read;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type;
}