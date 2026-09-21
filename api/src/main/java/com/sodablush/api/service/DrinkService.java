package com.sodablush.api.service;

import com.sodablush.api.dto.DrinkDTO;
import com.sodablush.api.dto.DrinkDetailResponseDTO;
import com.sodablush.api.exception.NotFoundException;
import com.sodablush.api.repository.CodeExerciseRepository;
import com.sodablush.api.repository.DrinkDefinitionRepository;
import com.sodablush.api.repository.DrinkSyntaxRepository;
import com.sodablush.api.repository.TestRepository;
import com.sodablush.api.model.Drink;
import com.sodablush.api.repository.DrinkRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DrinkService {

    private final DrinkRepository drinkRepository;
    private final DrinkDefinitionRepository definitionRepo;
    private final DrinkSyntaxRepository syntaxRepo;
    private final CodeExerciseRepository exerciseRepo;
    private final TestRepository testRepo;

    public DrinkService(DrinkRepository drinkRepository, DrinkDefinitionRepository definitionRepo,
            DrinkSyntaxRepository syntaxRepo, CodeExerciseRepository exerciseRepo, TestRepository testRepo) {
        this.drinkRepository = drinkRepository;
        this.definitionRepo = definitionRepo;
        this.syntaxRepo = syntaxRepo;
        this.exerciseRepo = exerciseRepo;
        this.testRepo = testRepo;
    }

    public List<DrinkDTO> getDrinksForCan(UUID canId) {
        List<Drink> tragos = drinkRepository.findByCanIdOrderByStepOrderAsc(canId);
        
        List<DrinkDTO> tragosParaFrontend = new ArrayList<>();

        for (Drink trago : tragos) {
            DrinkDTO dto = new DrinkDTO();
            dto.setId(trago.getId());
            dto.setTitle(trago.getTitle());
            dto.setStepOrder(trago.getStepOrder());
            dto.setType(trago.getType());
            
            dto.setCanId(trago.getCan().getId()); 
            
            tragosParaFrontend.add(dto);
        }

        return tragosParaFrontend;
    }
    
    public DrinkDetailResponseDTO getDrinkDetails(UUID drinkId) {
        //busca el trago 
        Drink tragoBase = drinkRepository.findById(drinkId)
                .orElseThrow(() -> new NotFoundException("Lección no encontrada"));

        //dto con los datos básicos
        DrinkDetailResponseDTO dto = new DrinkDetailResponseDTO();
        dto.setId(tragoBase.getId());
        dto.setTitle(tragoBase.getTitle());
        dto.setType(tragoBase.getType());
        dto.setStepOrder(tragoBase.getStepOrder());

        // logica de dto dependiendo del tipo de lección
        if ("Definicion".equalsIgnoreCase(tragoBase.getType())) {
            
            //  DRINK_DEFINITIONS
            definitionRepo.findById(drinkId).ifPresent(definicion -> {
                dto.setDefinitionText(definicion.getDefinitionText());
                dto.setShortText(definicion.getShortText());
                dto.setAnimatedGifUrl(definicion.getAnimatedGifUrl());
                dto.setExampleCode(definicion.getExampleCode());
                dto.setTips(definicion.getTips());
            });

        } else if ("Sintaxis".equalsIgnoreCase(tragoBase.getType())) {
            
            // DRINK_SYNTAXES
            syntaxRepo.findById(drinkId).ifPresent(sintaxis -> {
                dto.setSyntaxTemplate(sintaxis.getSyntaxTemplate());
                dto.setExplanation(sintaxis.getExplanation());
                dto.setRecommendations(sintaxis.getRecommendations());
                dto.setOutputDemoHtml(sintaxis.getOutputDemoHtml());
                dto.setOutputDemoCss(sintaxis.getOutputDemoCss());
                dto.setTokens(sintaxis.getTokens());
                dto.setAnimations(sintaxis.getAnimations());
            });
        }

        // Para tragos tipo mini-test o prueba final, exponemos el id del recurso
        // para que el cliente sepa a que endpoint ir.
        exerciseRepo.findByDrinkId(drinkId).ifPresent(ejercicio -> dto.setExerciseId(ejercicio.getId()));
        testRepo.findByDrinkId(drinkId).ifPresent(test -> dto.setTestId(test.getId()));

        return dto;
    }


}