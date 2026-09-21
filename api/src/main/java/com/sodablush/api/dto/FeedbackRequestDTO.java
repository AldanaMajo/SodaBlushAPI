package com.sodablush.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FeedbackRequestDTO {
    @NotBlank(message = "El tipo es obligatorio (ej. 'bug', 'idea', 'otro')")
    private String type;

    @NotBlank(message = "El mensaje no puede estar vacio")
    @Size(max = 2000, message = "El mensaje no puede superar 2000 caracteres")
    private String message;

    private String screenshotUrl;
}
