package com.akillidiyet.service;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.domain.DietGoal;
import com.akillidiyet.domain.ExerciseLog;
import com.akillidiyet.domain.FoodLogEntry;
import com.akillidiyet.domain.MealType;
import com.akillidiyet.domain.WaterLog;
import com.akillidiyet.repo.ExerciseLogRepository;
import com.akillidiyet.repo.FoodLogEntryRepository;
import com.akillidiyet.repo.SleepLogRepository;
import com.akillidiyet.repo.WaterLogRepository;
import com.akillidiyet.service.dto.DailySummaryDto;
import com.akillidiyet.service.dto.DailyTargetsDto;
import com.akillidiyet.service.dto.DailyTaskDto;
import com.akillidiyet.service.dto.MealMacroDto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DailySummaryService {

    private final FoodLogEntryRepository foodLogRepository;
    private final ExerciseLogRepository exerciseLogRepository;
    private final WaterLogRepository waterLogRepository;
    private final SleepLogRepository sleepLogRepository;
    private final NutritionService nutritionService;
    private final CoachService coachService;
    private final StreakService streakService;

    @Transactional(readOnly = true)
    public DailySummaryDto summarize(AppUser user, LocalDate date) {
        List<FoodLogEntry> entries = foodLogRepository.findByUserAndLogDateOrderByIdAsc(user, date);
        DailyTargetsDto targets = nutritionService.computeTargets(user);

        int kcal = 0;
        double p = 0, c = 0, f = 0;
        Map<MealType, double[]> mealAgg = new EnumMap<>(MealType.class);
        for (MealType mt : MealType.values()) {
            mealAgg.put(mt, new double[4]);
        }

        for (FoodLogEntry e : entries) {
            int ek = NutritionService.caloriesForGrams(e.getFood(), e.getGrams());
            double ep = NutritionService.macroForGrams(e.getFood().getProteinPer100g(), e.getGrams());
            double ec = NutritionService.macroForGrams(e.getFood().getCarbsPer100g(), e.getGrams());
            double ef = NutritionService.macroForGrams(e.getFood().getFatPer100g(), e.getGrams());
            kcal += ek;
            p += ep;
            c += ec;
            f += ef;
            double[] arr = mealAgg.get(e.getMealType());
            arr[0] += ek;
            arr[1] += ep;
            arr[2] += ec;
            arr[3] += ef;
        }

        Map<MealType, MealMacroDto> byMeal = new EnumMap<>(MealType.class);
        for (Map.Entry<MealType, double[]> en : mealAgg.entrySet()) {
            double[] a = en.getValue();
            byMeal.put(
                    en.getKey(),
                    MealMacroDto.builder()
                            .calories((int) Math.round(a[0]))
                            .proteinG(round1(a[1]))
                            .carbsG(round1(a[2]))
                            .fatG(round1(a[3]))
                            .build());
        }

        int waterGoal = user.getDailyWaterGoalMl() != null ? user.getDailyWaterGoalMl() : 2000;
        int waterMl =
                waterLogRepository.findByUserAndLogDate(user, date).map(WaterLog::getTotalMl).orElse(0);

        int exerciseBurned =
                exerciseLogRepository.findByUserAndLogDateOrderByIdAsc(user, date).stream()
                        .mapToInt(ExerciseLog::getCaloriesBurned)
                        .sum();
        int netEnergy = kcal - exerciseBurned;
        int remaining = targets.targetCalories() - netEnergy;

        int streak = streakService.activityStreakEndingYesterday(user, date);

        Double sleepHours =
                sleepLogRepository.findByUserAndLogDate(user, date).map(s -> s.getHoursSlept()).orElse(null);

        List<DailyTaskDto> dailyTasks =
                buildDailyTasks(user.getDietGoal(), waterMl, waterGoal, p, targets, netEnergy, sleepHours);

        List<String> coach =
                coachService.buildCoachMessages(
                        kcal,
                        exerciseBurned,
                        targets.targetCalories(),
                        p,
                        targets.targetProteinG(),
                        waterMl,
                        waterGoal,
                        sleepHours);
        List<String> sug = coachService.buildMacroSuggestions(targets, p);

        return DailySummaryDto.builder()
                .date(date)
                .consumedCalories(kcal)
                .exerciseCaloriesBurned(exerciseBurned)
                .netEnergyCalories(netEnergy)
                .proteinG(round1(p))
                .carbsG(round1(c))
                .fatG(round1(f))
                .targets(targets)
                .waterMl(waterMl)
                .waterGoalMl(waterGoal)
                .sleepHours(sleepHours)
                .caloriesRemaining(remaining)
                .byMeal(byMeal)
                .logStreakDays(streak)
                .dailyTasks(dailyTasks)
                .coachMessagesTr(coach)
                .suggestionsTr(sug)
                .build();
    }

    private static List<DailyTaskDto> buildDailyTasks(
            DietGoal goal,
            int waterMl,
            int waterGoal,
            double proteinG,
            DailyTargetsDto targets,
            int netEnergy,
            Double sleepHours) {
        List<DailyTaskDto> tasks = new ArrayList<>();
        boolean waterOk = waterGoal <= 0 || waterMl >= waterGoal * 0.8;
        tasks.add(
                DailyTaskDto.builder()
                        .id("water")
                        .labelTr("Su hedefinin en az %80'ine ulaş")
                        .done(waterOk)
                        .build());
        double pt = targets.targetProteinG();
        boolean proteinOk = pt <= 0 || proteinG >= pt * 0.85;
        tasks.add(
                DailyTaskDto.builder()
                        .id("protein")
                        .labelTr("Protein hedefinin en az %85'ini tamamla")
                        .done(proteinOk)
                        .build());
        int target = targets.targetCalories();
        boolean calOk = target <= 0 || caloriesTaskAligned(goal, netEnergy, target);
        tasks.add(
                DailyTaskDto.builder()
                        .id("calories")
                        .labelTr("Net kalori ile günlük hedefini tutarlı tut")
                        .done(calOk)
                        .build());
        boolean sleepOk = sleepHours != null && sleepHours >= 6;
        tasks.add(
                DailyTaskDto.builder()
                        .id("sleep")
                        .labelTr("Uyku kaydı: en az 6 saat")
                        .done(sleepOk)
                        .build());
        return tasks;
    }

    private static boolean caloriesTaskAligned(DietGoal goal, int net, int target) {
        if (target <= 0) {
            return true;
        }
        if (goal == null) {
            return net <= target * 1.05;
        }
        return switch (goal) {
            case LOSE_WEIGHT -> net <= target;
            case GAIN_MUSCLE -> net >= target * 0.92;
            case MAINTAIN -> net >= target * 0.92 && net <= target * 1.08;
        };
    }

    private static double round1(double v) {
        return Math.round(v * 10) / 10.0;
    }
}
