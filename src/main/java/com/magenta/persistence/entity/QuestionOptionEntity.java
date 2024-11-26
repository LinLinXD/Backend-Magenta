package com.magenta.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad para una opción de pregunta del cuestionario.
 */
@Entity
@Table(name = "question_options")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionOptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador de la opción

    @Column(nullable = false)
    private String optionText; // Texto de la opción

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionnaireQuestionEntity question; // Pregunta asociada a la opción
}