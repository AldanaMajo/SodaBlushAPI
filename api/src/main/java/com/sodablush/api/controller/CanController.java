package com.sodablush.api.controller;

import com.sodablush.api.dto.CanDetailResponseDTO;
import com.sodablush.api.dto.CanListResponseDTO;
import com.sodablush.api.dto.StartCanResponseDTO;
import com.sodablush.api.model.User;
import com.sodablush.api.service.CanService;
import com.sodablush.api.service.CurrentUserService;
import com.sodablush.api.service.ProgressService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sodablush.api.dto.DrinkDTO;
import com.sodablush.api.service.DrinkService;

import java.util.List;
import java.util.UUID;

@RestController 
@RequestMapping("/api/cans") 
public class CanController {

    private final CanService canService;
    private final DrinkService drinkService;
    private final ProgressService progressService;
    private final CurrentUserService currentUserService;

    public CanController(CanService canService, DrinkService drinkService,
            ProgressService progressService, CurrentUserService currentUserService) {
        this.canService = canService;
        this.drinkService = drinkService;
        this.progressService = progressService;
        this.currentUserService = currentUserService;
    }

    @GetMapping 
    public List<CanListResponseDTO> getAllCans(@AuthenticationPrincipal Jwt jwt) {
        User usuario = currentUserService.requireActiveUser(jwt);
        return canService.getAllCansForUser(usuario.getId());
    }

    @GetMapping("/{id}")
    public CanDetailResponseDTO getCanDetails(@PathVariable UUID id) {
        return canService.getCanById(id);
    }

    @GetMapping("/{canId}/drinks")
    public List<DrinkDTO> getDrinksByCan(@PathVariable UUID canId) {
        return drinkService.getDrinksForCan(canId);
    }

    /** Abre una lata (animacion de abrir + primer trago). */
    @PostMapping("/{canId}/start")
    public StartCanResponseDTO startCan(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID canId) {
        User usuario = currentUserService.requireActiveUser(jwt);
        return progressService.startCan(usuario, canId);
    }
}
