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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;



    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class AppointmentService {
        private final AppointmentRepository appointmentRepository;
        private final UserRepository userRepository;
        private final QuestionnaireQuestionRepository questionRepository;
        private final AppointmentNotificationService notificationService;
        private final AppointmentValidationService validationService;

        private static final int MIN_HOURS_BEFORE_APPOINTMENT = 24;
        private static final int BUFFER_HOURS = 2;
        private static final int MAX_WORKING_HOUR = 18;
        private static final int MIN_WORKING_HOUR = 9;
        private static final int MAX_ACTIVE_APPOINTMENTS = 2;

        @Transactional
        public AppointmentDTO createAppointment(String username, CreateAppointmentDTO createDTO) {
            log.debug("Creando cita para usuario: {} en fecha: {}", username, createDTO.getAppointmentDateTime());

            // Validar usuario
            UserEntity user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Validar fecha y hora
            validationService.validateAppointmentDateTime(createDTO.getAppointmentDateTime());

            // Verificar disponibilidad
            if (!isTimeSlotAvailable(createDTO.getAppointmentDateTime())) {
                throw new RuntimeException("El horario seleccionado no está disponible");
            }

            // Crear la cita
            AppointmentEntity appointment = AppointmentEntity.builder()
                    .user(user)
                    .appointmentDateTime(createDTO.getAppointmentDateTime())
                    .status(AppointmentStatus.PENDING)
                    .eventType(createDTO.getEventType())
                    .responses(new ArrayList<>())
                    .build();

            // Procesar respuestas del cuestionario
            if (createDTO.getResponses() != null) {
                processQuestionnaireResponses(appointment, createDTO.getResponses());
            }

            // Guardar la cita
            appointment = appointmentRepository.save(appointment);

            // Crear notificaciones programadas
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
            // Validar horario de trabajo
            if (dateTime.getHour() < MIN_WORKING_HOUR || dateTime.getHour() >= MAX_WORKING_HOUR) {
                return false;
            }

            // Verificar si hay citas cercanas
            LocalDateTime bufferStart = dateTime.minusHours(BUFFER_HOURS);
            LocalDateTime bufferEnd = dateTime.plusHours(BUFFER_HOURS);

            return appointmentRepository
                    .findOverlappingAppointments(bufferStart, bufferEnd)
                    .isEmpty();
        }

        public List<CalendarDayDTO> getAvailabilityForMonth(YearMonth yearMonth) {
            List<CalendarDayDTO> calendarDays = new ArrayList<>();
            LocalDate now = LocalDate.now();

            // Obtener todas las citas para el mes
            List<AppointmentEntity> monthAppointments = appointmentRepository
                    .findAllByMonthAndYear(yearMonth.getMonthValue(), yearMonth.getYear());

            // Procesar cada día del mes
            for (int i = 1; i <= yearMonth.lengthOfMonth(); i++) {
                LocalDate date = yearMonth.atDay(i);

                // No mostrar días pasados
                if (date.isBefore(now)) {
                    calendarDays.add(createPastCalendarDay(date));
                    continue;
                }

                List<TimeSlotDTO> timeSlots = generateTimeSlotsForDay(date, monthAppointments);
                boolean isAvailable = timeSlots.stream().anyMatch(TimeSlotDTO::isAvailable);

                calendarDays.add(CalendarDayDTO.builder()
                        .date(date)
                        .timeSlots(timeSlots)
                        .isAvailable(isAvailable)
                        .isToday(date.equals(now))
                        .isPast(date.isBefore(now))
                        .build());
            }

            return calendarDays;
        }

        private List<TimeSlotDTO> generateTimeSlotsForDay(LocalDate date,
                                                          List<AppointmentEntity> appointments) {
            List<TimeSlotDTO> timeSlots = new ArrayList<>();
            LocalTime currentTime = LocalTime.of(MIN_WORKING_HOUR, 0);
            LocalTime endTime = LocalTime.of(MAX_WORKING_HOUR, 0);

            while (currentTime.isBefore(endTime)) {
                LocalDateTime slotDateTime = LocalDateTime.of(date, currentTime);
                boolean isAvailable = isTimeSlotAvailable(slotDateTime);

                timeSlots.add(TimeSlotDTO.builder()
                        .startTime(slotDateTime)
                        .endTime(slotDateTime.plusHours(1))
                        .available(isAvailable)
                        .unavailableReason(isAvailable ? null : "Horario no disponible")
                        .build());

                currentTime = currentTime.plusHours(1);
            }

            return timeSlots;
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

        private CalendarDayDTO createPastCalendarDay(LocalDate date) {
            return CalendarDayDTO.builder()
                    .date(date)
                    .timeSlots(Collections.emptyList()) // Días pasados no tienen slots disponibles
                    .isAvailable(false)
                    .isToday(false)
                    .isPast(true)
                    .build();
        }

        @Transactional(readOnly = true)
        public AppointmentDTO getAppointmentById(Long appointmentId) {
            log.debug("Buscando cita con ID: {}", appointmentId);

            AppointmentEntity appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

            return AppointmentDTO.fromEntity(appointment);
        }

        @Transactional
        public void cancelAppointment(Long appointmentId) {
            log.debug("Cancelando cita con ID: {}", appointmentId);

            AppointmentEntity appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

            // Verificar si la cita puede ser cancelada (no está en el pasado)
            if (appointment.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("No se puede cancelar una cita pasada");
            }

            // Verificar si la cita está a menos de 24 horas
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

            // Si es fin de semana o fecha pasada, retornar slot no disponible
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

            // Generar slots para cada hora del día
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
            // Tu lógica existente para verificar disponibilidad
            return true; // Simplificado para el ejemplo
        }

        private String getUnavailabilityReason(LocalDateTime dateTime, List<AppointmentEntity> dayAppointments) {
            // Si es una hora pasada
            if (dateTime.isBefore(LocalDateTime.now())) {
                return "Horario pasado";
            }

            // Si hay una cita en el horario o dentro del buffer
            LocalDateTime bufferStart = dateTime.minusHours(BUFFER_HOURS);
            LocalDateTime bufferEnd = dateTime.plusHours(BUFFER_HOURS);

            boolean hasOverlap = dayAppointments.stream()
                    .anyMatch(appointment ->
                            !appointment.getStatus().equals(AppointmentStatus.CANCELLED) &&
                                    appointment.getAppointmentDateTime().isAfter(bufferStart) &&
                                    appointment.getAppointmentDateTime().isBefore(bufferEnd));

            if (hasOverlap) {
                return "Horario no disponible";
            }

            // Si está fuera del horario de atención
            if (dateTime.getHour() < MIN_WORKING_HOUR || dateTime.getHour() >= MAX_WORKING_HOUR) {
                return "Fuera del horario de atención";
            }

            return null;
        }

        @Transactional(readOnly = true)
        public long getActiveAppointmentsCount(String username) {
            List<AppointmentStatus> activeStatuses = Arrays.asList(
                    AppointmentStatus.PENDING,
                    AppointmentStatus.CONFIRMED
            );
            return appointmentRepository.countActiveAppointmentsByUsername(username, activeStatuses);
        }

        @Transactional(readOnly = true)
        public boolean hasOverlappingAppointments(String username, LocalDateTime dateTime) {
            LocalDateTime bufferStart = dateTime.minusHours(BUFFER_HOURS);
            LocalDateTime bufferEnd = dateTime.plusHours(BUFFER_HOURS);

            return appointmentRepository.existsOverlappingAppointmentForUser(
                    username,
                    bufferStart,
                    bufferEnd
            );
        }

        private void validateAppointmentCreation(String username, CreateAppointmentDTO createDTO) {
            // Validar límite de citas activas
            long activeAppointments = getActiveAppointmentsCount(username);
            if (activeAppointments >= MAX_ACTIVE_APPOINTMENTS) {
                throw new RuntimeException("Has alcanzado el límite de citas activas");
            }

            // Validar que no haya citas superpuestas para el mismo usuario
            if (hasOverlappingAppointments(username, createDTO.getAppointmentDateTime())) {
                throw new RuntimeException("Ya tienes una cita programada para este horario");
            }
        }
    }