package com.magenta.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad para una cita.
 */
@Entity
@Table(name = "appointments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador de la cita

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user; // Usuario asociado a la cita

    @Column(nullable = false)
    private LocalDateTime appointmentDateTime; // Fecha y hora de la cita

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status; // Estado de la cita

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventType eventType; // Tipo de evento de la cita

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionnaireResponseEntity> responses; // Respuestas del cuestionario asociadas a la cita

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppointmentNotificationEntity> notifications; // Notificaciones asociadas a la cita

    @Column(nullable = false)
    private LocalDateTime createdAt; // Fecha y hora de creación de la cita

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}