package com.sodablush.api.dto;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class CompleteDrinkResponseDTO {
    private UUID drinkId;
    private boolean canCompleted;    //true si era el ultimo trago de la lata
    private UUID nextDrinkId;        //siguiente trago (null si la lata se termino)
    private List<String> newAchievements; //codigos de logros otorgados en esta accion
}
