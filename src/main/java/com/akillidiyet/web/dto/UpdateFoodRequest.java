package com.akillidiyet.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateFoodRequest(
        @NotBlank @Size(max = 200) String name,
        @Positive double caloriesPer100g,
        double proteinPer100g,
        double carbsPer100g,
        double fatPer100g,
        Double tablespoonGrams,
        Double sliceGrams) {}
