package com.akillidiyet.web.dto;

import lombok.Builder;

@Builder
public record AuthResponse(String token, UserResponse user) {}
