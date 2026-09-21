package com.sodablush.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sodablush.api.dto.AchievementDTO;
import com.sodablush.api.service.AchievementService;

/** Catalogo de logros (los del usuario estan en /api/me/achievements). */
@RestController
@RequestMapping("/api/achievements")
public class AchievementController {

    private final AchievementService achievementService;

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @GetMapping
    public List<AchievementDTO> getAllAchievements() {
        return achievementService.getAllAchievements();
    }
}
