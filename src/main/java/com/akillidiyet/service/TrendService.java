package com.akillidiyet.service;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.domain.FoodLogEntry;
import com.akillidiyet.domain.WaterLog;
import com.akillidiyet.domain.WeightLog;
import com.akillidiyet.domain.ExerciseLog;
import com.akillidiyet.domain.SleepLog;
import com.akillidiyet.repo.ExerciseLogRepository;
import com.akillidiyet.repo.FoodLogEntryRepository;
import com.akillidiyet.repo.SleepLogRepository;
import com.akillidiyet.repo.WaterLogRepository;
import com.akillidiyet.repo.WeightLogRepository;
import com.akillidiyet.service.dto.DailyTargetsDto;
import com.akillidiyet.service.dto.TrendDayDto;
import com.akillidiyet.service.dto.TrendRangeDto;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TrendService {

    private static final int MAX_RANGE_DAYS = 120;

    private final FoodLogEntryRepository foodLogRepository;
    private final ExerciseLogRepository exerciseLogRepository;
    private final WaterLogRepository waterLogRepository;
    private final WeightLogRepository weightLogRepository;
    private final SleepLogRepository sleepLogRepository;
    private final NutritionService nutritionService;

    @Transactional(readOnly = true)
    public TrendRangeDto trends(AppUser user, LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "from, to'dan sonra olamaz");
        }
        long daysBetween = ChronoUnit.DAYS.between(from, to) + 1;
        if (daysBetween > MAX_RANGE_DAYS) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "En fazla " + MAX_RANGE_DAYS + " günlük aralık seçilebilir");
        }

        DailyTargetsDto targets = nutritionService.computeTargets(user);

        Map<LocalDate, double[]> foodAgg = new HashMap<>();
        List<FoodLogEntry> foods =
                foodLogRepository.findByUserAndLogDateBetweenOrderByLogDateAscIdAsc(user, from, to);
        for (FoodLogEntry e : foods) {
            double[] a = foodAgg.computeIfAbsent(e.getLogDate(), d -> new double[4]);
            a[0] += NutritionService.caloriesForGrams(e.getFood(), e.getGrams());
            a[1] += NutritionService.macroForGrams(e.getFood().getProteinPer100g(), e.getGrams());
            a[2] += NutritionService.macroForGrams(e.getFood().getCarbsPer100g(), e.getGrams());
            a[3] += NutritionService.macroForGrams(e.getFood().getFatPer100g(), e.getGrams());
        }

        Map<LocalDate, Integer> waterByDay = new HashMap<>();
        for (WaterLog w : waterLogRepository.findByUserAndLogDateBetweenOrderByLogDateAsc(user, from, to)) {
            waterByDay.put(w.getLogDate(), w.getTotalMl());
        }

        Map<LocalDate, Double> weightByDay = new HashMap<>();
        for (WeightLog w :
                weightLogRepository.findByUserAndLogDateBetweenOrderByLogDateAscIdAsc(user, from, to)) {
            weightByDay.put(w.getLogDate(), w.getWeightKg());
        }

        Map<LocalDate, Integer> exerciseByDay = new HashMap<>();
        for (ExerciseLog ex :
                exerciseLogRepository.findByUserAndLogDateBetweenOrderByLogDateAscIdAsc(user, from, to)) {
            exerciseByDay.merge(ex.getLogDate(), ex.getCaloriesBurned(), Integer::sum);
        }

        Map<LocalDate, Double> sleepByDay = new HashMap<>();
        for (SleepLog s : sleepLogRepository.findByUserAndLogDateBetweenOrderByLogDateAsc(user, from, to)) {
            sleepByDay.put(s.getLogDate(), s.getHoursSlept());
        }

        List<TrendDayDto> days = new ArrayList<>();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            double[] fa = foodAgg.getOrDefault(d, new double[4]);
            int ex = exerciseByDay.getOrDefault(d, 0);
            days.add(
                    TrendDayDto.builder()
                            .date(d)
                            .calories((int) Math.round(fa[0]))
                            .exerciseCalories(ex)
                            .proteinG(round1(fa[1]))
                            .carbsG(round1(fa[2]))
                            .fatG(round1(fa[3]))
                            .waterMl(waterByDay.getOrDefault(d, 0))
                            .weightKg(weightByDay.get(d))
                            .sleepHours(sleepByDay.get(d))
                            .build());
        }

        return TrendRangeDto.builder()
                .from(from)
                .to(to)
                .targetCalories(targets.targetCalories())
                .targetProteinG(targets.targetProteinG())
                .days(days)
                .build();
    }

    private static double round1(double v) {
        return Math.round(v * 10) / 10.0;
    }
}
