package com.sodablush.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sodablush.api.dto.ProgressSummaryDTO;
import com.sodablush.api.dto.UpdateProfileRequestDTO;
import com.sodablush.api.dto.UpdateStatusRequestDTO;
import com.sodablush.api.dto.UserAchievementDTO;
import com.sodablush.api.dto.UserProfileDTO;
import com.sodablush.api.model.User;
import com.sodablush.api.service.AchievementService;
import com.sodablush.api.service.CurrentUserService;
import com.sodablush.api.service.ProgressService;

import jakarta.validation.Valid;

/** Perfil, preferencias, progreso y logros del usuario autenticado. */
@RestController
@RequestMapping("/api/me")
public class MeController {

    private final CurrentUserService currentUserService;
    private final ProgressService progressService;
    private final AchievementService achievementService;

    public MeController(CurrentUserService currentUserService, ProgressService progressService,
            AchievementService achievementService) {
        this.currentUserService = currentUserService;
        this.progressService = progressService;
        this.achievementService = achievementService;
    }

    @GetMapping
    public UserProfileDTO getProfile(@AuthenticationPrincipal Jwt jwt) {
        return toDto(currentUserService.getOrCreateUser(jwt));
    }

    @PatchMapping
    public UserProfileDTO updateProfile(@AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateProfileRequestDTO request) {
        User usuario = currentUserService.getOrCreateUser(jwt);
        return toDto(currentUserService.updateProfile(usuario, request));
    }

    /** Suspender o reactivar la cuenta ('active' / 'suspended'). */
    @PatchMapping("/status")
    public UserProfileDTO updateStatus(@AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateStatusRequestDTO request) {
        User usuario = currentUserService.getOrCreateUser(jwt);
        return toDto(currentUserService.updateAccountStatus(usuario, request.getStatus()));
    }

    @GetMapping("/progress")
    public ProgressSummaryDTO getProgress(@AuthenticationPrincipal Jwt jwt) {
        User usuario = currentUserService.requireActiveUser(jwt);
        return progressService.getSummary(usuario);
    }

    /** Resetea el progreso (las latas vuelven a estar cerradas); los logros se conservan. */
    @PostMapping("/progress/reset")
    public Map<String, String> resetProgress(@AuthenticationPrincipal Jwt jwt) {
        User usuario = currentUserService.requireActiveUser(jwt);
        progressService.resetProgress(usuario);
        return Map.of("message", "Progreso reiniciado. Los logros se conservan.");
    }

    @GetMapping("/achievements")
    public List<UserAchievementDTO> getMyAchievements(@AuthenticationPrincipal Jwt jwt) {
        User usuario = currentUserService.requireActiveUser(jwt);
        return achievementService.getUserAchievements(usuario);
    }

    private UserProfileDTO toDto(User usuario) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(usuario.getId());
        dto.setEmail(usuario.getEmail());
        dto.setDisplayName(usuario.getDisplayName());
        dto.setAvatarSeed(usuario.getAvatarSeed());
        dto.setLocale(usuario.getLocale());
        dto.setTheme(usuario.getTheme());
        dto.setSoundSettings(usuario.getSoundSettings());
        dto.setNotificationSettings(usuario.getNotificationSettings());
        dto.setAccountStatus(currentUserService.isSuspended(usuario)
                ? CurrentUserService.STATUS_SUSPENDED
                : CurrentUserService.STATUS_ACTIVE);
        dto.setCreatedAt(usuario.getCreatedAt());
        return dto;
    }
}
