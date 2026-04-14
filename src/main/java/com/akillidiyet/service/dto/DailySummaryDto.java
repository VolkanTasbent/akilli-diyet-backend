package com.akillidiyet.service.dto;

import com.akillidiyet.domain.MealType;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record DailySummaryDto(
        LocalDate date,
        int consumedCalories,
        /** Toplam yakılan kcal (tüm egzersiz kayıtları) */
        int exerciseCaloriesBurned,
        /** consumedCalories − exerciseCaloriesBurned */
        int netEnergyCalories,
        double proteinG,
        double carbsG,
        double fatG,
        DailyTargetsDto targets,
        int waterMl,
        int waterGoalMl,
        /** O güne kayıtlı uyku süresi (saat); yoksa null */
        Double sleepHours,
        /** Hedef kalori − net enerji (egzersiz bütçeyi genişletir) */
        int caloriesRemaining,
        Map<MealType, MealMacroDto> byMeal,
        /** Dünden geriye ardışık “en az bir kayıt” günü sayısı */
        int logStreakDays,
        List<DailyTaskDto> dailyTasks,
        List<String> coachMessagesTr,
        List<String> suggestionsTr) {}
