package com.magenta.dto;

import com.magenta.persistence.entity.AppointmentEntity;
import com.magenta.persistence.entity.AppointmentNotificationEntity;
import com.magenta.persistence.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentNotificationDTO {
    private Long id;
    private Long appointmentId;
    private LocalDateTime notificationTime;
    private boolean read;
    private NotificationType type;
    private String message;

    public static AppointmentNotificationDTO fromEntity(AppointmentNotificationEntity entity) {
        return AppointmentNotificationDTO.builder()
                .id(entity.getId())
                .appointmentId(entity.getAppointment().getId())
                .notificationTime(entity.getNotificationTime())
                .read(entity.isRead())
                .type(entity.getType())
                .message(generateNotificationMessage(entity))
                .build();
    }

    private static String generateNotificationMessage(AppointmentNotificationEntity notification) {
        AppointmentEntity appointment = notification.getAppointment();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String appointmentDate = appointment.getAppointmentDateTime().format(formatter);

        return switch (notification.getType()) {
            case FIVE_DAYS_BEFORE ->
                    "Tienes una cita programada para el " + appointmentDate + " (5 días)";
            case TWO_DAYS_BEFORE ->
                    "Recuerda tu cita programada para el " + appointmentDate + " (2 días)";
            case ONE_DAY_BEFORE ->
                    "Tu cita está próxima - " + appointmentDate + " (mañana)";
            case ONE_HOUR_BEFORE ->
                    "Tu cita comenzará en 1 hora - " + appointmentDate;
            case APPOINTMENT_CREATED ->
                    "¡Cita creada exitosamente para el " + appointmentDate + "!";
            case APPOINTMENT_CANCELLED ->
                    "Tu cita programada para el " + appointmentDate + " ha sido cancelada";
        };
    }

    // Metodo de conveniencia para crear una notificación de cita creada
    public static AppointmentNotificationDTO createAppointmentCreatedNotification(AppointmentEntity appointment) {
        return AppointmentNotificationDTO.builder()
                .appointmentId(appointment.getId())
                .notificationTime(LocalDateTime.now())
                .read(false)
                .type(NotificationType.APPOINTMENT_CREATED)
                .message("¡Cita creada exitosamente para el " +
                        appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "!")
                .build();
    }

    // Metodo de conveniencia para crear una notificación de cita cancelada
    public static AppointmentNotificationDTO createAppointmentCancelledNotification(AppointmentEntity appointment) {
        return AppointmentNotificationDTO.builder()
                .appointmentId(appointment.getId())
                .notificationTime(LocalDateTime.now())
                .read(false)
                .type(NotificationType.APPOINTMENT_CANCELLED)
                .message("Tu cita programada para el " +
                        appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
                        " ha sido cancelada")
                .build();
    }
}