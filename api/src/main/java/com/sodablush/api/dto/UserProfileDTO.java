package com.sodablush.api.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import lombok.Data;

@Data
public class UserProfileDTO {
    private UUID id;
    private String email;
    private String displayName;
    private String avatarSeed;
    private String locale;
    private String theme;
    private Map<String, Object> soundSettings;
    private Map<String, Object> notificationSettings;
    private String accountStatus;
    private LocalDateTime createdAt;
}
