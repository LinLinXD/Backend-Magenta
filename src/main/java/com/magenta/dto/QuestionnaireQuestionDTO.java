package com.magenta.dto;

import com.magenta.persistence.entity.EventType;
import com.magenta.persistence.entity.QuestionType;
import com.magenta.persistence.entity.QuestionnaireQuestionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Transfer Object (DTO) para una pregunta del cuestionario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireQuestionDTO {
    private Long id; // Identificador de la pregunta
    private String questionText; // Texto de la pregunta
    private QuestionType questionType; // Tipo de pregunta
    private List<QuestionOptionDTO> options; // Opciones de la pregunta
    private boolean required; // Indica si la pregunta es obligatoria
    private EventType eventType; // Tipo de evento asociado a la pregunta

    /**
     * Convierte una entidad de pregunta del cuestionario a un DTO.
     *
     * @param entity la entidad de pregunta del cuestionario
     * @return el DTO de la pregunta del cuestionario
     */
    public static QuestionnaireQuestionDTO fromEntity(QuestionnaireQuestionEntity entity) {
        return QuestionnaireQuestionDTO.builder()
                .id(entity.getId())
                .questionText(entity.getQuestionText())
                .questionType(entity.getQuestionType())
                .options(entity.getOptions().stream()
                        .map(QuestionOptionDTO::fromEntity)
                        .collect(Collectors.toList()))
                .required(entity.isRequired())
                .eventType(entity.getEventType())
                .build();
    }
}