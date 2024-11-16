package com.magenta.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "questionnaire_questions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireQuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String questionText;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "question_id")
    private List<QuestionOptionEntity> options;

    @Column(nullable = false)
    private boolean required;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventType eventType;
}