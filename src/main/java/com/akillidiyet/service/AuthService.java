package com.akillidiyet.service;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.repo.AppUserRepository;
import com.akillidiyet.security.JwtService;
import com.akillidiyet.web.UserMapper;
import com.akillidiyet.web.dto.AuthResponse;
import com.akillidiyet.web.dto.LoginRequest;
import com.akillidiyet.web.dto.RegisterRequest;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmailIgnoreCase(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bu e-posta zaten kayıtlı");
        }
        Instant now = Instant.now();
        AppUser u =
                AppUser.builder()
                        .email(req.email().trim().toLowerCase())
                        .passwordHash(passwordEncoder.encode(req.password()))
                        .displayName(req.displayName().trim())
                        .dailyWaterGoalMl(2000)
                        .studentMode(false)
                        .createdAt(now)
                        .updatedAt(now)
                        .build();
        u = userRepository.save(u);
        String token = jwtService.generateToken(u);
        return AuthResponse.builder().token(token).user(UserMapper.toResponse(u)).build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email().trim().toLowerCase(), req.password()));
        AppUser u =
                userRepository
                        .findByEmailIgnoreCase(req.email().trim().toLowerCase())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Geçersiz giriş"));
        String token = jwtService.generateToken(u);
        return AuthResponse.builder().token(token).user(UserMapper.toResponse(u)).build();
    }
}
