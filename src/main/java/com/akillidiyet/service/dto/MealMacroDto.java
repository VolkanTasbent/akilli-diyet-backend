package com.akillidiyet.service.dto;

import lombok.Builder;

@Builder
public record MealMacroDto(int calories, double proteinG, double carbsG, double fatG) {}
