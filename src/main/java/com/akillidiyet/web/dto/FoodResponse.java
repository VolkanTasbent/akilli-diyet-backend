package com.akillidiyet.web.dto;

import lombok.Builder;

@Builder
public record FoodResponse(
        Long id,
        String name,
        double caloriesPer100g,
        double proteinPer100g,
        double carbsPer100g,
        double fatPer100g,
        Double tablespoonGrams,
        boolean custom) {}
