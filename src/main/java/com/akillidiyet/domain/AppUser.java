package com.akillidiyet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "app_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, name = "password_hash")
    private String passwordHash;

    @Column(nullable = false, name = "display_name")
    private String displayName;

    private Double heightCm;
    private Double weightKg;
    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level")
    private ActivityLevel activityLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "diet_goal")
    private DietGoal dietGoal;

    @Column(name = "target_weight_kg")
    private Double targetWeightKg;

    /** Hafta cinsinden hedef süre (ör. 8 hafta) */
    @Column(name = "goal_duration_weeks")
    private Integer goalDurationWeeks;

    private String city;

    @Column(name = "student_mode")
    private Boolean studentMode;

    @Column(name = "daily_water_goal_ml")
    private Integer dailyWaterGoalMl;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    /** Kayıtlı kullanıcıya zamanlanmış e-posta hatırlatmaları (SMTP gerekir). */
    @Column(name = "reminder_email_enabled")
    private Boolean reminderEmailEnabled;

    @Column(name = "reminder_email_water")
    private Boolean reminderEmailWater;

    @Column(name = "reminder_email_breakfast")
    private Boolean reminderEmailBreakfast;

    @Column(name = "reminder_email_lunch")
    private Boolean reminderEmailLunch;

    @Column(name = "reminder_email_dinner")
    private Boolean reminderEmailDinner;

    @Column(name = "last_reminder_email_water_at")
    private Instant lastReminderEmailWaterAt;

    @Column(name = "last_reminder_email_breakfast_date")
    private LocalDate lastReminderEmailBreakfastDate;

    @Column(name = "last_reminder_email_lunch_date")
    private LocalDate lastReminderEmailLunchDate;

    @Column(name = "last_reminder_email_dinner_date")
    private LocalDate lastReminderEmailDinnerDate;
}
