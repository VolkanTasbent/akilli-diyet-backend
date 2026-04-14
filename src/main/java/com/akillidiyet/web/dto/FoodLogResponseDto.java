package com.akillidiyet.web.dto;

import com.akillidiyet.domain.MealType;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record FoodLogResponseDto(
        Long id,
        LocalDate date,
        MealType mealType,
        Long foodId,
        String foodName,
        double grams,
        String note,
        int caloriesEstimate) {}
