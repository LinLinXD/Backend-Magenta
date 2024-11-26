package com.magenta.dto;

import com.magenta.persistence.entity.QuestionnaireResponseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) para una respuesta del cuestionario.
 */
@Data
@Builder
@NoArgsConstructor
public class QuestionnaireResponseDTO {
    private Long questionId; // Identificador de la pregunta
    private String response; // Respuesta a la pregunta
    private QuestionnaireQuestionDTO question; // Pregunta del cuestionario

    /**
     * Constructor adicional para la conversión.
     *
     * @param questionId Identificador de la pregunta
     * @param response Respuesta a la pregunta
     * @param question Pregunta del cuestionario
     */
    public QuestionnaireResponseDTO(Long questionId, String response, QuestionnaireQuestionDTO question) {
        this.questionId = questionId;
        this.response = response;
        this.question = question;
    }

    /**
     * Convierte una entidad de respuesta del cuestionario a un DTO.
     *
     * @param entity la entidad de respuesta del cuestionario
     * @return el DTO de la respuesta del cuestionario
     */
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