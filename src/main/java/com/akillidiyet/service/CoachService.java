package com.akillidiyet.service;

import com.akillidiyet.domain.DietGoal;
import com.akillidiyet.domain.MealType;
import com.akillidiyet.service.dto.CoachMessageInput;
import com.akillidiyet.service.dto.DailyTargetsDto;
import com.akillidiyet.service.dto.MealMacroDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CoachService {

    private static final int MAX_MESSAGES = 8;

    public List<String> buildCoachMessages(CoachMessageInput in) {
        List<String> m = new ArrayList<>();

        if (in.sleepHours() != null && in.sleepHours() > 0 && in.sleepHours() < 6) {
            m.add("Dünkü uyku "
                    + String.format("%.1f", in.sleepHours())
                    + " saat — kısa uyku açlık ve tuzlu atıştırmalık isteğini artırabilir; bugün proteine ve suya öncelik ver.");
        }

        int target = in.targetCalories();
        if (target <= 0) {
            return trim(m);
        }

        if (in.exerciseBurned() > 0) {
            m.add("Bugün "
                    + in.exerciseBurned()
                    + " kcal egzersiz kaydı var; net enerji hesabında bu miktar düşülmüş durumda.");
        }

        int net = in.consumedCalories() - in.exerciseBurned();
        int rem = in.caloriesRemaining();
        double ratio = net / (double) target;

        if (rem >= 280) {
            m.add("Tahmini "
                    + rem
                    + " kcal payın kaldı; öğünleri veya atıştırmalıkları bu çerçevede planlayabilirsin.");
        } else if (rem <= -180) {
            m.add("Net alım hedefinin yaklaşık "
                    + Math.abs(rem)
                    + " kcal üzerindesin; akşamı hafifletmek veya hareket eklemek dengeyi iyileştirir.");
        } else if (ratio < 0.45 && in.consumedCalories() > 0) {
            m.add("Şu an net kalori hedefinin altındasın; öğünleri dengelemeye veya küçük bir ek öğüne devam edebilirsin.");
        } else if (ratio > 0.95 && ratio <= 1.06) {
            m.add("Net kalori hedefine çok yakınsın; sıvı alımı ve küçük porsiyonlarla günü kontrollü kapat.");
        } else if (ratio > 1.12) {
            m.add("Net kalori hedefini hafif aştın; yarın veya önümüzdeki öğünlerde hafifletmek sürdürülebilir olur.");
        } else {
            m.add("Net enerji (yemek − egzersiz) hedefinle uyumlu bir aralıktasın; makroları gözden geçirmeye devam et.");
        }

        dietGoalNudge(in.dietGoal(), rem, net, target, m);

        double pt = in.targetProteinG();
        if (pt > 0 && in.proteinG() < pt * 0.72) {
            m.add("Protein hedefinin belirgin şekilde altındasın (%"
                    + Math.round(100 * in.proteinG() / pt)
                    + ") — yoğurt, yumurta, baklagil veya tavuk eklemek işe yarar.");
        } else if (pt > 0 && in.proteinG() >= pt * 0.95 && in.proteinG() <= pt * 1.08) {
            m.add("Protein hedefine oldukça yakınsın; gün sonuna kadar dağılımı koru.");
        }

        double ct = in.targetCarbsG();
        if (ct > 5 && in.consumedCalories() > 250 && in.carbsG() < ct * 0.62) {
            m.add("Karbonhidrat hedefinin altındasın; tam tahıl, meyve veya kök sebzelerle dengeleyebilirsin.");
        }

        double ft = in.targetFatG();
        if (ft > 2 && in.consumedCalories() > 250 && in.fatG() < ft * 0.58) {
            m.add("Yağ alımı hedefe göre düşük; zeytinyağı, kuruyemiş veya avokado eklenebilir.");
        }

        if (in.waterGoalMl() > 0 && in.waterMl() < in.waterGoalMl() * 0.48) {
            m.add("Su tüketimi hedefe göre düşük (%"
                    + Math.round(100.0 * in.waterMl() / in.waterGoalMl())
                    + "); birkaç bardak su hatırlatması iyi gelir.");
        } else if (in.waterGoalMl() > 0 && in.waterMl() >= in.waterGoalMl() * 0.92) {
            m.add("Su hedefine çok yakınsın; gün sonuna kadar tamamlamak sindirimi ve enerjiyi destekler.");
        }

        mealPatternTips(in.byMeal(), in.consumedCalories(), m);

        return trim(m);
    }

    private static void dietGoalNudge(DietGoal goal, int rem, int net, int target, List<String> m) {
        if (goal == null) {
            return;
        }
        switch (goal) {
            case LOSE_WEIGHT -> {
                if (rem < -120) {
                    m.add("Kilo verme hedefinde bugün net enerji artışı var; önümüzdeki öğünlerde porsiyon veya seçimle telafi edilebilir.");
                } else if (rem > 350 && net < target * 0.75) {
                    m.add("Kilo verme hedefinde aşırı düşük kalori günleri bazen ters teper; çok aç kalmadan hedef çerçevesinde kalmaya çalış.");
                }
            }
            case GAIN_MUSCLE -> {
                if (rem > 400) {
                    m.add("Kas hedefinde kalori payın yüksek; antrenman günlerinde protein ve karbonhidrat kalitesine odaklanmak faydalı.");
                }
            }
            case MAINTAIN -> {
                if (Math.abs(rem) <= 80 && net > 0) {
                    m.add("Koruma hedefinde bugün net enerji neredeyse denge noktasında; bu ritmi sürdürmek iyi bir işaret.");
                }
            }
        }
    }

    private static void mealPatternTips(Map<MealType, MealMacroDto> byMeal, int consumedKcal, List<String> m) {
        if (byMeal == null || consumedKcal < 120) {
            return;
        }
        MealMacroDto b = byMeal.get(MealType.BREAKFAST);
        int bk = b != null ? b.calories() : 0;
        if (bk == 0 && consumedKcal > 200) {
            m.add("Kahvaltıda kayıt yok; enerjiyi güne yaymak genelde açlık dalgalarını azaltır.");
        }
        MealMacroDto sn = byMeal.get(MealType.SNACK);
        int sk = sn != null ? sn.calories() : 0;
        if (consumedKcal > 0 && sk > consumedKcal * 0.38) {
            m.add("Kalorinin önemli kısmı ara öğünden geliyor; mümkünse ana öğünlere kaydırmak doygunluğu artırabilir.");
        }
    }

    private static List<String> trim(List<String> m) {
        if (m.size() <= MAX_MESSAGES) {
            return m;
        }
        return new ArrayList<>(m.subList(0, MAX_MESSAGES));
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
