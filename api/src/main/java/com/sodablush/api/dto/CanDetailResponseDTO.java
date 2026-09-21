package com.sodablush.api.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class CanDetailResponseDTO {
    private UUID id;
    private String name;
    private String description;
    private String difficulty;
    private Integer unlockOrder;
    private String fullImageUrl;
    private String emptyImageUrl;
    private String openSoundUrl;
    private String crushSoundUrl;
}
