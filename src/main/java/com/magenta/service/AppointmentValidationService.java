package com.magenta.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

/**
 * Servicio para validaciones.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentValidationService {
    private static final int MIN_HOURS_BEFORE_APPOINTMENT = 24;

    /**
     * Valida la fecha y hora de una cita.
     *
     * @param dateTime la fecha y hora de la cita
     */
    public void validateAppointmentDateTime(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();

        // Validar que la cita no sea en el pasado
        if (dateTime.isBefore(now)) {
            throw new RuntimeException("No se puede agendar una cita en el pasado");
        }

        // Validar que la cita sea al menos 24 horas después
        if (dateTime.isBefore(now.plusHours(MIN_HOURS_BEFORE_APPOINTMENT))) {
            throw new RuntimeException("Las citas deben agendarse con al menos 24 horas de anticipación");
        }

        // Validar que sea un día hábil
        if (dateTime.getDayOfWeek() == DayOfWeek.SATURDAY ||
                dateTime.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new RuntimeException("Las citas solo pueden ser agendadas en días hábiles");
        }
    }
}