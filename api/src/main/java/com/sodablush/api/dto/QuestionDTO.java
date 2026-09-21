package com.sodablush.api.dto;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import lombok.Data;

/** Pregunta del mini-test SIN la respuesta correcta ni la explicacion. */
@Data
public class QuestionDTO {
    private UUID id;
    private String questionType;
    private String prompt;
    private String codeSnippet;
    private Integer orderIndex;
    private BigDecimal points;
    private Map<String, Object> options;
}
