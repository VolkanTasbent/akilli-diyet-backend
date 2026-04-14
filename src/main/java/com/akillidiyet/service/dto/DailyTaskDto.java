package com.akillidiyet.service.dto;

import lombok.Builder;

@Builder
public record DailyTaskDto(String id, String labelTr, boolean done) {}
