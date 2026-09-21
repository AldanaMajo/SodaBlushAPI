package com.sodablush.api.dto;

import java.util.Map;

import jakarta.validation.constraints.Size;
import lombok.Data;

/** Todos los campos son opcionales: solo se actualiza lo que venga en el body. */
@Data
public class UpdateProfileRequestDTO {
    @Size(min = 1, max = 60, message = "El nombre debe tener entre 1 y 60 caracteres")
    private String displayName;
    private String locale;
    private String theme;
    private Map<String, Object> soundSettings;
    private Map<String, Object> notificationSettings;
}
