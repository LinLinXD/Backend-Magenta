package com.magenta.controller;

import com.magenta.dto.QuestionnaireQuestionDTO;
import com.magenta.persistence.entity.EventType;
import com.magenta.persistence.repository.QuestionnaireQuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para la gestión de cuestionarios.
 */
@RestController
@RequestMapping("/questionnaire")
@RequiredArgsConstructor
@Slf4j
public class QuestionnaireController {
    private final QuestionnaireQuestionRepository questionRepository;

    /**
     * Obtiene las preguntas por tipo de evento.
     *
     * @param eventType el tipo de evento
     * @return una lista de preguntas del cuestionario
     */
    @GetMapping("/questions/{eventType}")
    public ResponseEntity<List<QuestionnaireQuestionDTO>> getQuestionsByEventType(
            @PathVariable EventType eventType) {
        try {
            List<QuestionnaireQuestionDTO> questions = questionRepository
                    .findByEventTypeWithOptions(eventType)
                    .stream()
                    .map(QuestionnaireQuestionDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            log.error("Error al obtener las preguntas: ", e);
            return ResponseEntity.badRequest().build();
        }
    }
}