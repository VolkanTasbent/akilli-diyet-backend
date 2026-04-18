package com.akillidiyet.config;

import com.akillidiyet.domain.Food;
import com.akillidiyet.repo.FoodRepository;
import java.util.ArrayList;
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
            List<Food> toSave = new ArrayList<>();
            for (Food f : defaultCatalog()) {
                if (!foodRepository.existsByOwnerIsNullAndNameIgnoreCase(f.getName())) {
                    toSave.add(f);
                }
            }
            if (!toSave.isEmpty()) {
                foodRepository.saveAll(toSave);
            }
        };
    }

    /** Yaklaşık değerler (100 g başına); tbsp = 1 yemek kaşığı ~gram (isteğe bağlı). */
    private static List<Food> defaultCatalog() {
        return List.of(
                food("Tavuk göğsü (pişmiş)", 165, 31, 0, 3.6, 15.0),
                food("Tavuk but (pişmiş)", 209, 26, 0, 11, null),
                food("Hindi göğsü (pişmiş)", 135, 30, 0, 1, null),
                food("Kıyma (yağsız, pişmiş)", 250, 26, 0, 15, null),
                food("Dana bonfile (ızgara)", 180, 28, 0, 7, null),
                food("Kuzu pirzola (pişmiş)", 290, 25, 0, 20, null),
                food("Somon (ızgara)", 206, 22, 0, 12, null),
                food("Levrek (ızgara)", 124, 23, 0, 3, null),
                food("Hamsi (kızartma)", 220, 20, 8, 12, null),
                food("Ton balığı (suda)", 116, 25.5, 0, 0.8, 15.0),
                food("Sucuk (pişmiş)", 330, 16, 1, 28, null),
                food("Pastırma", 250, 30, 1, 13, null),
                food("Sosis (tavada)", 280, 12, 2, 25, null),
                food("Yumurta (haşlanmış)", 155, 13, 1.1, 11, 12.0),
                food("Yumurta (omlet)", 154, 10.6, 1.6, 12, null),
                food("Lor peyniri (yağsız)", 72, 11, 3.5, 0.5, null),
                food("Beyaz peynir", 264, 14, 4.1, 21, 14.0),
                food("Kaşar peyniri", 350, 23, 2, 28, null),
                food("Labne", 180, 7, 4, 16, null),
                food("Yoğurt (tam yağlı)", 61, 3.5, 4.7, 3.3, 15.0),
                food("Yoğurt (light)", 45, 4, 4, 0.5, null),
                food("Ayran", 37, 2, 3.5, 1.5, null),
                food("Süt (tam yağlı)", 64, 3.3, 4.8, 3.6, null),
                food("Pirinç pilavı", 130, 2.7, 28, 0.3, 12.0),
                food("Bulgur pilavı", 120, 4, 22, 1.5, null),
                food("Makarna (haşlanmış)", 131, 5, 25, 1.1, null),
                food("Erişte (haşlanmış)", 138, 5, 27, 1.5, null),
                food("Kuskus (haşlanmış)", 112, 3.8, 23, 0.2, null),
                food("Tam buğday ekmeği", 247, 13, 41, 3.4, 12.0),
                food("Beyaz ekmek", 265, 9, 49, 3.2, null),
                food("Simit", 275, 10, 52, 4, null),
                food("Börek (peynirli, ort.)", 290, 8, 32, 15, null),
                food("Lahmacun", 250, 10, 28, 11, null),
                food("Pide (kıymalı)", 270, 12, 30, 12, null),
                food("Döner (tavuk)", 180, 18, 12, 7, null),
                food("Döner (et)", 215, 16, 10, 12, null),
                food("İskender (ortalama)", 180, 12, 12, 10, null),
                food("Köfte (ızgara)", 220, 18, 3, 15, null),
                food("Kuru fasulye (haşlama)", 90, 6, 16, 0.5, null),
                food("Nohut yemeği", 120, 6, 18, 3, null),
                food("Mercimek çorbası", 56, 3.6, 8.8, 1.2, 15.0),
                food("Ezogelin çorbası", 60, 2.5, 10, 1.5, null),
                food("Tarhana çorbası", 45, 2, 6, 1.2, null),
                food("Menemen", 95, 6.5, 4.2, 6.5, 12.0),
                food("Patates kızartması", 312, 3.4, 41, 15, null),
                food("Patates püresi (sütlü)", 85, 2, 14, 2.5, null),
                food("Ispanak (haşlama)", 23, 2.9, 3.6, 0.4, null),
                food("Brokoli (haşlama)", 35, 2.8, 7, 0.4, null),
                food("Salata (yeşillik, ort.)", 20, 1.5, 3, 0.3, null),
                food("Domates", 18, 0.9, 3.9, 0.2, null),
                food("Salatalık", 15, 0.7, 3.6, 0.1, null),
                food("Havuç", 41, 0.9, 10, 0.2, null),
                food("Elma", 52, 0.3, 14, 0.2, null),
                food("Muz", 89, 1.1, 23, 0.3, null),
                food("Portakal", 47, 0.9, 12, 0.1, null),
                food("Üzüm", 69, 0.7, 18, 0.2, null),
                food("Çilek", 32, 0.7, 7.7, 0.3, null),
                food("Ceviz", 654, 15, 14, 65, null),
                food("Badem", 579, 21, 22, 50, null),
                food("Fındık", 628, 15, 17, 61, null),
                food("Antep fıstığı", 562, 20, 28, 45, null),
                food("Zeytinyağı", 884, 0, 0, 100, 14.0),
                food("Tereyağı", 717, 0.9, 0.1, 81, 14.0),
                food("Bal", 304, 0.3, 82, 0, null),
                food("Reçel (ortalama)", 260, 0.3, 65, 0.1, null),
                food("Çikolata (sütlü)", 535, 8, 59, 30, null),
                food("Bisküvi (basit)", 450, 7, 70, 16, null),
                food("Cips (patates)", 536, 7, 53, 34, null),
                food("Ayran (ev yapımı)", 37, 2, 3.5, 1.5, null),
                food("Kefir", 40, 3.5, 4, 1.5, null),
                food("Tahin", 595, 17, 21, 54, 15.0),
                food("Pekmez", 290, 0.5, 70, 0.2, null),
                food("Humus", 166, 8, 14, 10, null),
                food("Acuka", 280, 5, 12, 25, null),
                food("Karnıyarık", 95, 6, 8, 4.5, null),
                food("İmam bayıldı", 85, 1.5, 9, 5, null),
                food("Mantı (yoğurtlu)", 130, 8, 15, 4.5, null),
                food("Çiğ köfte", 180, 5, 28, 5, null),
                food("Kumpir (ortalama)", 140, 3, 22, 4.5, null),
                food("Köfte ekmek", 280, 14, 28, 12, null),
                food("Balık ekmek", 220, 15, 24, 8, null),
                food("Tost (kaşarlı)", 290, 14, 22, 16, null),
                food("Omlet (peynirli)", 175, 12, 2, 13, null),
                food("Yulaf ezmesi (süt ile pişmiş)", 90, 4, 12, 3, null),
                food("Granola", 471, 10, 64, 20, null),
                food("Mısır gevreği", 350, 7, 84, 0.4, null),
                food("Pizza dilimi (ortalama)", 266, 11, 33, 10, null),
                food("Hamburger (ortalama)", 295, 17, 30, 12, null));
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
