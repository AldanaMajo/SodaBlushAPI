package com.sodablush.api.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class AchievementDTO {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private String iconUrl;
    private String type;
    private Integer points;
}
