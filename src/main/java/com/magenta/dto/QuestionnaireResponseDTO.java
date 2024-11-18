package com.magenta.dto;

import com.magenta.persistence.entity.QuestionnaireResponseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class QuestionnaireResponseDTO {
    private Long questionId;
    private String response;
    private QuestionnaireQuestionDTO question;  // Agregamos este campo

    // Constructor adicional para la conversión que estabas intentando usar
    public QuestionnaireResponseDTO(Long questionId, String response, QuestionnaireQuestionDTO question) {
        this.questionId = questionId;
        this.response = response;
        this.question = question;
    }

    public static QuestionnaireResponseDTO fromEntity(QuestionnaireResponseEntity entity) {
        if (entity == null) {
            return null;
        }

        return QuestionnaireResponseDTO.builder()
                .questionId(entity.getQuestion().getId())
                .response(entity.getResponse())
                .question(entity.getQuestion() != null ?
                        QuestionnaireQuestionDTO.fromEntity(entity.getQuestion()) : null)
                .build();
    }
}