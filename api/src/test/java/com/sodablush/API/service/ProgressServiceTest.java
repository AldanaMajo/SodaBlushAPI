package com.sodablush.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.sodablush.api.dto.CompleteDrinkResponseDTO;
import com.sodablush.api.dto.StartCanResponseDTO;
import com.sodablush.api.exception.ForbiddenException;
import com.sodablush.api.model.Can;
import com.sodablush.api.model.Drink;
import com.sodablush.api.model.User;
import com.sodablush.api.model.UserCanProgress;
import com.sodablush.api.repository.CanRepository;
import com.sodablush.api.repository.DrinkRepository;
import com.sodablush.api.repository.UserCanProgressRepository;
import com.sodablush.api.repository.UserDrinkProgressRepository;

/** Unlock y completar lata (sin base de datos). */
class ProgressServiceTest {

    private final CanRepository canRepository = Mockito.mock(CanRepository.class);
    private final DrinkRepository drinkRepository = Mockito.mock(DrinkRepository.class);
    private final UserCanProgressRepository canProgressRepository = Mockito.mock(UserCanProgressRepository.class);
    private final UserDrinkProgressRepository drinkProgressRepository = Mockito.mock(UserDrinkProgressRepository.class);
    private final AchievementService achievementService = Mockito.mock(AchievementService.class);

    private final ProgressService service = new ProgressService(canRepository, drinkRepository,
            canProgressRepository, drinkProgressRepository, achievementService);

    private User user;
    private Can lata;
    private Drink trago;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());

        lata = new Can();
        lata.setId(UUID.randomUUID());
        lata.setUnlockOrder(1);
        lata.setName("Hover");

        trago = new Drink();
        trago.setId(UUID.randomUUID());
        trago.setCan(lata);
        trago.setStepOrder(1);
        trago.setTitle("Definicion");
    }

    @Test
    void startCanFailsWhenLocked() {
        lata.setUnlockOrder(3);
        when(canRepository.findById(lata.getId())).thenReturn(Optional.of(lata));
        when(canProgressRepository.findHighestCompletedLevel(user.getId())).thenReturn(0);

        assertThrows(ForbiddenException.class, () -> service.startCan(user, lata.getId()));
    }

    @Test
    void startCanOpensFirstDrink() {
        when(canRepository.findById(lata.getId())).thenReturn(Optional.of(lata));
        when(canProgressRepository.findHighestCompletedLevel(user.getId())).thenReturn(null);
        when(canProgressRepository.findByUserIdAndCanId(user.getId(), lata.getId())).thenReturn(Optional.empty());
        when(drinkRepository.findByCanIdOrderByStepOrderAsc(lata.getId())).thenReturn(List.of(trago));
        when(canProgressRepository.save(any(UserCanProgress.class))).thenAnswer(inv -> inv.getArgument(0));

        StartCanResponseDTO dto = service.startCan(user, lata.getId());

        assertEquals(lata.getId(), dto.getCanId());
        assertEquals(trago.getId(), dto.getCurrentDrinkId());
        assertEquals(ProgressService.STATUS_IN_PROGRESS, dto.getStatus());
        assertFalse(dto.isAlreadyStarted());
    }

    @Test
    void completeLastDrinkWhenProgressAlreadySavedCompletesCan() {
        UserCanProgress progresoLata = new UserCanProgress();
        progresoLata.setId(UUID.randomUUID());
        progresoLata.setUser(user);
        progresoLata.setCan(lata);
        progresoLata.setStatus(ProgressService.STATUS_IN_PROGRESS);
        progresoLata.setTimesCompleted(0);

        com.sodablush.api.model.UserDrinkProgress progresoTrago = new com.sodablush.api.model.UserDrinkProgress();
        progresoTrago.setId(UUID.randomUUID());
        progresoTrago.setUser(user);
        progresoTrago.setCan(lata);
        progresoTrago.setDrink(trago);
        progresoTrago.setStatus(ProgressService.STATUS_COMPLETED);
        progresoTrago.setAttempts(1);

        when(drinkRepository.findById(trago.getId())).thenReturn(Optional.of(trago));
        when(canProgressRepository.findHighestCompletedLevel(user.getId())).thenReturn(null);
        when(canProgressRepository.findByUserIdAndCanId(user.getId(), lata.getId()))
                .thenReturn(Optional.of(progresoLata));
        when(drinkProgressRepository.findByUserIdAndDrinkId(user.getId(), trago.getId()))
                .thenReturn(Optional.of(progresoTrago));
        when(drinkProgressRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(drinkRepository.findByCanIdOrderByStepOrderAsc(lata.getId())).thenReturn(List.of(trago));
        when(drinkProgressRepository.findByUserId(user.getId())).thenReturn(List.of(progresoTrago));
        when(canProgressRepository.save(any(UserCanProgress.class))).thenAnswer(inv -> inv.getArgument(0));
        when(achievementService.onCanCompleted(user)).thenReturn(List.of("SENOR_DE_LAS_LATAS"));

        CompleteDrinkResponseDTO dto = service.completeDrink(user, trago.getId());

        assertTrue(dto.isCanCompleted());
        assertEquals(List.of("SENOR_DE_LAS_LATAS"), dto.getNewAchievements());
        ArgumentCaptor<UserCanProgress> captor = ArgumentCaptor.forClass(UserCanProgress.class);
        verify(canProgressRepository).save(captor.capture());
        assertEquals(ProgressService.STATUS_COMPLETED, captor.getValue().getStatus());
        assertEquals(1, captor.getValue().getTimesCompleted());
    }
}
