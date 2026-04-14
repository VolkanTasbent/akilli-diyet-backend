package com.akillidiyet.service;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.repo.ExerciseLogRepository;
import com.akillidiyet.repo.FoodLogEntryRepository;
import com.akillidiyet.repo.SleepLogRepository;
import com.akillidiyet.repo.WaterLogRepository;
import com.akillidiyet.repo.WeightLogRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StreakService {

    private static final int MAX_STREAK_SCAN = 400;

    private final FoodLogEntryRepository foodLogRepository;
    private final ExerciseLogRepository exerciseLogRepository;
    private final WaterLogRepository waterLogRepository;
    private final WeightLogRepository weightLogRepository;
    private final SleepLogRepository sleepLogRepository;

    /**
     * Dün ve öncesinde, ardışık günlerde en az bir kayıt (yemek / su / egzersiz / kilo / uyku) var mı?
     * Bugün sayılmaz; motivasyon için “kaç gündür takiptesin” sayacı.
     */
    @Transactional(readOnly = true)
    public int activityStreakEndingYesterday(AppUser user, LocalDate today) {
        int streak = 0;
        LocalDate d = today.minusDays(1);
        for (int i = 0; i < MAX_STREAK_SCAN; i++) {
            if (!dayHasActivity(user, d)) {
                break;
            }
            streak++;
            d = d.minusDays(1);
        }
        return streak;
    }

    private boolean dayHasActivity(AppUser user, LocalDate d) {
        if (foodLogRepository.existsByUserAndLogDate(user, d)) {
            return true;
        }
        if (exerciseLogRepository.existsByUserAndLogDate(user, d)) {
            return true;
        }
        if (weightLogRepository.existsByUserAndLogDate(user, d)) {
            return true;
        }
        if (sleepLogRepository.existsByUserAndLogDate(user, d)) {
            return true;
        }
        return waterLogRepository
                .findByUserAndLogDate(user, d)
                .map(w -> w.getTotalMl() > 0)
                .orElse(false);
    }
}
