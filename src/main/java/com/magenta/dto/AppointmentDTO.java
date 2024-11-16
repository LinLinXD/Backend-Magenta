package com.magenta.dto;

import com.magenta.persistence.entity.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    private Long id;
    private String username;
    private LocalDateTime appointmentDateTime;
    private AppointmentStatus status;
    private EventType eventType;
    private List<QuestionnaireResponseDTO> responses;
    private LocalDateTime createdAt;
    private String error;

    // Constructor personalizado para conversión desde Entity
    public static AppointmentDTO fromEntity(AppointmentEntity entity) {
        if (entity == null) {
            return null;
        }

        return AppointmentDTO.builder()
                .id(entity.getId())
                .username(entity.getUser() != null ? entity.getUser().getUsername() : null)
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