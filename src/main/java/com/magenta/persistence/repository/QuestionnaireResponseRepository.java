package com.magenta.persistence.repository;

import com.magenta.persistence.entity.QuestionnaireResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface QuestionnaireResponseRepository extends JpaRepository<QuestionnaireResponseEntity, Long> {

}