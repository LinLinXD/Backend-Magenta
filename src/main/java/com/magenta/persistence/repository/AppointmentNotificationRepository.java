package com.magenta.persistence.repository;

import com.magenta.persistence.entity.AppointmentNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentNotificationRepository extends JpaRepository<AppointmentNotificationEntity, Long> {

    @Query("SELECT n FROM AppointmentNotificationEntity n " +
            "WHERE n.notificationTime <= :now " +
            "AND n.sent = false")
    List<AppointmentNotificationEntity> findPendingNotifications(
            @Param("now") LocalDateTime now
    );

    @Query("SELECT n FROM AppointmentNotificationEntity n " +
            "WHERE n.appointment.user.username = :username " +
            "AND n.read = false " +
            "ORDER BY n.notificationTime DESC")
    List<AppointmentNotificationEntity> findUnreadByUsername(
            @Param("username") String username
    );

    @Query("SELECT n FROM AppointmentNotificationEntity n " +
            "WHERE n.appointment.id = :appointmentId " +
            "ORDER BY n.notificationTime ASC")
    List<AppointmentNotificationEntity> findByAppointmentIdOrdered(
            @Param("appointmentId") Long appointmentId
    );

    @Query("SELECT COUNT(n) FROM AppointmentNotificationEntity n " +
            "WHERE n.appointment.user.username = :username " +
            "AND n.read = false")
    long countUnreadNotifications(
            @Param("username") String username
    );

    @Modifying
    @Query("UPDATE AppointmentNotificationEntity n " +
            "SET n.read = true " +
            "WHERE n.appointment.user.username = :username")
    void markAllAsRead(
            @Param("username") String username
    );

    @Modifying
    @Query("DELETE FROM AppointmentNotificationEntity n " +
            "WHERE n.appointment.id = :appointmentId " +
            "AND n.sent = false " +
            "AND n.type != 'APPOINTMENT_CANCELLED'")
    void deleteUnsentNotifications(@Param("appointmentId") Long appointmentId);

    @Query("SELECT n FROM AppointmentNotificationEntity n " +
            "WHERE n.sent = false " +
            "AND n.notificationTime <= :now " +
            "AND n.appointment.status != 'CANCELLED' " +
            "ORDER BY n.notificationTime ASC")
    List<AppointmentNotificationEntity> findNotificationsToSend(@Param("now") LocalDateTime now);

}