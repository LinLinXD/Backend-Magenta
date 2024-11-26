package com.magenta.dto;

import com.magenta.persistence.entity.EventType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object (DTO) para crear una cita.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentDTO {
    @NotNull(message = "La fecha y hora de la cita son obligatorias")
    private LocalDateTime appointmentDateTime;

    @NotNull(message = "El tipo de evento es obligatorio")
    private EventType eventType;

    @Builder.Default
    private List<QuestionnaireResponseDTO> responses = new ArrayList<>();


}