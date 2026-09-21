package com.sodablush.api.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sodablush.api.dto.CanProgressDTO;
import com.sodablush.api.dto.CompleteDrinkResponseDTO;
import com.sodablush.api.dto.DrinkProgressDTO;
import com.sodablush.api.dto.ProgressSummaryDTO;
import com.sodablush.api.dto.StartCanResponseDTO;
import com.sodablush.api.exception.BadRequestException;
import com.sodablush.api.exception.ForbiddenException;
import com.sodablush.api.exception.NotFoundException;
import com.sodablush.api.model.Can;
import com.sodablush.api.model.Drink;
import com.sodablush.api.model.User;
import com.sodablush.api.model.UserCanProgress;
import com.sodablush.api.model.UserDrinkProgress;
import com.sodablush.api.repository.CanRepository;
import com.sodablush.api.repository.DrinkRepository;
import com.sodablush.api.repository.UserCanProgressRepository;
import com.sodablush.api.repository.UserDrinkProgressRepository;

/**
 * Ciclo de vida del aprendizaje: abrir latas, completar tragos,
 * desbloquear la siguiente lata y resetear el progreso.
 */
@Service
public class ProgressService {

    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_COMPLETED = "COMPLETED";

    private final CanRepository canRepository;
    private final DrinkRepository drinkRepository;
    private final UserCanProgressRepository canProgressRepository;
    private final UserDrinkProgressRepository drinkProgressRepository;
    private final AchievementService achievementService;

    public ProgressService(CanRepository canRepository, DrinkRepository drinkRepository,
            UserCanProgressRepository canProgressRepository,
            UserDrinkProgressRepository drinkProgressRepository,
            AchievementService achievementService) {
        this.canRepository = canRepository;
        this.drinkRepository = drinkRepository;
        this.canProgressRepository = canProgressRepository;
        this.drinkProgressRepository = drinkProgressRepository;
        this.achievementService = achievementService;
    }

    /** Abre una lata (idempotente: si ya estaba abierta devuelve el estado actual). */
    @Transactional
    public StartCanResponseDTO startCan(User user, UUID canId) {
        Can lata = canRepository.findById(canId)
                .orElseThrow(() -> new NotFoundException("Lata no encontrada"));
        ensureUnlocked(user, lata);

        Optional<UserCanProgress> existente = canProgressRepository.findByUserIdAndCanId(user.getId(), canId);
        if (existente.isPresent()) {
            UserCanProgress progreso = existente.get();
            StartCanResponseDTO dto = new StartCanResponseDTO();
            dto.setCanId(canId);
            dto.setStatus(progreso.getStatus());
            dto.setCurrentDrinkId(progreso.getCurrentDrink() != null ? progreso.getCurrentDrink().getId() : null);
            dto.setAlreadyStarted(true);
            return dto;
        }

        List<Drink> tragos = drinkRepository.findByCanIdOrderByStepOrderAsc(canId);
        if (tragos.isEmpty()) {
            throw new BadRequestException("Esta lata todavia no tiene tragos cargados");
        }

        UserCanProgress progreso = new UserCanProgress();
        progreso.setId(UUID.randomUUID());
        progreso.setUser(user);
        progreso.setCan(lata);
        progreso.setStatus(STATUS_IN_PROGRESS);
        progreso.setCurrentDrink(tragos.get(0));
        progreso.setStartedAt(LocalDateTime.now());
        progreso.setTimesCompleted(0);
        canProgressRepository.save(progreso);

        StartCanResponseDTO dto = new StartCanResponseDTO();
        dto.setCanId(canId);
        dto.setStatus(STATUS_IN_PROGRESS);
        dto.setCurrentDrinkId(tragos.get(0).getId());
        dto.setAlreadyStarted(false);
        return dto;
    }

