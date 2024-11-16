package com.magenta.persistence.repository;

import com.magenta.persistence.entity.AppointmentEntity;
import com.magenta.persistence.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    @Query("SELECT a FROM AppointmentEntity a WHERE a.appointmentDateTime BETWEEN :startTime AND :endTime")
    List<AppointmentEntity> findOverlappingAppointments(
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    @Query("SELECT a FROM AppointmentEntity a " +
            "WHERE MONTH(a.appointmentDateTime) = :month " +
            "AND YEAR(a.appointmentDateTime) = :year")
    List<AppointmentEntity> findAllByMonthAndYear(int month, int year);

    List<AppointmentEntity> findByUserUsername(String username);

    @Query("SELECT a FROM AppointmentEntity a " +
            "WHERE a.user.username = :username " +
            "AND a.appointmentDateTime > :now " +
            "ORDER BY a.appointmentDateTime ASC")
    List<AppointmentEntity> findUpcomingAppointments(
            @Param("username") String username,
            @Param("now") LocalDateTime now
    );

    @Query("SELECT COUNT(a) > 0 FROM AppointmentEntity a " +
            "WHERE a.appointmentDateTime BETWEEN :startTime AND :endTime " +
            "AND a.status <> 'CANCELLED'")
    boolean existsOverlappingAppointment(
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    @Query("SELECT a FROM AppointmentEntity a " +
            "WHERE a.status = :status " +
            "AND a.appointmentDateTime BETWEEN :startDate AND :endDate")
    List<AppointmentEntity> findByStatusAndDateRange(
            @Param("status") AppointmentStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COUNT(a) FROM AppointmentEntity a " +
            "WHERE a.user.username = :username " +
            "AND a.status IN (:activeStatuses)")
    long countActiveAppointmentsByUsername(
            @Param("username") String username,
            @Param("activeStatuses") List<AppointmentStatus> activeStatuses
    );

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM AppointmentEntity a " +
            "WHERE a.user.username = :username " +
            "AND a.appointmentDateTime BETWEEN :startTime AND :endTime " +
            "AND a.status <> 'CANCELLED'")
    boolean existsOverlappingAppointmentForUser(
            @Param("username") String username,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT a FROM AppointmentEntity a " +
            "WHERE a.appointmentDateTime BETWEEN :startTime AND :endTime")
    List<AppointmentEntity> findByAppointmentDateTimeBetween(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}