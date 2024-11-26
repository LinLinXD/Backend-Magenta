package com.magenta.service;

import com.magenta.dto.*;
import com.magenta.persistence.entity.*;
import com.magenta.persistence.repository.AppointmentRepository;
import com.magenta.persistence.repository.QuestionnaireQuestionRepository;
import com.magenta.persistence.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar las citas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final QuestionnaireQuestionRepository questionRepository;
    private final AppointmentNotificationService notificationService;
    private final AppointmentValidationService validationService;

    private static final int BUFFER_HOURS = 2;
    private static final int MAX_WORKING_HOUR = 18;
    private static final int MIN_WORKING_HOUR = 9;

    @Transactional
    public AppointmentDTO createAppointment(String username, CreateAppointmentDTO createDTO) {

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        validationService.validateAppointmentDateTime(createDTO.getAppointmentDateTime());

        if (!isTimeSlotAvailable(createDTO.getAppointmentDateTime())) {
            throw new RuntimeException("El horario seleccionado no está disponible");
        }

        AppointmentEntity appointment = AppointmentEntity.builder()
                .user(user)
                .appointmentDateTime(createDTO.getAppointmentDateTime())
                .status(AppointmentStatus.PENDING)
                .eventType(createDTO.getEventType())
                .responses(new ArrayList<>())
                .build();

        if (createDTO.getResponses() != null) {
            processQuestionnaireResponses(appointment, createDTO.getResponses());
        }

        appointment = appointmentRepository.save(appointment);

        notificationService.createNotificationsForAppointment(appointment);

        return AppointmentDTO.fromEntity(appointment);
    }

    private void processQuestionnaireResponses(AppointmentEntity appointment,
                                               List<QuestionnaireResponseDTO> responses) {

        for (QuestionnaireResponseDTO responseDTO : responses) {

            QuestionnaireQuestionEntity question = questionRepository.findById(responseDTO.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Pregunta no encontrada"));

            QuestionnaireResponseEntity response = QuestionnaireResponseEntity.builder()
                    .appointment(appointment)
                    .question(question)
                    .response(responseDTO.getResponse())
                    .build();

            appointment.getResponses().add(response);
        }

    }

    public boolean isTimeSlotAvailable(LocalDateTime dateTime) {
        if (dateTime.getHour() < MIN_WORKING_HOUR || dateTime.getHour() >= MAX_WORKING_HOUR) {
            return false;
        }

        LocalDateTime bufferStart = dateTime.minusHours(BUFFER_HOURS);
        LocalDateTime bufferEnd = dateTime.plusHours(BUFFER_HOURS);

        return appointmentRepository
                .findOverlappingAppointments(bufferStart, bufferEnd)
                .isEmpty();
    }

    @Transactional
    public AppointmentDTO updateAppointmentStatus(Long appointmentId, AppointmentStatus newStatus) {
        AppointmentEntity appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        appointment.setStatus(newStatus);
        return AppointmentDTO.fromEntity(appointmentRepository.save(appointment));
    }

    public List<AppointmentDTO> getUserAppointments(String username) {
        return appointmentRepository.findByUserUsername(username).stream()
                .map(AppointmentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AppointmentDTO getAppointmentById(Long appointmentId) {

        AppointmentEntity appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        return AppointmentDTO.fromEntity(appointment);
    }

    @Transactional
    public void cancelAppointment(Long appointmentId) {
        log.debug("Cancelando cita con ID: {}", appointmentId);

        AppointmentEntity appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        if (appointment.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No se puede cancelar una cita pasada");
        }

        if (appointment.getAppointmentDateTime().minusHours(24).isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Las citas deben cancelarse con al menos 24 horas de anticipación");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        log.debug("Cita cancelada exitosamente");
    }




    @Transactional(readOnly = true)
    public List<TimeSlotDTO> getAvailabilityForDate(LocalDate date) {
        List<TimeSlotDTO> timeSlots = new ArrayList<>();
        LocalTime startTime = LocalTime.of(9, 0); // Hora inicio día laboral
        LocalTime endTime = LocalTime.of(18, 0);  // Hora fin día laboral

        if (date.isBefore(LocalDate.now()) ||
                date.getDayOfWeek() == DayOfWeek.SATURDAY ||
                date.getDayOfWeek() == DayOfWeek.SUNDAY) {

            timeSlots.add(TimeSlotDTO.builder()
                    .startTime(date.atTime(startTime))
                    .endTime(date.atTime(endTime))
                    .available(false)
                    .unavailableReason(date.isBefore(LocalDate.now()) ?
                            "Fecha pasada" : "Fin de semana")
                    .build());
            return timeSlots;
        }

        while (startTime.isBefore(endTime)) {
            LocalDateTime slotDateTime = date.atTime(startTime);
            boolean isAvailable = checkAvailability(slotDateTime);

            timeSlots.add(TimeSlotDTO.builder()
                    .startTime(slotDateTime)
                    .endTime(slotDateTime.plusHours(1))
                    .available(isAvailable)
                    .unavailableReason(!isAvailable ? "Horario no disponible" : null)
                    .build());

            startTime = startTime.plusHours(1);
        }

        return timeSlots;
    }




    private boolean checkAvailability(LocalDateTime dateTime) {
        return true;
    }
}