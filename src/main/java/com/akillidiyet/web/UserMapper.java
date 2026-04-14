package com.akillidiyet.web;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.web.dto.UserResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public static UserResponse toResponse(AppUser u) {
        return UserResponse.builder()
                .id(u.getId())
                .email(u.getEmail())
                .displayName(u.getDisplayName())
                .heightCm(u.getHeightCm())
                .weightKg(u.getWeightKg())
                .age(u.getAge())
                .gender(u.getGender())
                .activityLevel(u.getActivityLevel())
                .dietGoal(u.getDietGoal())
                .targetWeightKg(u.getTargetWeightKg())
                .goalDurationWeeks(u.getGoalDurationWeeks())
                .city(u.getCity())
                .studentMode(u.getStudentMode())
                .dailyWaterGoalMl(u.getDailyWaterGoalMl())
                .build();
    }
}
