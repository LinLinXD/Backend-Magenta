package com.magenta.dto;

import com.magenta.persistence.entity.QuestionOptionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) para una opción de pregunta.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionOptionDTO {
    private Long id; // Identificador de la opción
    private String optionText; // Texto de la opción

    /**
     * Convierte una entidad de opción de pregunta a un DTO.
     *
     * @param entity la entidad de opción de pregunta
     * @return el DTO de la opción de pregunta
     */
    public static QuestionOptionDTO fromEntity(QuestionOptionEntity entity) {
        return QuestionOptionDTO.builder()
                .id(entity.getId())
                .optionText(entity.getOptionText())
                .build();
    }
}