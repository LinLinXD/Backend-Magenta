package com.magenta.controller;

import com.magenta.dto.AppointmentDTO;
import com.magenta.dto.QuestionnaireQuestionDTO;
import com.magenta.dto.QuestionnaireResponseDTO;
import com.magenta.persistence.entity.AppointmentEntity;
import com.magenta.persistence.entity.AppointmentStatus;
import com.magenta.persistence.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.stream.Collectors;

/**
 * Controlador para la gestión de citas por parte del administrador.
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AppointmentRepository appointmentRepository;

    /**
     * Obtiene todas las citas.
     *
     * @return una lista de todas las citas
     */
    @GetMapping("/appointments")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<AppointmentDTO>> getAllAppointments() {
        try {

            List<AppointmentEntity> appointments = appointmentRepository.findAll();

            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(appointment -> AppointmentDTO.builder()
                            .id(appointment.getId())
                            .username(appointment.getUser().getUsername())
                            .name(appointment.getUser().getName())
                            .email(appointment.getUser().getEmail())
                            .appointmentDateTime(appointment.getAppointmentDateTime())
                            .status(appointment.getStatus())
                            .eventType(appointment.getEventType())
                            .responses(appointment.getResponses().stream()
                                    .map(response -> {
                                        return response != null ? new QuestionnaireResponseDTO(
                                                response.getQuestion().getId(),
                                                response.getResponse(),
                                                response.getQuestion() != null ?
                                                        QuestionnaireQuestionDTO.fromEntity(response.getQuestion()) : null
                                        ) : null;
                                    })
                                    .collect(Collectors.toList()))
                            .createdAt(appointment.getCreatedAt())
                            .build())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(appointmentDTOs);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Actualiza el estado de una cita.
     *
     * @param id el ID de la cita
     * @param status el nuevo estado de la cita
     * @return la cita actualizada
     */
    @PutMapping("/appointments/{id}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AppointmentDTO> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam AppointmentStatus status) {
        try {

            AppointmentEntity appointment = appointmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

            appointment.setStatus(status);
            appointment = appointmentRepository.save(appointment);

            AppointmentDTO updatedAppointment = AppointmentDTO.fromEntity(appointment);

            return ResponseEntity.ok(updatedAppointment);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtiene los detalles de una cita específica.
     *
     * @param id el ID de la cita
     * @return los detalles de la cita
     */
    @GetMapping("/appointments/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AppointmentDTO> getAppointment(@PathVariable Long id) {
        try {

            AppointmentEntity appointment = appointmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

            AppointmentDTO appointmentDTO = AppointmentDTO.fromEntity(appointment);

            return ResponseEntity.ok(appointmentDTO);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}