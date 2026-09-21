package com.sodablush.api.dto;

import lombok.Data;

@Data 
public class ExerciseSubmitResponseDTO {
    private boolean isCorrect;
    private String feedbackMessage;
}
