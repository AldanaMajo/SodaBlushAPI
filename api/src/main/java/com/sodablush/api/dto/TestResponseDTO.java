package com.sodablush.api.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class TestResponseDTO {
    private UUID id;
    private UUID drinkId;
    private String testType;
    private String instructions;
    private BigDecimal passingScore;
    private Integer maxAttempts;
    private Integer livesAllowed;
    private List<QuestionDTO> questions;
}
