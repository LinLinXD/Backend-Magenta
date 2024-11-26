package com.magenta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) para un intervalo de tiempo.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotDTO {
    private LocalDateTime startTime; // Hora de inicio
    private LocalDateTime endTime; // Hora de fin
    private boolean available; // Indica si el intervalo de tiempo está disponible
    private String unavailableReason; // Razón por la cual el intervalo de tiempo no está disponible
}