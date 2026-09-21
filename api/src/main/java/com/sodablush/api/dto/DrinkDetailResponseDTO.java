package com.sodablush.api.dto;

import lombok.Data;
import java.util.Map;
import java.util.UUID;

@Data
public class DrinkDetailResponseDTO {
    // Datos de la tabla DRINKS
    private UUID id;
    private String title;
    private String type;
    private Integer stepOrder;

    //Campos de DEFINICION 
    private String definitionText;
    private String shortText;
    private String animatedGifUrl;
    private String exampleCode;
    private Map<String, Object> tips;

    //Campos de SINTAXIS
    private String syntaxTemplate;
    private String explanation;
    private Map<String, Object> recommendations;
    private String outputDemoHtml;
    private String outputDemoCss;
    private Map<String, Object> tokens;
    private Map<String, Object> animations;

    //Recursos asociados al trago (si aplica segun el tipo)
    private UUID exerciseId; //prueba final (CODE_EXERCISES)
    private UUID testId;     //mini-test (TESTS)
}