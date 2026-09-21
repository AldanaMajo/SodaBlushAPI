package com.sodablush.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class CanProgressDTO {
    private UUID canId;
    private String canName;
    private String status;
    private UUID currentDrinkId;
    private Integer timesCompleted;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
