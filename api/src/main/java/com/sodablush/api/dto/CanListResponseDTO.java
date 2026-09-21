package com.sodablush.api.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class CanListResponseDTO {
    private UUID id;
    private String name;
    private String difficulty;
    private Boolean isLocked; 
    private String fullImageUrl;
}