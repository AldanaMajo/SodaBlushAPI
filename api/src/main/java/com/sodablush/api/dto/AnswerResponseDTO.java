package com.sodablush.api.dto;

import lombok.Data;

@Data
public class AnswerResponseDTO {
    private boolean correct;
    private Integer livesRemaining;
    private String explanation; //se revela despues de contestar
    private boolean outOfLives; //true si el intento se quedo "sin gas"
}
