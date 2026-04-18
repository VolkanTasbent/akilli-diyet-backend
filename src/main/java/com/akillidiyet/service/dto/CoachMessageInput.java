package com.akillidiyet.service.dto;

import com.akillidiyet.domain.DietGoal;
import com.akillidiyet.domain.MealType;
import java.util.Map;

public record CoachMessageInput(
        int consumedCalories,
        int exerciseBurned,
        int targetCalories,
        int caloriesRemaining,
        double proteinG,
        double targetProteinG,
        double carbsG,
        double targetCarbsG,
        double fatG,
        double targetFatG,
        int waterMl,
        int waterGoalMl,
        Double sleepHours,
        DietGoal dietGoal,
        Map<MealType, MealMacroDto> byMeal) {}
