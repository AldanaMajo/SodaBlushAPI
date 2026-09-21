package com.sodablush.api.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class DrinkDTO {
    private UUID id;
    private String title;
    private Integer stepOrder;
    private String type;
    private UUID canId; 
}