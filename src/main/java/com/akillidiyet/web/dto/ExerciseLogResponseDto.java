package com.akillidiyet.web.dto;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record ExerciseLogResponseDto(Long id, LocalDate date, int caloriesBurned, String label) {}
