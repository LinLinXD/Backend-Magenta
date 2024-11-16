package com.magenta.dto;

import com.magenta.persistence.entity.QuestionOptionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionOptionDTO {
    private Long id;
    private String optionText;

    public static QuestionOptionDTO fromEntity(QuestionOptionEntity entity) {
        return QuestionOptionDTO.builder()
                .id(entity.getId())
                .optionText(entity.getOptionText())
                .build();
    }
}