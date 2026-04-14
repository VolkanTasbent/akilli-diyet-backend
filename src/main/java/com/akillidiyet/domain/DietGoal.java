package com.akillidiyet.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DietGoal {
    LOSE_WEIGHT("Kilo verme"),
    MAINTAIN("Koruma"),
    GAIN_MUSCLE("Kas kütlesi artırma");

    private final String labelTr;
}
