package com.sodablush.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class DrinkProgressDTO {
    private UUID drinkId;
    private UUID canId;
    private String drinkTitle;
    private String status;
    private BigDecimal score;
    private Integer attempts;
    private LocalDateTime completedAt;
}
