package com.akillidiyet.service.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record WeeklyScoreDto(
        int score,
        String periodLabelTr,
        List<String> hintsTr) {}
