package com.umcsuser.carrent.web;

import com.umcsuser.carrent.models.VehicleCategoryConfig;
import com.umcsuser.carrent.services.impl.VehicleCategoryConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final VehicleCategoryConfigService categoryService;

    public CategoryController(VehicleCategoryConfigService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<VehicleCategoryConfig> list() {
        return categoryService.findAll();
    }

    @GetMapping("/{category}")
    public VehicleCategoryConfig get(@PathVariable String category) {
        return categoryService.findByCategory(category);
    }
}