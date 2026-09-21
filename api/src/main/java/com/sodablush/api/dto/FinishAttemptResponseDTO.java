package com.sodablush.api.dto;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class FinishAttemptResponseDTO {
    private UUID attemptId;
    private Integer score;       //porcentaje 0-100
    private boolean passed;
    private Integer livesRemaining;
    private boolean drinkCompleted; //si paso, el trago se marca completado
    private UUID nextDrinkId;
    private boolean canCompleted;
    private List<String> newAchievements;
}
