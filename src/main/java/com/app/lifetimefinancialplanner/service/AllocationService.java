package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.AllocationDTO;
import com.app.lifetimefinancialplanner.domain.embeddable.AllocationEmbeddable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AllocationService {

    // Convert DTO to embeddable
    public AllocationEmbeddable convertDTOToEmbeddable(AllocationDTO dto) {
        if (dto == null) {
            return null;
        }
        AllocationEmbeddable item = new AllocationEmbeddable();
        item.setInvestmentKey(dto.getInvestmentKey());
        item.setRatio(dto.getRatio());
        return item;
    }

    // Convert embeddable to DTO.
    public AllocationDTO convertEmbeddableToDTO(AllocationEmbeddable item) {
        if (item == null) {
            return null;
        }
        AllocationDTO dto = new AllocationDTO();
        dto.setInvestmentKey(item.getInvestmentKey());
        dto.setRatio(item.getRatio());
        return dto;
    }

    // Convert list of DTOs to list of embeddable
    public List<AllocationEmbeddable> convertDTOListToEmbeddableList(List<AllocationDTO> dtoList) {
        if (dtoList == null) {
            return null;
        }
        return dtoList.stream()
                .map(this::convertDTOToEmbeddable)
                .collect(Collectors.toList());
    }

    // Convert list of embeddable to list of DTOs
    public List<AllocationDTO> convertEmbeddableListToDTOList(List<AllocationEmbeddable> itemList) {
        if (itemList == null) {
            return null;
        }
        return itemList.stream()
                .map(this::convertEmbeddableToDTO)
                .collect(Collectors.toList());
    }
}
