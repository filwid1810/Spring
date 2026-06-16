package com.umcsuser.carrent.services.impl;

import com.umcsuser.carrent.models.VehicleCategoryConfig;
import com.umcsuser.carrent.repositories.VehicleCategoryConfigRepository;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class  VehicleCategoryConfigService {
    private final VehicleCategoryConfigRepository configRepository;

    public VehicleCategoryConfigService(VehicleCategoryConfigRepository configRepository) {
        this.configRepository = configRepository;
    }
    public List<VehicleCategoryConfig> findAll() {
        return configRepository.findAll();
    }

    public VehicleCategoryConfig findByCategory(String category) {

        return configRepository.findAll().stream()
                .filter(c -> c.getCategory().equalsIgnoreCase(category))
                .findFirst()
                .orElse(null);
    }
    public List<VehicleCategoryConfig> findAllCategories() {
        return configRepository.findAll();
    }

    public VehicleCategoryConfig getByCategory(String category) {
        return configRepository.findByCategory(category)
                .orElseThrow(() -> new IllegalArgumentException("Nieznana kategoria pojazdu: " + category));
    }

    public boolean categoryExists(String category) {
        return configRepository.findByCategory(category).isPresent();
    }
}

