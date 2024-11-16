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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireQuestionDTO {
    private Long id;
    private String questionText;
    private QuestionType questionType;
    private List<QuestionOptionDTO> options;
    private boolean required;
    private EventType eventType;

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