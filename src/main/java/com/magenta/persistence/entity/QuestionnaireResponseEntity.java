package com.magenta.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad para una respuesta del cuestionario.
 */
@Entity
@Table(name = "questionnaire_responses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireResponseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador de la respuesta

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private AppointmentEntity appointment; // Cita asociada a la respuesta

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionnaireQuestionEntity question; // Pregunta asociada a la respuesta

    @Column(nullable = false)
    private String response; // Texto de la respuesta
}