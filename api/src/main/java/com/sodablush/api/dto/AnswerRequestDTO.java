package com.sodablush.api.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnswerRequestDTO {
    @NotNull(message = "questionId es obligatorio")
    private UUID questionId;

    //Una de las dos formas de respuesta (opcion o texto)
    private UUID selectedOptionId;
    private String answeredText;

    private Integer timeSpentSeconds;
}
