package com.akillidiyet.web.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AddSleepLogRequest(
        @NotNull LocalDate date,
        @DecimalMin(value = "0.5", inclusive = true) @DecimalMax(value = "24", inclusive = true) double hoursSlept) {}
