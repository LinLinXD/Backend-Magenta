package com.magenta.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object (DTO) para representar un día en el calendario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarDayDTO {
    private LocalDate date; // La fecha del día del calendario
    private List<TimeSlotDTO> timeSlots; // Lista de franjas horarias disponibles en el día
    private boolean isAvailable; // Indica si el día está disponible para citas

    @JsonProperty("isToday")
    private boolean isToday; // Indica si el día es el día actual

    @JsonProperty("isPast")
    private boolean isPast; // Indica si el día es una fecha pasada
}