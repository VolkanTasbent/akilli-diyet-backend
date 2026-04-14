package com.akillidiyet.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record AddExerciseLogRequest(
        @NotNull LocalDate date,
        @Positive int caloriesBurned,
        @Size(max = 120) String label) {}