    /**
     * Marca un trago como completado y avanza el progreso de la lata.
     * Si era el ultimo trago, completa la lata (y revisa logros).
     */
    @Transactional
    public CompleteDrinkResponseDTO completeDrink(User user, UUID drinkId) {
        Drink trago = drinkRepository.findById(drinkId)
                .orElseThrow(() -> new NotFoundException("Trago no encontrado"));
        Can lata = trago.getCan();
        ensureUnlocked(user, lata);

        // Si la lata no estaba abierta, la abrimos automaticamente
        UserCanProgress progresoLata = canProgressRepository
                .findByUserIdAndCanId(user.getId(), lata.getId())
                .orElseGet(() -> {
                    UserCanProgress nuevo = new UserCanProgress();
                    nuevo.setId(UUID.randomUUID());
                    nuevo.setUser(user);
                    nuevo.setCan(lata);
                    nuevo.setStatus(STATUS_IN_PROGRESS);
                    nuevo.setStartedAt(LocalDateTime.now());
                    nuevo.setTimesCompleted(0);
                    return canProgressRepository.save(nuevo);
                });

        // Upsert del progreso del trago
        UserDrinkProgress progresoTrago = drinkProgressRepository
                .findByUserIdAndDrinkId(user.getId(), drinkId)
                .orElseGet(() -> {
                    UserDrinkProgress nuevo = new UserDrinkProgress();
                    nuevo.setId(UUID.randomUUID());
                    nuevo.setUser(user);
                    nuevo.setCan(lata);
                    nuevo.setDrink(trago);
                    nuevo.setAttempts(0);
                    return nuevo;
                });
        int intentos = progresoTrago.getAttempts() != null ? progresoTrago.getAttempts() : 0;
        progresoTrago.setAttempts(intentos + 1);
        progresoTrago.setStatus(STATUS_COMPLETED);
        progresoTrago.setCompletedAt(LocalDateTime.now());
        drinkProgressRepository.save(progresoTrago);

        // Calcular el siguiente trago pendiente
        List<Drink> tragos = drinkRepository.findByCanIdOrderByStepOrderAsc(lata.getId());
        Set<UUID> completados = new HashSet<>();
        completados.add(drinkId);
        for (UserDrinkProgress p : drinkProgressRepository.findByUserId(user.getId())) {
            if (p.getCan() != null && lata.getId().equals(p.getCan().getId())
                    && STATUS_COMPLETED.equals(p.getStatus())
                    && p.getDrink() != null) {
                completados.add(p.getDrink().getId());
            }
        }
        Drink siguiente = null;
        for (Drink d : tragos) {
            if (!completados.contains(d.getId())) {
                siguiente = d;
                break;
            }
        }

        CompleteDrinkResponseDTO respuesta = new CompleteDrinkResponseDTO();
        respuesta.setDrinkId(drinkId);
        respuesta.setNewAchievements(List.of());

        boolean lataYaCompletada = STATUS_COMPLETED.equals(progresoLata.getStatus());

        if (siguiente == null && !lataYaCompletada) {
            // Ultimo trago: se completa la lata
            progresoLata.setStatus(STATUS_COMPLETED);
            progresoLata.setCompletedAt(LocalDateTime.now());
            int veces = progresoLata.getTimesCompleted() != null ? progresoLata.getTimesCompleted() : 0;
            progresoLata.setTimesCompleted(veces + 1);
            progresoLata.setCurrentDrink(null);
            canProgressRepository.save(progresoLata);

            respuesta.setCanCompleted(true);
            respuesta.setNewAchievements(achievementService.onCanCompleted(user));
        } else if (!lataYaCompletada) {
            progresoLata.setCurrentDrink(siguiente);
            canProgressRepository.save(progresoLata);
            respuesta.setCanCompleted(false);
            respuesta.setNextDrinkId(siguiente.getId());
        } else {
            // Replay de una lata ya completada: no cambia el estado de la lata
            respuesta.setCanCompleted(false);
            respuesta.setNextDrinkId(siguiente != null ? siguiente.getId() : null);
        }

        return respuesta;
    }

    public ProgressSummaryDTO getSummary(User user) {
        ProgressSummaryDTO resumen = new ProgressSummaryDTO();
        resumen.setTotalCans(canRepository.count());
        resumen.setCompletedCans(canProgressRepository.countByUserIdAndStatus(user.getId(), STATUS_COMPLETED));

        List<CanProgressDTO> latas = new ArrayList<>();
        for (UserCanProgress p : canProgressRepository.findByUserId(user.getId())) {
            CanProgressDTO dto = new CanProgressDTO();
            dto.setCanId(p.getCan().getId());
            dto.setCanName(p.getCan().getName());
            dto.setStatus(p.getStatus());
            dto.setCurrentDrinkId(p.getCurrentDrink() != null ? p.getCurrentDrink().getId() : null);
            dto.setTimesCompleted(p.getTimesCompleted());
            dto.setStartedAt(p.getStartedAt());
            dto.setCompletedAt(p.getCompletedAt());
            latas.add(dto);
        }
        resumen.setCans(latas);

        List<DrinkProgressDTO> tragos = new ArrayList<>();
        for (UserDrinkProgress p : drinkProgressRepository.findByUserId(user.getId())) {
            DrinkProgressDTO dto = new DrinkProgressDTO();
            dto.setDrinkId(p.getDrink().getId());
            dto.setCanId(p.getCan() != null ? p.getCan().getId() : null);
            dto.setDrinkTitle(p.getDrink().getTitle());
            dto.setStatus(p.getStatus());
            dto.setScore(p.getScore());
            dto.setAttempts(p.getAttempts());
            dto.setCompletedAt(p.getCompletedAt());
            tragos.add(dto);
        }
        resumen.setDrinks(tragos);

        return resumen;
    }

    /** Borra el progreso de latas y tragos. Los logros se conservan. */
    @Transactional
    public void resetProgress(User user) {
        drinkProgressRepository.deleteByUserId(user.getId());
        canProgressRepository.deleteByUserId(user.getId());
    }

    private void ensureUnlocked(User user, Can lata) {
        Integer maxCompletado = canProgressRepository.findHighestCompletedLevel(user.getId());
        int nivel = maxCompletado != null ? maxCompletado : 0;
        if (lata.getUnlockOrder() != null && lata.getUnlockOrder() > nivel + 1) {
            throw new ForbiddenException("Esta lata todavia esta bloqueada");
        }
    }
}
