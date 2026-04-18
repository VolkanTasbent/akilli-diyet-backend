package com.akillidiyet.web;

import com.akillidiyet.service.AuthService;
import com.akillidiyet.service.PasswordResetService;
import com.akillidiyet.web.dto.AuthResponse;
import com.akillidiyet.web.dto.ForgotPasswordRequest;
import com.akillidiyet.web.dto.LoginRequest;
import com.akillidiyet.web.dto.RegisterRequest;
import com.akillidiyet.web.dto.ResetPasswordRequest;
import jakarta.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @Value("${app.frontend.base-url:http://localhost:5173}")
    private String frontendBaseUrl;

    /** Tarayıcı veya yanlış bağlantı GET ile API adresine gelirse 405 yerine arayüze yönlendir. */
    @GetMapping("/register")
    public RedirectView registerGet() {
        return new RedirectView(trimBase(frontendBaseUrl) + "/register");
    }

    @GetMapping("/login")
    public RedirectView loginGet() {
        return new RedirectView(trimBase(frontendBaseUrl) + "/login");
    }

    @GetMapping("/forgot-password")
    public RedirectView forgotPasswordGet() {
        return new RedirectView(trimBase(frontendBaseUrl) + "/forgot-password");
    }

    @GetMapping("/reset-password")
    public RedirectView resetPasswordGet(@RequestParam(required = false) String token) {
        String base = trimBase(frontendBaseUrl) + "/reset-password";
        if (token != null && !token.isBlank()) {
            base += "?token=" + URLEncoder.encode(token.strip(), StandardCharsets.UTF_8);
        }
        return new RedirectView(base);
    }

    private static String trimBase(String url) {
        return url == null ? "" : url.replaceAll("/$", "");
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest body) {
        return authService.register(body);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest body) {
        return authService.login(body);
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void forgotPassword(@Valid @RequestBody ForgotPasswordRequest body) {
        passwordResetService.requestReset(body.email());
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest body) {
        passwordResetService.resetWithToken(body.token(), body.newPassword());
    }
}
