package com.akillidiyet.service;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.domain.PasswordResetToken;
import com.akillidiyet.repo.AppUserRepository;
import com.akillidiyet.repo.PasswordResetTokenRepository;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private static final int TOKEN_BYTES = 32;

    private final AppUserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.frontend.base-url:http://localhost:5173}")
    private String frontendBaseUrl;

    @Value("${app.mail.from:}")
    private String mailFrom;

    /** Her zaman aynı mesaj; e-posta varlığı sızdırılmaz. */
    @Transactional
    public void requestReset(String emailRaw) {
        String email = emailRaw == null ? "" : emailRaw.trim().toLowerCase();
        if (email.isEmpty()) {
            return;
        }
        userRepository
                .findByEmailIgnoreCase(email)
                .ifPresent(user -> {
                            tokenRepository.deleteByUser(user);
                            String token = newRandomToken();
                            Instant exp = Instant.now().plus(1, ChronoUnit.HOURS);
                            PasswordResetToken entity =
                                    PasswordResetToken.builder()
                                            .user(user)
                                            .token(token)
                                            .expiresAt(exp)
                                            .build();
                            tokenRepository.save(entity);
                            String base = frontendBaseUrl.replaceAll("/$", "");
                            String link =
                                    base
                                            + "/reset-password?token="
                                            + URLEncoder.encode(token, StandardCharsets.UTF_8);
                            sendOrLog(user.getEmail(), link);
                        });
    }

    @Transactional
    public void resetWithToken(String tokenRaw, String newPassword) {
        if (tokenRaw == null || tokenRaw.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz bağlantı");
        }
        String token = tokenRaw.trim();
        PasswordResetToken t =
                tokenRepository
                        .findByToken(token)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST, "Geçersiz veya süresi dolmuş bağlantı"));
        if (t.getConsumedAt() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu bağlantı zaten kullanıldı");
        }
        if (t.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bağlantının süresi doldu");
        }
        AppUser u = t.getUser();
        u.setPasswordHash(passwordEncoder.encode(newPassword));
        u.setUpdatedAt(Instant.now());
        t.setConsumedAt(Instant.now());
    }

    private void sendOrLog(String to, String link) {
        if (mailSender != null && mailFrom != null && !mailFrom.isBlank()) {
            try {
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setFrom(mailFrom);
                msg.setTo(to);
                msg.setSubject("Akıllı Diyet — şifre sıfırlama");
                msg.setText(
                        "Şifrenizi sıfırlamak için aşağıdaki bağlantıyı kullanın (1 saat geçerlidir):\n\n"
                                + link
                                + "\n\nBağlantıyı siz istemediyseniz bu e-postayı yok sayın.");
                mailSender.send(msg);
            } catch (Exception e) {
                log.warn("Şifre sıfırlama e-postası gönderilemedi; link loglanıyor. Alıcı={}", to, e);
                log.info("Şifre sıfırlama linki ({}): {}", to, link);
            }
        } else {
            log.info("Şifre sıfırlama linki (e-posta yapılandırılmadı, alıcı={}): {}", to, link);
        }
    }

    private static String newRandomToken() {
        byte[] buf = new byte[TOKEN_BYTES];
        new SecureRandom().nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }
}
