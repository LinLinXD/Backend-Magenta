package com.magenta.persistence.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "questionnaire_responses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireResponseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private AppointmentEntity appointment;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionnaireQuestionEntity question;

    @Column(nullable = false)
    private String response;
}