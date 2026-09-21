package com.sodablush.api.dto;

import java.util.List;

import lombok.Data;

@Data
public class ProgressSummaryDTO {
    private long totalCans;
    private long completedCans;
    private List<CanProgressDTO> cans;
    private List<DrinkProgressDTO> drinks;
}
