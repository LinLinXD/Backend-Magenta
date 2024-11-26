package com.magenta.controller;

import java.time.LocalDate;
import com.magenta.service.AppointmentService;
import com.magenta.service.AppointmentNotificationService;
import com.magenta.dto.TimeSlotDTO;
import com.magenta.dto.AppointmentDTO;
import com.magenta.dto.CreateAppointmentDTO;
import com.magenta.persistence.entity.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

/**
 * Controlador para la gestión de citas.
 */
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class AppointmentController {
    private final AppointmentService appointmentService;
    /**
     * Crear una nueva cita.
     *
     * @param createDTO los datos para crear la cita
     * @param username el nombre de usuario del cliente
     * @return la cita creada
     */
    @PostMapping("/appointments")
    public ResponseEntity<AppointmentDTO> createAppointment(
            @RequestBody CreateAppointmentDTO createDTO,
            @RequestParam String username) {
        try {
            AppointmentDTO appointment = appointmentService.createAppointment(username, createDTO);
            return ResponseEntity.ok(appointment);
        } catch (Exception e) {
            log.error("Error al crear la cita: ", e);
            return ResponseEntity.badRequest().body(
                    AppointmentDTO.builder()
                            .error(e.getMessage())
                            .build()
            );
        }
    }

    /**
     * Obtener todas las citas de un usuario.
     *
     * @param username el nombre de usuario del cliente
     * @return una lista de citas del usuario
     */
    @GetMapping("/appointments/user/{username}")
    public ResponseEntity<List<AppointmentDTO>> getUserAppointments(
            @PathVariable String username) {
        try {
            List<AppointmentDTO> appointments = appointmentService.getUserAppointments(username);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtener una cita específica.
     *
     * @param appointmentId el ID de la cita
     * @return los detalles de la cita
     */
    @GetMapping("/appointments/{appointmentId}")
    public ResponseEntity<AppointmentDTO> getAppointment(
            @PathVariable Long appointmentId) {
        try {
            AppointmentDTO appointment = appointmentService.getAppointmentById(appointmentId);
            return ResponseEntity.ok(appointment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualizar el estado de una cita.
     *
     * @param appointmentId el ID de la cita
     * @param status el nuevo estado de la cita
     * @return la cita actualizada
     */
    @PutMapping("/appointments/{appointmentId}/status")
    public ResponseEntity<AppointmentDTO> updateAppointmentStatus(
            @PathVariable Long appointmentId,
            @RequestParam AppointmentStatus status) {
        try {
            AppointmentDTO appointment = appointmentService.updateAppointmentStatus(appointmentId, status);
            return ResponseEntity.ok(appointment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Cancelar una cita.
     *
     * @param appointmentId el ID de la cita
     * @return una respuesta vacía indicando éxito o error
     */
    @DeleteMapping("/appointments/{appointmentId}")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long appointmentId) {
        try {
            appointmentService.cancelAppointment(appointmentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtener disponibilidad de horarios para una fecha específica.
     *
     * @param date la fecha para la cual se desea consultar la disponibilidad
     * @return una lista de horarios disponibles
     */
    @GetMapping("/appointments/availability")
    public ResponseEntity<List<TimeSlotDTO>> getAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<TimeSlotDTO> availability = appointmentService.getAvailabilityForDate(date);
            return ResponseEntity.ok(availability);
        } catch (Exception e) {
            log.error("Error al obtener la disponibilidad: ", e);
            return ResponseEntity.badRequest().build();
        }
    }
}