package com.akillidiyet.service.dto;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record TrendDayDto(
        LocalDate date,
        int calories,
        int exerciseCalories,
        double proteinG,
        double carbsG,
               double fatG,
        int waterMl,
        Double weightKg,
        /** Kayıtlı uyku süresi (saat); yoksa null */
        Double sleepHours) {}
