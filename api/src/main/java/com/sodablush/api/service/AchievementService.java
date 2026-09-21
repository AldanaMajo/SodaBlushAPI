package com.sodablush.api.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sodablush.api.dto.AchievementDTO;
import com.sodablush.api.dto.UserAchievementDTO;
import com.sodablush.api.model.Achievement;
import com.sodablush.api.model.User;
import com.sodablush.api.model.UserAchievement;
import com.sodablush.api.repository.AchievementRepository;
import com.sodablush.api.repository.CanRepository;
import com.sodablush.api.repository.UserAchievementRepository;
import com.sodablush.api.repository.UserCanProgressRepository;

/**
 * Logros del blueprint:
 * - "Señor de las latas": terminar todas las latas por primera vez.
 * - "Señor del reciclaje": volver a terminarlas despues de resetear el progreso.
 * Los logros deben existir en la tabla ACHIEVEMENTS con estos codes (ver README).
 */
@Service
public class AchievementService {

    public static final String CODE_SENOR_DE_LAS_LATAS = "SENOR_DE_LAS_LATAS";
    public static final String CODE_SENOR_DEL_RECICLAJE = "SENOR_DEL_RECICLAJE";

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final CanRepository canRepository;
    private final UserCanProgressRepository canProgressRepository;

    public AchievementService(AchievementRepository achievementRepository,
            UserAchievementRepository userAchievementRepository,
            CanRepository canRepository,
            UserCanProgressRepository canProgressRepository) {
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
        this.canRepository = canRepository;
        this.canProgressRepository = canProgressRepository;
    }

    public List<AchievementDTO> getAllAchievements() {
        List<AchievementDTO> dtos = new ArrayList<>();
        for (Achievement logro : achievementRepository.findAll()) {
            dtos.add(toDto(logro));
        }
        return dtos;
    }

    public List<UserAchievementDTO> getUserAchievements(User user) {
        List<UserAchievementDTO> dtos = new ArrayList<>();
        for (UserAchievement ua : userAchievementRepository.findByUserId(user.getId())) {
            UserAchievementDTO dto = new UserAchievementDTO();
            dto.setAchievement(toDto(ua.getAchievement()));
            dto.setUnlockedAt(ua.getUnlockedAt());
            dto.setTimesUnlocked(ua.getTimesUnlocked());
            dtos.add(dto);
        }
        return dtos;
    }

    /**
     * Se llama cada vez que el usuario completa una lata.
     * Devuelve los codes de los logros otorgados en esta accion.
     */
    @Transactional
    public List<String> onCanCompleted(User user) {
        long totalLatas = canRepository.count();
        if (totalLatas == 0) {
            return List.of();
        }
        long latasCompletadas = canProgressRepository
                .countByUserIdAndStatus(user.getId(), ProgressService.STATUS_COMPLETED);
        if (latasCompletadas < totalLatas) {
            return List.of();
        }

        List<String> otorgados = new ArrayList<>();
        if (!hasAchievement(user, CODE_SENOR_DE_LAS_LATAS)) {
            if (grant(user, CODE_SENOR_DE_LAS_LATAS)) {
                otorgados.add(CODE_SENOR_DE_LAS_LATAS);
            }
        } else {
            // Ya habia terminado todo antes (y reseteo): logro de reciclaje
            if (grant(user, CODE_SENOR_DEL_RECICLAJE)) {
                otorgados.add(CODE_SENOR_DEL_RECICLAJE);
            }
        }
        return otorgados;
    }

    private boolean hasAchievement(User user, String code) {
        Optional<Achievement> logro = achievementRepository.findByCode(code);
        return logro.isPresent()
                && userAchievementRepository.findByUserIdAndAchievementId(user.getId(), logro.get().getId()).isPresent();
    }

    /** Otorga el logro (o incrementa timesUnlocked si ya lo tenia). */
    private boolean grant(User user, String code) {
        Optional<Achievement> logroOpt = achievementRepository.findByCode(code);
        if (logroOpt.isEmpty()) {
            // El logro no esta cargado en la base de datos; no es un error del usuario.
            return false;
        }
        Achievement logro = logroOpt.get();

        Optional<UserAchievement> existente = userAchievementRepository
                .findByUserIdAndAchievementId(user.getId(), logro.getId());

        if (existente.isPresent()) {
            UserAchievement ua = existente.get();
            int veces = ua.getTimesUnlocked() != null ? ua.getTimesUnlocked() : 1;
            ua.setTimesUnlocked(veces + 1);
            ua.setUnlockedAt(LocalDateTime.now());
            userAchievementRepository.save(ua);
        } else {
            UserAchievement ua = new UserAchievement();
            ua.setId(UUID.randomUUID());
            ua.setUser(user);
            ua.setAchievement(logro);
            ua.setProgress(new BigDecimal("100"));
            ua.setUnlockedAt(LocalDateTime.now());
            ua.setTimesUnlocked(1);
            userAchievementRepository.save(ua);
        }
        return true;
    }

    private AchievementDTO toDto(Achievement logro) {
        AchievementDTO dto = new AchievementDTO();
        dto.setId(logro.getId());
        dto.setCode(logro.getCode());
        dto.setName(logro.getName());
        dto.setDescription(logro.getDescription());
        dto.setIconUrl(logro.getIconUrl());
        dto.setType(logro.getType());
        dto.setPoints(logro.getPoints());
        return dto;
    }
}
