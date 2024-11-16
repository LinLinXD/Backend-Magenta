package com.magenta.persistence.repository;

import com.magenta.persistence.entity.EventType;
import com.magenta.persistence.entity.QuestionnaireResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionnaireResponseRepository extends JpaRepository<QuestionnaireResponseEntity, Long> {

    List<QuestionnaireResponseEntity> findByAppointmentId(Long appointmentId);

    @Query("SELECT r FROM QuestionnaireResponseEntity r " +
            "WHERE r.appointment.id = :appointmentId " +
            "AND r.question.id = :questionId")
    Optional<QuestionnaireResponseEntity> findByAppointmentAndQuestion(
            @Param("appointmentId") Long appointmentId,
            @Param("questionId") Long questionId
    );

    @Query("SELECT r FROM QuestionnaireResponseEntity r " +
            "WHERE r.appointment.user.username = :username " +
            "AND r.appointment.eventType = :eventType")
    List<QuestionnaireResponseEntity> findByUsernameAndEventType(
            @Param("username") String username,
            @Param("eventType") EventType eventType
    );
}