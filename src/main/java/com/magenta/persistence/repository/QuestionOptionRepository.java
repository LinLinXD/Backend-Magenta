package com.magenta.persistence.repository;

import com.magenta.persistence.entity.QuestionOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOptionEntity, Long> {
}