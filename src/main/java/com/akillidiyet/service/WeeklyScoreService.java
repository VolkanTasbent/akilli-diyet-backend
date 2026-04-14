package com.akillidiyet.service;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.domain.DietGoal;
import com.akillidiyet.service.dto.DailyTargetsDto;
import com.akillidiyet.service.dto.TrendDayDto;
import com.akillidiyet.service.dto.TrendRangeDto;
import com.akillidiyet.service.dto.WeeklyScoreDto;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WeeklyScoreService {

    private static final int WINDOW_DAYS = 7;

    private final TrendService trendService;
    private final NutritionService nutritionService;

    @Transactional(readOnly = true)
    public WeeklyScoreDto compute(AppUser user, LocalDate weekEndInclusive) {
        LocalDate start = weekEndInclusive.minusDays(WINDOW_DAYS - 1);
        TrendRangeDto trend = trendService.trends(user, start, weekEndInclusive);
        DailyTargetsDto targets = nutritionService.computeTargets(user);
        int waterGoal = user.getDailyWaterGoalMl() != null ? user.getDailyWaterGoalMl() : 2000;

        double total = 0;
        for (TrendDayDto d : trend.days()) {
            total += dayScore(user.getDietGoal(), d, targets, waterGoal);
        }
        int score = trend.days().isEmpty()
                ? 0
                : (int) Math.round(Math.min(100, Math.max(0, total / trend.days().size())));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String label = start.format(fmt) + " – " + weekEndInclusive.format(fmt);

        List<String> hints = new ArrayList<>();
        if (score >= 80) {
            hints.add("Bu hafta genel olarak çok iyi gidiyorsun.");
        } else if (score >= 60) {
            hints.add("Fena değil; su veya protein tarafını biraz sıkılaştırabilirsin.");
        } else {
            hints.add("Kayıt ve hedeflere yaklaşmak için günlük mini görevlere odaklan.");
        }

        return WeeklyScoreDto.builder().score(score).periodLabelTr(label).hintsTr(hints).build();
    }

    private static double dayScore(DietGoal goal, TrendDayDto d, DailyTargetsDto targets, int waterGoal) {
        boolean empty =
                d.calories() == 0
                        && d.exerciseCalories() == 0
                        && d.waterMl() == 0
                        && d.weightKg() == null
                        && (d.sleepHours() == null || d.sleepHours() <= 0);
        if (empty) {
            return 32;
        }
        int net = d.calories() - d.exerciseCalories();
        double wScore =
                waterGoal <= 0 ? 72 : Math.min(100, (d.waterMl() / Math.max(1, waterGoal * 0.8)) * 100);
        double pScore =
                targets.targetProteinG() <= 0
                        ? 72
                        : Math.min(100, (d.proteinG() / Math.max(0.1, targets.targetProteinG())) * 100);
        double cScore = calorieScore(goal, net, targets.targetCalories());
        return (wScore + pScore + cScore) / 3.0;
    }

    private static double calorieScore(DietGoal goal, int net, int target) {
        if (target <= 0) {
            return 75;
        }
        if (goal == null) {
            return net <= target * 1.08 ? 90 : 55;
        }
        return switch (goal) {
            case LOSE_WEIGHT -> net <= target ? 92 : (net <= target * 1.12 ? 65 : 45);
            case GAIN_MUSCLE -> net >= target * 0.9 ? 90 : (net >= target * 0.78 ? 65 : 45);
            case MAINTAIN -> (net >= target * 0.9 && net <= target * 1.1) ? 92 : 58;
        };
    }
}
