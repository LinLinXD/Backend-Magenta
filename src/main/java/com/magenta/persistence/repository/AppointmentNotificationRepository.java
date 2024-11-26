package com.magenta.persistence.repository;

import com.magenta.persistence.entity.AppointmentNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para gestionar las notificaciones de citas.
 */
@Repository
public interface AppointmentNotificationRepository extends JpaRepository<AppointmentNotificationEntity, Long> {

    @Query("SELECT n FROM AppointmentNotificationEntity n " +
            "WHERE n.appointment.user.username = :username " +
            "AND n.read = false " +
            "AND n.sent = true " +
            "ORDER BY n.notificationTime DESC")
    List<AppointmentNotificationEntity> findUnreadByUsername(
            @Param("username") String username
    );

    // Para contar notificaciones no leídas
    @Query("SELECT COUNT(n) FROM AppointmentNotificationEntity n " +
            "WHERE n.appointment.user.username = :username " +
            "AND n.read = false")
    long countUnreadNotifications(
            @Param("username") String username
    );

    // Para marcar todas las notificaciones como leídas
    @Modifying
    @Query("UPDATE AppointmentNotificationEntity n " +
            "SET n.read = true " +
            "WHERE n.appointment.user.username = :username")
    void markAllAsRead(
            @Param("username") String username
    );

    // Para encontrar notificaciones que deben enviarse en un rango de tiempo
    @Query("SELECT n FROM AppointmentNotificationEntity n " +
            "WHERE n.sent = false " +
            "AND n.notificationTime BETWEEN :startTime AND :endTime " +
            "AND n.appointment.status != 'CANCELLED' " +
            "ORDER BY n.notificationTime ASC")
    List<AppointmentNotificationEntity> findNotificationsToSend(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}