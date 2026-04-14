package com.akillidiyet.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActivityLevel {
    SEDENTARY(1.2, "Hareketsiz"),
    LIGHT(1.375, "Hafif aktif"),
    MODERATE(1.55, "Orta aktif"),
    ACTIVE(1.725, "Çok aktif"),
    VERY_ACTIVE(1.9, "Aşırı aktif");

    private final double multiplier;
    private final String labelTr;
}
