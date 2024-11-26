package com.magenta.dto;

import com.magenta.persistence.entity.AppointmentEntity;
import com.magenta.persistence.entity.AppointmentStatus;
import com.magenta.persistence.entity.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Transfer Object (DTO) para la entidad Appointment.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    private Long id;
    private String username;
    private String name;  // Nombre del usuario
    private String email; // Email del usuario
    private LocalDateTime appointmentDateTime;
    private AppointmentStatus status;
    private EventType eventType;
    private List<QuestionnaireResponseDTO> responses;
    private LocalDateTime createdAt;
    private String error;

    /**
     * Convierte una entidad AppointmentEntity a un AppointmentDTO.
     *
     * @param entity la entidad AppointmentEntity a convertir
     * @return el objeto AppointmentDTO resultante
     */
    public static AppointmentDTO fromEntity(AppointmentEntity entity) {
        if (entity == null) {
            return null;
        }

        return AppointmentDTO.builder()
                .id(entity.getId())
                .username(entity.getUser() != null ? entity.getUser().getUsername() : null)
                .name(entity.getUser() != null ? entity.getUser().getName() : null)
                .email(entity.getUser() != null ? entity.getUser().getEmail() : null)
                .appointmentDateTime(entity.getAppointmentDateTime())
                .status(entity.getStatus())
                .eventType(entity.getEventType())
                .responses(entity.getResponses() != null ?
                        entity.getResponses().stream()
                                .map(QuestionnaireResponseDTO::fromEntity)
                                .collect(Collectors.toList()) :
                        new ArrayList<>())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}