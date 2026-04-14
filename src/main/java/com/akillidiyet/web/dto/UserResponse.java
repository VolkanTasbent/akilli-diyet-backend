package com.akillidiyet.web.dto;

import com.akillidiyet.domain.ActivityLevel;
import com.akillidiyet.domain.DietGoal;
import com.akillidiyet.domain.Gender;
import lombok.Builder;

@Builder
public record UserResponse(
        Long id,
        String email,
        String displayName,
        Double heightCm,
        Double weightKg,
        Integer age,
        Gender gender,
        ActivityLevel activityLevel,
        DietGoal dietGoal,
        Double targetWeightKg,
        Integer goalDurationWeeks,
        String city,
        Boolean studentMode,
        Integer dailyWaterGoalMl) {}
