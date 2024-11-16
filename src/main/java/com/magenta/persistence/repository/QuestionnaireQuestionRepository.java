package com.magenta.persistence.repository;

import com.magenta.persistence.entity.EventType;
import com.magenta.persistence.entity.QuestionnaireQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionnaireQuestionRepository extends JpaRepository<QuestionnaireQuestionEntity, Long> {

    List<QuestionnaireQuestionEntity> findByEventType(EventType eventType);

    @Query("SELECT q FROM QuestionnaireQuestionEntity q " +
            "WHERE q.eventType = :eventType " +
            "AND q.required = true")
    List<QuestionnaireQuestionEntity> findRequiredQuestionsByEventType(
            @Param("eventType") EventType eventType
    );

    @Query("SELECT DISTINCT q.eventType FROM QuestionnaireQuestionEntity q")
    List<EventType> findAllEventTypes();

    @Query("SELECT q FROM QuestionnaireQuestionEntity q " +
            "LEFT JOIN FETCH q.options " +
            "WHERE q.eventType = :eventType")
    List<QuestionnaireQuestionEntity> findByEventTypeWithOptions(
            @Param("eventType") EventType eventType
    );
}