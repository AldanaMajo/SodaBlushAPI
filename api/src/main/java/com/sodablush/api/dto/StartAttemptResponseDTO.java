package com.sodablush.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class StartAttemptResponseDTO {
    private UUID attemptId;
    private UUID testId;
    private Integer livesRemaining;
    private LocalDateTime startedAt;
}
