package com.akillidiyet.service;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.repo.AppUserRepository;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderEmailService {

    private static final ZoneId ZONE = ZoneId.of("Europe/Istanbul");

    private final AppUserRepository userRepository;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.mail.from:}")
    private String mailFrom;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${app.reminder.email.water-interval-hours:2}")
    private int waterIntervalHours;

    @Value("${app.reminder.email.breakfast-time:08:30}")
    private String breakfastTimeStr;

    @Value("${app.reminder.email.lunch-time:13:00}")
    private String lunchTimeStr;

    @Value("${app.reminder.email.dinner-time:19:30}")
    private String dinnerTimeStr;

    private LocalTime breakfastTime;
    private LocalTime lunchTime;
    private LocalTime dinnerTime;

    @PostConstruct
    void parseTimes() {
        breakfastTime = LocalTime.parse(breakfastTimeStr.trim());
        lunchTime = LocalTime.parse(lunchTimeStr.trim());
        dinnerTime = LocalTime.parse(dinnerTimeStr.trim());
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void sendDueReminders() {
        if (mailSender == null) {
            return;
        }
        String from = fromAddress();
        if (from.isBlank()) {
            return;
        }

        ZonedDateTime nowZ = ZonedDateTime.now(ZONE);
        Instant nowInstant = nowZ.toInstant();
        LocalDate today = nowZ.toLocalDate();
        LocalTime nowTime = nowZ.toLocalTime();

        List<AppUser> users = userRepository.findByReminderEmailEnabledTrue();
        for (AppUser u : users) {
            try {
                if (Boolean.TRUE.equals(u.getReminderEmailWater())) {
                    maybeWater(u, nowInstant, from);
                }
                if (Boolean.TRUE.equals(u.getReminderEmailBreakfast())) {
                    maybeMeal(
                            u,
                            today,
                            nowTime,
                            breakfastTime,
                            u.getLastReminderEmailBreakfastDate(),
                            d -> u.setLastReminderEmailBreakfastDate(d),
                            "Kahvaltı hatırlatması",
                            "Merhaba,\n\nKahvaltı kaydını eklemeyi unutma.\n\n— Akıllı Diyet",
                            from);
                }
                if (Boolean.TRUE.equals(u.getReminderEmailLunch())) {
                    maybeMeal(
                            u,
                            today,
                            nowTime,
                            lunchTime,
                            u.getLastReminderEmailLunchDate(),
                            d -> u.setLastReminderEmailLunchDate(d),
                            "Öğle yemeği hatırlatması",
                            "Merhaba,\n\nÖğle öğününü kaydetmeyi unutma.\n\n— Akıllı Diyet",
                            from);
                }
                if (Boolean.TRUE.equals(u.getReminderEmailDinner())) {
                    maybeMeal(
                            u,
                            today,
                            nowTime,
                            dinnerTime,
                            u.getLastReminderEmailDinnerDate(),
                            d -> u.setLastReminderEmailDinnerDate(d),
                            "Akşam yemeği hatırlatması",
                            "Merhaba,\n\nAkşam öğününü kaydetmeyi unutma.\n\n— Akıllı Diyet",
                            from);
                }
            } catch (Exception e) {
                log.warn("Hatırlatma e-postası gönderilemedi, kullanıcı id={}", u.getId(), e);
            }
        }
    }

    private void maybeWater(AppUser u, Instant now, String from) {
        Instant last = u.getLastReminderEmailWaterAt();
        long minHours = Math.max(1, waterIntervalHours);
        if (last != null && Duration.between(last, now).toHours() < minHours) {
            return;
        }
        send(u.getEmail(), from, "Su içme hatırlatması", "Merhaba,\n\nSu hedefine yaklaşmak için bir bardak su içmeyi düşün.\n\n— Akıllı Diyet");
        u.setLastReminderEmailWaterAt(now);
    }

    private void maybeMeal(
            AppUser u,
            LocalDate today,
            LocalTime now,
            LocalTime scheduled,
            LocalDate lastSent,
            java.util.function.Consumer<LocalDate> setLast,
            String subject,
            String body,
            String from) {
        if (lastSent != null && lastSent.equals(today)) {
            return;
        }
        if (now.getHour() != scheduled.getHour() || now.getMinute() != scheduled.getMinute()) {
            return;
        }
        send(u.getEmail(), from, subject, body);
        setLast.accept(today);
    }

    private void send(String to, String from, String subject, String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        mailSender.send(msg);
    }

    private String fromAddress() {
        if (mailFrom != null && !mailFrom.isBlank()) {
            return mailFrom.trim();
        }
        return mailUsername == null ? "" : mailUsername.trim();
    }
}
