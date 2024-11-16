package com.magenta.dto;

import com.magenta.persistence.entity.QuestionnaireResponseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para las respuestas del cuestionario
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireResponseDTO {
    private Long questionId;
    private String response;

    public static QuestionnaireResponseDTO fromEntity(QuestionnaireResponseEntity entity) {
        if (entity == null) {
            return null;
        }
        return QuestionnaireResponseDTO.builder()
                .questionId(entity.getQuestion().getId())
                .response(entity.getResponse())
                .build();
    }
}