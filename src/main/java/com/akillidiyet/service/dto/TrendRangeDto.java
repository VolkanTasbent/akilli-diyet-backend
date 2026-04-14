package com.akillidiyet.service.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record TrendRangeDto(
        LocalDate from,
        LocalDate to,
        int targetCalories,
        double targetProteinG,
        List<TrendDayDto> days) {}
