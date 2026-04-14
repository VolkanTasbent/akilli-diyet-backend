package com.akillidiyet.service.dto;

import lombok.Builder;

@Builder
public record DailyTargetsDto(
        double bmr,
        double tdee,
        int targetCalories,
        double targetProteinG,
        double targetCarbsG,
        double targetFatG,
        Integer suggestedDailyDeficit,
        String explanationTr) {}
