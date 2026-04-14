package com.akillidiyet.config;

import com.akillidiyet.domain.Food;
import com.akillidiyet.repo.FoodRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final FoodRepository foodRepository;

    @Bean
    CommandLineRunner seedFoods() {
        return args -> {
            if (foodRepository.count() > 0) {
                return;
            }
            List<Food> foods =
                    List.of(
                            food("Tavuk göğsü (pişmiş)", 165, 31, 0, 3.6, 15.0),
                            food("Pirinç pilavı", 130, 2.7, 28, 0.3, 12.0),
                            food("Mercimek çorbası", 56, 3.6, 8.8, 1.2, 15.0),
                            food("Yoğurt (tam yağlı)", 61, 3.5, 4.7, 3.3, 15.0),
                            food("Yumurta (haşlanmış)", 155, 13, 1.1, 11, 12.0),
                            food("Tam buğday ekmeği", 247, 13, 41, 3.4, 12.0),
                            food("Zeytinyağı", 884, 0, 0, 100, 14.0),
                            food("Ton balığı (suda)", 116, 25.5, 0, 0.8, 15.0),
                            food("Beyaz peynir", 264, 14, 4.1, 21, 14.0),
                            food("Menemen (ortalama)", 95, 6.5, 4.2, 6.5, 12.0));
            foodRepository.saveAll(foods);
        };
    }

    private static Food food(
            String name, double kcal, double p, double c, double f, Double tbsp) {
        return Food.builder()
                .name(name)
                .caloriesPer100g(kcal)
                .proteinPer100g(p)
                .carbsPer100g(c)
                .fatPer100g(f)
                .tablespoonGrams(tbsp)
                .build();
    }
}
