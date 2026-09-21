package com.sodablush.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data 
public class ExerciseSubmitRequestDTO {
    @NotBlank(message = "El codigo enviado no puede estar vacio")
    private String submittedCode; //codigo que escribio el estudiante
}
