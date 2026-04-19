package com.akillidiyet.config;

import com.akillidiyet.domain.Food;
import com.akillidiyet.repo.FoodRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
            List<Food> toInsert = new ArrayList<>();
            List<Food> toUpdate = new ArrayList<>();
            for (Food f : defaultCatalog()) {
                Optional<Food> existing = foodRepository.findByOwnerIsNullAndNameIgnoreCase(f.getName());
                if (existing.isEmpty()) {
                    toInsert.add(f);
                } else {
                    Food e = existing.get();
                    e.setCaloriesPer100g(f.getCaloriesPer100g());
                    e.setProteinPer100g(f.getProteinPer100g());
                    e.setCarbsPer100g(f.getCarbsPer100g());
                    e.setFatPer100g(f.getFatPer100g());
                    e.setTablespoonGrams(f.getTablespoonGrams());
                    e.setSliceGrams(f.getSliceGrams());
                    toUpdate.add(e);
                }
            }
            if (!toInsert.isEmpty()) {
                foodRepository.saveAll(toInsert);
            }
            if (!toUpdate.isEmpty()) {
                foodRepository.saveAll(toUpdate);
            }
        };
    }

    /**
     * Yaklaşık değerler (100 g başına). tbsp = 1 yemek kaşığı ~g (çorba, yoğurt, reçel vb.). slice = 1 dilim
     * ~g (yalnızca dilimle servis edilenler).
     */
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
                food("Lor peyniri (yağsız)", 72, 11, 3.5, 0.5, 12.0),
                food("Beyaz peynir", 264, 14, 4.1, 21, 14.0),
                food("Kaşar peyniri", 350, 23, 2, 28, null),
                food("Labne", 180, 7, 4, 16, 12.0),
                food("Yoğurt (tam yağlı)", 61, 3.5, 4.7, 3.3, 15.0),
                food("Yoğurt (light)", 45, 4, 4, 0.5, 15.0),
                food("Ayran", 37, 2, 3.5, 1.5, 15.0),
                food("Süt (tam yağlı)", 64, 3.3, 4.8, 3.6, null),
                food("Pirinç pilavı", 130, 2.7, 28, 0.3, 12.0),
                food("Bulgur pilavı", 120, 4, 22, 1.5, 12.0),
                food("Makarna (haşlanmış)", 131, 5, 25, 1.1, 12.0),
                food("Erişte (haşlanmış)", 138, 5, 27, 1.5, 12.0),
                food("Kuskus (haşlanmış)", 112, 3.8, 23, 0.2, 11.0),
                food("Tam buğday ekmeği", 247, 13, 41, 3.4, 12.0),
                food("Beyaz ekmek", 265, 9, 49, 3.2, null),
                food("Dilim ekmek (beyaz, ort.)", 265, 9, 49, 3.2, null, 28.0),
                food("Simit", 275, 10, 52, 4, null),
                food("Börek (peynirli, ort.)", 290, 8, 32, 15, null),
                food("Lahmacun", 250, 10, 28, 11, null),
                food("Pide (kıymalı)", 270, 12, 30, 12, null),
                food("Pişi (kızartma)", 335, 6.5, 43, 16, null),
                food("Döner (tavuk)", 180, 18, 12, 7, null),
                food("Döner (et)", 215, 16, 10, 12, null),
                food("İskender (ortalama)", 180, 12, 12, 10, null),
                food("Köfte (ızgara)", 220, 18, 3, 15, null),
                food("Kuru fasulye (haşlama)", 90, 6, 16, 0.5, 15.0),
                food("Nohut yemeği", 120, 6, 18, 3, 14.0),
                food("Mercimek çorbası", 56, 3.6, 8.8, 1.2, 15.0),
                food("Ezogelin çorbası", 60, 2.5, 10, 1.5, 15.0),
                food("Tarhana çorbası", 45, 2, 6, 1.2, 12.0),
                food("Menemen", 95, 6.5, 4.2, 6.5, 12.0),
                food("Patates kızartması", 312, 3.4, 41, 15, null),
                food("Patates püresi (sütlü)", 85, 2, 14, 2.5, 12.0),
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
                food("Bal", 304, 0.3, 82, 0, 21.0),
                food("Reçel (ortalama)", 260, 0.3, 65, 0.1, 18.0),
                food("Çikolata (sütlü)", 535, 8, 59, 30, 8.0),
                food("Bitter çikolata (%70+)", 598, 7.8, 46, 43, 7.0),
                food("Baklava (ortalama)", 430, 6, 50, 23, null),
                food("Lokma", 330, 4, 45, 15, null),
                food("Tulumba tatlısı", 360, 2, 52, 16, null),
                food("Halka tatlısı", 355, 2, 51, 15, null),
                food("Künefe", 325, 7.5, 36, 17, null),
                food("Revani", 352, 5, 59, 11, null),
                food("Brownie", 466, 6, 60, 24, null),
                food("Kurabiye (ev, ort.)", 472, 6, 66, 22, null),
                food("Cheesecake (dilim)", 321, 5.5, 25, 23, null, 95.0),
                food("Tiramisu (dilim)", 292, 4.5, 30, 17, null, 80.0),
                food("Profiterol (dilim)", 290, 5, 35, 14, null, 70.0),
                food("Sütlaç", 118, 3, 22, 2.2, 15.0),
                food("Kazandibi", 165, 4, 28, 4, 15.0),
                food("Muhallebi", 142, 3.5, 25.5, 3.2, 15.0),
                food("Magnolia / puding (ort.)", 185, 3.8, 30, 6.5, 15.0),
                food("Dondurma (vanilya)", 207, 3.5, 24, 11, 12.0),
                food("Lokum (ortalama)", 380, 0.5, 85, 0, 12.0),
                food("Tahin helvası", 516, 8, 43, 30, 12.0),
                food("Kabak tatlısı", 175, 1.5, 35, 3.5, null),
                food("Bisküvi (basit)", 450, 7, 70, 16, null),
                food("Cips (patates)", 536, 7, 53, 34, null),
                food("Ayran (ev yapımı)", 37, 2, 3.5, 1.5, 15.0),
                food("Kefir", 40, 3.5, 4, 1.5, 15.0),
                food("Tahin", 595, 17, 21, 54, 15.0),
                food("Pekmez", 290, 0.5, 70, 0.2, 18.0),
                food("Humus", 166, 8, 14, 10, 15.0),
                food("Acuka", 280, 5, 12, 25, 12.0),
                food("Karnıyarık", 95, 6, 8, 4.5, 18.0),
                food("İmam bayıldı", 85, 1.5, 9, 5, 18.0),
                food("Mantı (yoğurtlu)", 130, 8, 15, 4.5, 14.0),
                food("Çiğ köfte", 180, 5, 28, 5, null),
                food("Kumpir (ortalama)", 140, 3, 22, 4.5, null),
                food("Köfte ekmek", 280, 14, 28, 12, null),
                food("Balık ekmek", 220, 15, 24, 8, null),
                food("Tost (kaşarlı)", 290, 14, 22, 16, null),
                food("Omlet (peynirli)", 175, 12, 2, 13, null),
                food("Yulaf ezmesi (süt ile pişmiş)", 90, 4, 12, 3, 14.0),
                food("Granola", 471, 10, 64, 20, 12.0),
                food("Mısır gevreği", 350, 7, 84, 0.4, 10.0),
                food("Pizza dilimi (ortalama)", 266, 11, 33, 10, null, 100.0),
                food("Hamburger (ortalama)", 295, 17, 30, 12, null),
                food("Gözleme (peynirli)", 288, 10, 32, 14, null),
                food("Kinoa (haşlanmış)", 120, 4.4, 22, 1.9, 12.0),
                food("Barbunya pilaki", 105, 6.5, 16, 2.2, 15.0),
                food("Zeytin (siyah)", 115, 0.8, 6.3, 11, 8.0),
                food("Zeytin (yeşil)", 145, 1, 3.8, 15, 8.0),
                food("Domates çorbası", 33, 1.2, 6, 0.5, 15.0),
                food("Tavuk suyu çorbası", 36, 2.5, 4.2, 1.2, 15.0),
                food("İşkembe çorbası", 60, 4, 2, 4, 15.0),
                food("Hurma (kuru)", 277, 1.8, 75, 0.2, 15.0),
                food("İncir (kuru)", 249, 3.3, 64, 0.9, null),
                food("Fıstık ezmesi", 589, 25, 20, 50, 15.0),
                food("Ay çekirdeği (kavrulmuş, tuzlu)", 572, 21, 21, 50, null),
                food("Siyah çay (şekersiz, 1 fincan)", 1, 0, 0.3, 0, null),
                food("Türk kahvesi (şekersiz)", 2, 0.1, 0, 0, null),
                food("Portakal suyu (kutu)", 43, 0.6, 10.4, 0.1, 15.0),
                food("Smoothie (meyveli, ort.)", 65, 1.5, 14, 0.5, 15.0),
                food("Protein bar (ortalama)", 400, 25, 40, 14, null),
                food("Waffle (hamur, ort.)", 310, 6, 42, 13, null),
                food("Kek (pandispanya, ort.)", 350, 5, 52, 14, null),
                food("Pankek (maple şuruplu, ort.)", 220, 5, 35, 6.5, null),
                food("French toast (ekmek kızartması)", 240, 9, 28, 10, null));
    }

    private static Food food(String name, double kcal, double p, double c, double f, Double tbsp) {
        return food(name, kcal, p, c, f, tbsp, null);
    }

    private static Food food(
            String name, double kcal, double p, double c, double f, Double tbsp, Double sliceGrams) {
        return Food.builder()
                .name(name)
                .caloriesPer100g(kcal)
                .proteinPer100g(p)
                .carbsPer100g(c)
                .fatPer100g(f)
                .tablespoonGrams(tbsp)
                .sliceGrams(sliceGrams)
                .build();
    }
}
