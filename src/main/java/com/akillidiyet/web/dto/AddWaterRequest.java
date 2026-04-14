package com.akillidiyet.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record AddWaterRequest(@NotNull LocalDate date, @Positive int addMl) {}
