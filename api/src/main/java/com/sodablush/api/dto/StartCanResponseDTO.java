package com.sodablush.api.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class StartCanResponseDTO {
    private UUID canId;
    private String status;
    private UUID currentDrinkId; //primer trago pendiente
    private boolean alreadyStarted; //true si la lata ya estaba abierta
}
