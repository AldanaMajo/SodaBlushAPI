package com.sodablush.api.service;

import com.sodablush.api.dto.CanDetailResponseDTO;
import com.sodablush.api.dto.CanListResponseDTO;
import com.sodablush.api.exception.NotFoundException;
import com.sodablush.api.model.Can;
import com.sodablush.api.repository.CanRepository;
import com.sodablush.api.repository.UserCanProgressRepository; 
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service 
public class CanService {

    private final CanRepository canRepository;
    private final UserCanProgressRepository progressRepository; 


    public CanService(CanRepository canRepository, UserCanProgressRepository progressRepository) {
        this.canRepository = canRepository;
        this.progressRepository = progressRepository;
    }

    public List<CanListResponseDTO> getAllCansForUser(UUID userId) {
        List<Can> todasLasLatas = canRepository.findAll();
        
        
        Integer maxNivelCompletado = progressRepository.findHighestCompletedLevel(userId);
      
        if (maxNivelCompletado == null) {
            maxNivelCompletado = 0;
        }

        List<CanListResponseDTO> latasParaFrontend = new ArrayList<>();

        for (Can lata : todasLasLatas) {
            CanListResponseDTO dto = new CanListResponseDTO();
            
            dto.setId(lata.getId());
            dto.setName(lata.getName());
            dto.setDifficulty(lata.getDifficulty());
            dto.setFullImageUrl(lata.getFullImageUrl());

            boolean estaBloqueada = lata.getUnlockOrder() != null
                    && lata.getUnlockOrder() > (maxNivelCompletado + 1);
            dto.setIsLocked(estaBloqueada);

            latasParaFrontend.add(dto);
        }

        return latasParaFrontend;
    }
//CAN DETAIL
    public CanDetailResponseDTO getCanById(UUID canId) {
        Can lata = canRepository.findById(canId)
                .orElseThrow(() -> new NotFoundException("Lata no encontrada en la base de datos"));

        CanDetailResponseDTO dto = new CanDetailResponseDTO();
        dto.setId(lata.getId());
        dto.setName(lata.getName());
        dto.setDescription(lata.getDescription());
        dto.setDifficulty(lata.getDifficulty());
        dto.setUnlockOrder(lata.getUnlockOrder());
        dto.setFullImageUrl(lata.getFullImageUrl());
        dto.setEmptyImageUrl(lata.getEmptyImageUrl());
        dto.setOpenSoundUrl(lata.getOpenSoundUrl());
        dto.setCrushSoundUrl(lata.getCrushSoundUrl());

        return dto;
    }
}
