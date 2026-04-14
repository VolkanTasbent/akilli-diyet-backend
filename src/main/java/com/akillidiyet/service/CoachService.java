package com.akillidiyet.service;

import com.akillidiyet.service.dto.DailyTargetsDto;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CoachService {

    public List<String> buildCoachMessages(
            int consumed,
            int exerciseBurned,
            int target,
            double protein,
            double proteinTarget,
            int waterMl,
            int waterGoal,
            Double sleepHours) {
        List<String> m = new ArrayList<>();
        if (sleepHours != null && sleepHours > 0 && sleepHours < 6) {
            m.add("Az uyku açlığı artırabilir; bugün proteine ve suya ekstra özen göster.");
        }
        if (target <= 0) {
            return m;
        }
        if (exerciseBurned > 0) {
            m.add("Bugün " + exerciseBurned + " kcal egzersiz kaydı var; günlük bütçene bu kadar eklenebilir.");
        }
        int net = consumed - exerciseBurned;
        double ratio = net / (double) target;
        if (ratio < 0.5) {
            m.add("Günün ilk yarısındasın; öğünleri dengelemeye devam et.");
        } else if (ratio > 0.95 && ratio <= 1.05) {
            m.add("Hedef kaloriye (net) çok yakınsın; atıştırmalıklara dikkat et.");
        } else if (ratio > 1.1) {
            m.add("Net kalori hedefini aştın; yarın hafif bir gün planlayabilirsin.");
        } else {
            m.add("Bugün kalori hedefinle (yemek − egzersiz) uyumlu gidiyorsun.");
        }

        if (proteinTarget > 0 && protein < proteinTarget * 0.7) {
            m.add("Protein hedefinin altındasın — tavuk, yoğurt veya yumurta eklemeyi düşün.");
        }

        if (waterGoal > 0 && waterMl < waterGoal * 0.5) {
            m.add("Su içimi düşük; bir bardak daha su iyi gelir.");
        }
        return m;
    }

    public List<String> buildMacroSuggestions(DailyTargetsDto targets, double proteinConsumed) {
        List<String> s = new ArrayList<>();
        if (targets == null) {
            return s;
        }
        if (proteinConsumed < targets.targetProteinG() * 0.8) {
            s.add("Protein az → tavuk göğsü, ton balığı veya yumurta önerilir.");
        }
        return s;
    }
}
