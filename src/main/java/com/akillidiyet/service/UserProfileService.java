package com.akillidiyet.service;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.repo.AppUserRepository;
import com.akillidiyet.web.UserMapper;
import com.akillidiyet.web.dto.UpdateProfileRequest;
import com.akillidiyet.web.dto.UserResponse;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final AppUserRepository userRepository;

    @Transactional
    public UserResponse updateProfile(AppUser user, UpdateProfileRequest req) {
        if (req.displayName() != null && !req.displayName().isBlank()) {
            user.setDisplayName(req.displayName().trim());
        }
        if (req.heightCm() != null) {
            user.setHeightCm(req.heightCm());
        }
        if (req.weightKg() != null) {
            user.setWeightKg(req.weightKg());
        }
        if (req.age() != null) {
            user.setAge(req.age());
        }
        if (req.gender() != null) {
            user.setGender(req.gender());
        }
        if (req.activityLevel() != null) {
            user.setActivityLevel(req.activityLevel());
        }
        if (req.dietGoal() != null) {
            user.setDietGoal(req.dietGoal());
        }
        if (req.targetWeightKg() != null) {
            user.setTargetWeightKg(req.targetWeightKg());
        }
        if (req.goalDurationWeeks() != null) {
            user.setGoalDurationWeeks(req.goalDurationWeeks());
        }
        if (req.city() != null) {
            user.setCity(req.city().isBlank() ? null : req.city().trim());
        }
        if (req.studentMode() != null) {
            user.setStudentMode(req.studentMode());
        }
        if (req.dailyWaterGoalMl() != null) {
            user.setDailyWaterGoalMl(req.dailyWaterGoalMl());
        }
        if (req.reminderEmailEnabled() != null) {
            user.setReminderEmailEnabled(req.reminderEmailEnabled());
        }
        if (req.reminderEmailWater() != null) {
            user.setReminderEmailWater(req.reminderEmailWater());
        }
        if (req.reminderEmailBreakfast() != null) {
            user.setReminderEmailBreakfast(req.reminderEmailBreakfast());
        }
        if (req.reminderEmailLunch() != null) {
            user.setReminderEmailLunch(req.reminderEmailLunch());
        }
        if (req.reminderEmailDinner() != null) {
            user.setReminderEmailDinner(req.reminderEmailDinner());
        }
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        return UserMapper.toResponse(user);
    }
}
