package com.magenta.service;

import com.magenta.dto.AppointmentNotificationDTO;
import com.magenta.persistence.entity.AppointmentEntity;
import com.magenta.persistence.entity.AppointmentNotificationEntity;
import com.magenta.persistence.entity.AppointmentStatus;
import com.magenta.persistence.entity.NotificationType;
import com.magenta.persistence.repository.AppointmentNotificationRepository;
import com.magenta.persistence.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentNotificationService {
    private final AppointmentNotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createNotificationsForAppointment(AppointmentEntity appointment) {
        // No crear notificaciones si la cita está cancelada
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            return;
        }

        List<AppointmentNotificationEntity> notifications = new ArrayList<>();
        LocalDateTime appointmentTime = appointment.getAppointmentDateTime();
        LocalDateTime now = LocalDateTime.now();

        // Crear solo la notificación de creación de cita
        notifications.add(createNotification(
                appointment,
                now,
                NotificationType.APPOINTMENT_CREATED,
                true  // Esta notificación se envía inmediatamente
        ));

        // Programar las notificaciones futuras
        scheduleUpcomingNotifications(appointment, appointmentTime, now, notifications);

        // Guardar todas las notificaciones
        notificationRepository.saveAll(notifications);

        log.debug("Creadas {} notificaciones para la cita {}",
                notifications.size(), appointment.getId());
    }

    private void scheduleUpcomingNotifications(
            AppointmentEntity appointment,
            LocalDateTime appointmentTime,
            LocalDateTime now,
            List<AppointmentNotificationEntity> notifications
    ) {
        // 5 días antes
        if (appointmentTime.isAfter(now.plusDays(5))) {
            notifications.add(createNotification(
                    appointment,
                    appointmentTime.minusDays(5),
                    NotificationType.FIVE_DAYS_BEFORE,
                    false  // No enviada aún
            ));
        }

        // 2 días antes
        if (appointmentTime.isAfter(now.plusDays(2))) {
            notifications.add(createNotification(
                    appointment,
                    appointmentTime.minusDays(2),
                    NotificationType.TWO_DAYS_BEFORE,
                    false
            ));
        }

        // 1 día antes
        if (appointmentTime.isAfter(now.plusDays(1))) {
            notifications.add(createNotification(
                    appointment,
                    appointmentTime.minusDays(1),
                    NotificationType.ONE_DAY_BEFORE,
                    false
            ));
        }

        // 1 hora antes
        if (appointmentTime.isAfter(now.plusHours(1))) {
            notifications.add(createNotification(
                    appointment,
                    appointmentTime.minusHours(1),
                    NotificationType.ONE_HOUR_BEFORE,
                    false
            ));
        }
    }

    private AppointmentNotificationEntity createNotification(
            AppointmentEntity appointment,
            LocalDateTime notificationTime,
            NotificationType type,
            boolean sent
    ) {
        return AppointmentNotificationEntity.builder()
                .appointment(appointment)
                .notificationTime(notificationTime)
                .type(type)
                .sent(sent)
                .read(false)
                .build();
    }


    private boolean shouldSendNotification(
            AppointmentNotificationEntity notification,
            LocalDateTime now
    ) {
        LocalDateTime notificationTime = notification.getNotificationTime();
        return !notification.isSent() &&
                notificationTime.isAfter(now.minusMinutes(1)) &&
                notificationTime.isBefore(now.plusMinutes(1));
    }


    public List<AppointmentNotificationDTO> getUnreadNotifications(String username) {
        return notificationRepository.findUnreadByUsername(username)
                .stream()
                .map(AppointmentNotificationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public long getUnreadNotificationCount(String username) {
        return notificationRepository.countUnreadNotifications(username);
    }

    public void markNotificationAsRead(Long notificationId) {
        AppointmentNotificationEntity notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllNotificationsAsRead(String username) {
        notificationRepository.markAllAsRead(username);
    }

    // Mantén los otros métodos existentes del servicio...

    @Scheduled(fixedRate = 60000) // Cada minuto
    @Transactional
    public void processNotifications() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMinuteAgo = now.minusMinutes(1);
        LocalDateTime oneMinuteAhead = now.plusMinutes(1);

        List<AppointmentNotificationEntity> pendingNotifications =
                notificationRepository.findNotificationsToSend(oneMinuteAgo, oneMinuteAhead);

        for (AppointmentNotificationEntity notification : pendingNotifications) {
            try {
                if (notification.getAppointment().getStatus() != AppointmentStatus.CANCELLED
                        && !notification.isSent()
                        && isTimeToSend(notification, now)) {

                    notification.setSent(true);
                    notificationRepository.save(notification);
                    log.debug("Notificación {} enviada: {}",
                            notification.getId(),
                            notification.getType());
                }
            } catch (Exception e) {
                log.error("Error procesando notificación {}: {}",
                        notification.getId(),
                        e.getMessage());
            }
        }
    }

    private boolean isTimeToSend(AppointmentNotificationEntity notification, LocalDateTime now) {
        return notification.getNotificationTime().isAfter(now.minusMinutes(1))
                && notification.getNotificationTime().isBefore(now.plusMinutes(1));
    }




}