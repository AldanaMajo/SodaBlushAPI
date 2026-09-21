package com.sodablush.api.dto;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class ExerciseSubmitResponseDTO {
    private boolean correct;
    private String feedbackMessage;

    //Avance de progreso cuando la respuesta es correcta
    private boolean drinkCompleted;
    private boolean canCompleted;
    private UUID nextDrinkId;
    private List<String> newAchievements;
}
