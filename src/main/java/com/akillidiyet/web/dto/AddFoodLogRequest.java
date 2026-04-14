package com.akillidiyet.web.dto;

import com.akillidiyet.domain.MealType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record AddFoodLogRequest(
        @NotNull LocalDate date,
        @NotNull MealType mealType,
        @NotNull Long foodId,
        @Positive double grams,
        String note) {}
