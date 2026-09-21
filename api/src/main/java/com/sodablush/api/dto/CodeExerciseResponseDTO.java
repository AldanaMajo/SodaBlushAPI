package com.sodablush.api.dto;

import lombok.Data;
import java.util.Map;
import java.util.UUID;

@Data
public class CodeExerciseResponseDTO {
    
    private UUID id;
    private String instruction;
    private String starterCode;
    private String outputHtml;
    private String outputCss;
    private Map<String, Object> hints; 
    //omití datos que puedan revelar la respuesta 
}