package com.magenta.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entidad para una pregunta del cuestionario.
 */
@Entity
@Table(name = "questionnaire_questions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireQuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador de la pregunta

    @Column(nullable = false)
    private String questionText; // Texto de la pregunta

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private QuestionType questionType; // Tipo de pregunta

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "question_id")
    private List<QuestionOptionEntity> options; // Opciones de la pregunta

    @Column(nullable = false)
    private boolean required; // Indica si la pregunta es obligatoria

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventType eventType; // Tipo de evento asociado a la pregunta
}