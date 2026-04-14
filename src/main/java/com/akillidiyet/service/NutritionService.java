package com.akillidiyet.service;

import com.akillidiyet.domain.ActivityLevel;
import com.akillidiyet.domain.AppUser;
import com.akillidiyet.domain.DietGoal;
import com.akillidiyet.domain.Gender;
import com.akillidiyet.service.dto.DailyTargetsDto;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class NutritionService {

    private static final double KCAL_PER_KG_FAT = 7700;

    public DailyTargetsDto computeTargets(AppUser user) {
        Double w = user.getWeightKg();
        Double h = user.getHeightCm();
        Integer age = user.getAge();
        Gender g = user.getGender();
        ActivityLevel level = user.getActivityLevel();
        DietGoal goal = user.getDietGoal();

        if (w == null || h == null || age == null || g == null || level == null || goal == null) {
            return DailyTargetsDto.builder()
                    .bmr(0)
                    .tdee(0)
                    .targetCalories(2000)
                    .targetProteinG(120)
                    .targetCarbsG(200)
                    .targetFatG(65)
                    .suggestedDailyDeficit(null)
                    .explanationTr(
                            "Profil eksik. Boy, kilo, yaş, cinsiyet, aktivite ve hedef girildiğinde kişisel hedefler hesaplanır.")
                    .build();
        }

        double bmr = mifflinStJeor(w, h, age, g);
        double tdee = bmr * level.getMultiplier();

        int adjustment = 0;
        List<String> parts = new ArrayList<>();
        parts.add("BMR: " + Math.round(bmr) + " kcal, TDEE: " + Math.round(tdee) + " kcal.");

        if (goal == DietGoal.MAINTAIN) {
            adjustment = 0;
            parts.add("Hedef: kilo koruma — günlük kalori TDEE ile aynı.");
        } else if (goal == DietGoal.GAIN_MUSCLE) {
            adjustment = 350;
            parts.add("Hedef: kas artışı — günlük +350 kcal fazlalık önerisi.");
        } else {
            adjustment = computeWeightLossAdjustment(user, tdee, parts);
        }

        int targetCalories = (int) Math.round(tdee + adjustment);
        targetCalories = Math.max(1200, targetCalories);

        double proteinG = Math.round(w * (goal == DietGoal.GAIN_MUSCLE ? 2.0 : 1.8) * 10) / 10.0;
        int proteinKcal = (int) Math.round(proteinG * 4);
        int remaining = Math.max(targetCalories - proteinKcal, 0);
        double fatG = Math.round((remaining * 0.30) / 9.0 * 10) / 10.0;
        int fatKcal = (int) Math.round(fatG * 9);
        double carbsG = Math.round((targetCalories - proteinKcal - fatKcal) / 4.0 * 10) / 10.0;

        Integer deficit = goal == DietGoal.LOSE_WEIGHT ? (int) Math.round(tdee - targetCalories) : null;

        return DailyTargetsDto.builder()
                .bmr(round1(bmr))
                .tdee(round1(tdee))
                .targetCalories(targetCalories)
                .targetProteinG(proteinG)
                .targetCarbsG(carbsG)
                .targetFatG(fatG)
                .suggestedDailyDeficit(deficit != null && deficit > 0 ? deficit : null)
                .explanationTr(String.join(" ", parts))
                .build();
    }

    private int computeWeightLossAdjustment(AppUser user, double tdee, List<String> parts) {
        Double current = user.getWeightKg();
        Double targetW = user.getTargetWeightKg();
        Integer weeks = user.getGoalDurationWeeks();

        if (current != null && targetW != null && weeks != null && weeks > 0 && current > targetW) {
            double deltaKg = current - targetW;
            double dailyDeficit = (deltaKg * KCAL_PER_KG_FAT) / (weeks * 7.0);
            dailyDeficit = Math.min(1000, Math.max(200, dailyDeficit));
            int targetIntake = (int) Math.round(tdee - dailyDeficit);
            parts.add(
                    "Hedef: "
                            + String.format("%.1f", deltaKg)
                            + " kg verme, "
                            + weeks
                            + " hafta — günlük ~"
                            + Math.round(dailyDeficit)
                            + " kcal açık (üst sınır 1000).");
            return targetIntake - (int) Math.round(tdee);
        }

        parts.add("Kilo verme — varsayılan günlük ~500 kcal açık.");
        return -500;
    }

    private static double mifflinStJeor(double weightKg, double heightCm, int age, Gender gender) {
        double s = gender == Gender.MALE ? 5 : -161;
        return 10 * weightKg + 6.25 * heightCm - 5 * age + s;
    }

    private static double round1(double v) {
        return Math.round(v * 10) / 10.0;
    }

    public static int caloriesForGrams(com.akillidiyet.domain.Food food, double grams) {
        return (int) Math.round(food.getCaloriesPer100g() * grams / 100.0);
    }

    public static double macroForGrams(double per100g, double grams) {
        return Math.round(per100g * grams / 100.0 * 10) / 10.0;
    }
}
