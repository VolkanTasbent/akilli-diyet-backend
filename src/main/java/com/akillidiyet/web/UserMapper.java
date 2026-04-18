package com.akillidiyet.web;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.web.dto.UserResponse;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

@UtilityClass
public class UserMapper {

    public static UserResponse toResponse(AppUser u) {
        boolean hasAvatar = StringUtils.hasText(u.getAvatarMediaType());
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
                .reminderEmailEnabled(Boolean.TRUE.equals(u.getReminderEmailEnabled()))
                .reminderEmailWater(Boolean.TRUE.equals(u.getReminderEmailWater()))
                .reminderEmailBreakfast(Boolean.TRUE.equals(u.getReminderEmailBreakfast()))
                .reminderEmailLunch(Boolean.TRUE.equals(u.getReminderEmailLunch()))
                .reminderEmailDinner(Boolean.TRUE.equals(u.getReminderEmailDinner()))
                .hasAvatar(hasAvatar)
                .avatarUpdatedAt(u.getAvatarUpdatedAt())
                .build();
    }
}
