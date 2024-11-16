package com.magenta.persistence.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Table(name = "question_options")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionOptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String optionText;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionnaireQuestionEntity question;
}