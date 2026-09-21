package com.sodablush.api.controller;

import com.sodablush.api.dto.CanDetailResponseDTO;
import com.sodablush.api.dto.CanListResponseDTO;
import com.sodablush.api.service.CanService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sodablush.api.dto.DrinkDTO;
import com.sodablush.api.service.DrinkService; // Importa el nuevo servicio

import java.util.List;
import java.util.UUID;

@RestController 
@RequestMapping("/api/cans") 
public class CanController {

    private final CanService canService;
    private final DrinkService drinkService;

    public CanController(CanService canService, DrinkService drinkService) {
        this.canService = canService;
        this.drinkService = drinkService;
    }

    @GetMapping 
    public List<CanListResponseDTO> getAllCans() {
        // Simulamos un ID de usuario por ahora (más adelante, este ID vendrá de Auth0)
        UUID simulatedUserId = UUID.randomUUID(); 
        
        return canService.getAllCansForUser(simulatedUserId);
    }

    @GetMapping("/{id}")
    public CanDetailResponseDTO getCanDetails(@PathVariable UUID id) {
        return canService.getCanById(id);
    }

    @GetMapping("/{canId}/drinks")
    public List<DrinkDTO> getDrinksByCan(@PathVariable UUID canId) {
        return drinkService.getDrinksForCan(canId);
    }
}