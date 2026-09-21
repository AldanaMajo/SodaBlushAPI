package com.sodablush.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateStatusRequestDTO {
    @NotBlank(message = "El estado es obligatorio ('active' o 'suspended')")
    private String status;
}
