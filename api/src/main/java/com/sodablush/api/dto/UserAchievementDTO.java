package com.sodablush.api.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserAchievementDTO {
    private AchievementDTO achievement;
    private LocalDateTime unlockedAt;
    private Integer timesUnlocked;
}
