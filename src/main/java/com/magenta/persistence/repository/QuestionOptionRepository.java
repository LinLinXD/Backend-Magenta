package com.magenta.persistence.repository;

import com.magenta.persistence.entity.QuestionOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOptionEntity, Long> {

    List<QuestionOptionEntity> findByQuestionId(Long questionId);

    @Query("SELECT o FROM QuestionOptionEntity o " +
            "WHERE o.question.id = :questionId " +
            "ORDER BY o.id ASC")
    List<QuestionOptionEntity> findOrderedByQuestionId(
            @Param("questionId") Long questionId
    );
}