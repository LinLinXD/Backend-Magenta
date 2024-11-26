package com.magenta.persistence.repository;

import com.magenta.persistence.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para gestionar las citas.
 */
@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    @Query("SELECT a FROM AppointmentEntity a WHERE a.appointmentDateTime BETWEEN :startTime AND :endTime")
    List<AppointmentEntity> findOverlappingAppointments(
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    List<AppointmentEntity> findByUserUsername(String username);

}