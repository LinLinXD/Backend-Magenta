package com.magenta.controller;

import com.magenta.dto.AppointmentDTO;
import com.magenta.dto.QuestionnaireQuestionDTO;
import com.magenta.dto.QuestionnaireResponseDTO;
import com.magenta.persistence.entity.AppointmentEntity;
import com.magenta.persistence.entity.AppointmentStatus;
import com.magenta.persistence.entity.EventType;
import com.magenta.persistence.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final AppointmentRepository appointmentRepository;

    @GetMapping("/appointments")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<AppointmentDTO>> getAllAppointments() {
        try {
            log.debug("Obteniendo todas las citas para el admin");

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

            log.debug("Se encontraron {} citas", appointmentDTOs.size());
            return ResponseEntity.ok(appointmentDTOs);

        } catch (Exception e) {
            log.error("Error al obtener las citas", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/appointments/{id}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AppointmentDTO> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam AppointmentStatus status) {
        try {
            log.debug("Actualizando estado de cita {} a {}", id, status);

            AppointmentEntity appointment = appointmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

            appointment.setStatus(status);
            appointment = appointmentRepository.save(appointment);

            AppointmentDTO updatedAppointment = AppointmentDTO.fromEntity(appointment);

            log.debug("Estado de cita actualizado exitosamente");
            return ResponseEntity.ok(updatedAppointment);

        } catch (Exception e) {
            log.error("Error al actualizar estado de cita", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/appointments/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AppointmentDTO> getAppointment(@PathVariable Long id) {
        try {
            log.debug("Obteniendo detalles de cita {}", id);

            AppointmentEntity appointment = appointmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

            AppointmentDTO appointmentDTO = AppointmentDTO.fromEntity(appointment);

            log.debug("Detalles de cita obtenidos exitosamente");
            return ResponseEntity.ok(appointmentDTO);

        } catch (Exception e) {
            log.error("Error al obtener detalles de cita", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/appointments/stats")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getAppointmentStats() {
        try {
            log.debug("Obteniendo estadísticas de citas");

            Map<String, Object> stats = new HashMap<>();

            // Total de citas
            long totalAppointments = appointmentRepository.count();

            // Citas por estado
            Map<AppointmentStatus, Long> appointmentsByStatus = appointmentRepository.findAll().stream()
                    .collect(Collectors.groupingBy(
                            AppointmentEntity::getStatus,
                            Collectors.counting()
                    ));

            // Citas por tipo de evento
            Map<EventType, Long> appointmentsByType = appointmentRepository.findAll().stream()
                    .collect(Collectors.groupingBy(
                            AppointmentEntity::getEventType,
                            Collectors.counting()
                    ));

            stats.put("totalAppointments", totalAppointments);
            stats.put("appointmentsByStatus", appointmentsByStatus);
            stats.put("appointmentsByType", appointmentsByType);

            log.debug("Estadísticas obtenidas exitosamente");
            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            log.error("Error al obtener estadísticas", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}