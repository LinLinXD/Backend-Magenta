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


// CreateAppointmentDTO.java
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

    // Método para validar la fecha
    public void validateDateTime() {
        if (appointmentDateTime == null) {
            throw new IllegalArgumentException("La fecha y hora de la cita son obligatorias");
        }

        if (appointmentDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se puede agendar una cita en el pasado");
        }

        // Validar que sea en horario laboral (9am a 6pm)
        int hour = appointmentDateTime.getHour();
        if (hour < 9 || hour >= 18) {
            throw new IllegalArgumentException("Las citas solo pueden ser agendadas entre las 9:00 y las 18:00");
        }

        // Validar que no sea fin de semana
        DayOfWeek dayOfWeek = appointmentDateTime.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("No se pueden agendar citas en fin de semana");
        }
    }
}