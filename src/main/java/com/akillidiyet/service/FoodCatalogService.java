package com.akillidiyet.service;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.domain.Food;
import com.akillidiyet.repo.FoodLogEntryRepository;
import com.akillidiyet.repo.FoodRepository;
import com.akillidiyet.web.dto.CreateFoodRequest;
import com.akillidiyet.web.dto.FoodResponse;
import com.akillidiyet.web.dto.UpdateFoodRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class FoodCatalogService {

    private final FoodRepository foodRepository;
    private final FoodLogEntryRepository foodLogEntryRepository;

    @Transactional(readOnly = true)
    public List<FoodResponse> listMine(AppUser user) {
        return foodRepository.findByOwnerOrderByNameAsc(user).stream()
                .map(f -> toDto(f, foodLogEntryRepository.countByFood_Id(f.getId()) > 0))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FoodResponse> search(AppUser user, String q) {
        String term = q == null ? "" : q.trim();
        List<Food> global;
        List<Food> mine;
        if (term.length() < 2) {
            global = foodRepository.findByOwnerIsNullOrderByNameAsc();
            mine = foodRepository.findByOwnerOrderByNameAsc(user);
        } else {
            global = foodRepository.findByOwnerIsNullAndNameContainingIgnoreCaseOrderByNameAsc(term);
            mine = foodRepository.findByOwnerAndNameContainingIgnoreCaseOrderByNameAsc(user, term);
        }
        List<Food> merged = new ArrayList<>(global.size() + mine.size());
        merged.addAll(global);
        merged.addAll(mine);
        merged.sort(Comparator.comparing(Food::getName, String.CASE_INSENSITIVE_ORDER));
        return merged.stream().map(f -> toDto(f, false)).toList();
    }

    @Transactional
    public FoodResponse createCustom(AppUser owner, CreateFoodRequest req) {
        Food f =
                Food.builder()
                        .name(req.name().trim())
                        .caloriesPer100g(req.caloriesPer100g())
                        .proteinPer100g(req.proteinPer100g())
                        .carbsPer100g(req.carbsPer100g())
                        .fatPer100g(req.fatPer100g())
                        .tablespoonGrams(req.tablespoonGrams())
                        .sliceGrams(req.sliceGrams())
                        .owner(owner)
                        .build();
        f = foodRepository.save(f);
        return toDto(f, false);
    }

    @Transactional
    public FoodResponse updateCustom(AppUser owner, Long id, UpdateFoodRequest req) {
        Food f =
                foodRepository
                        .findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Besin bulunamadı"));
        if (f.getOwner() == null || !f.getOwner().getId().equals(owner.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu besini düzenleyemezsin");
        }
        f.setName(req.name().trim());
        f.setCaloriesPer100g(req.caloriesPer100g());
        f.setProteinPer100g(req.proteinPer100g());
        f.setCarbsPer100g(req.carbsPer100g());
        f.setFatPer100g(req.fatPer100g());
        f.setTablespoonGrams(req.tablespoonGrams());
        f.setSliceGrams(req.sliceGrams());
        return toDto(f, foodLogEntryRepository.countByFood_Id(f.getId()) > 0);
    }

    @Transactional
    public void deleteCustom(AppUser owner, Long id) {
        Food f =
                foodRepository
                        .findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Besin bulunamadı"));
        if (f.getOwner() == null || !f.getOwner().getId().equals(owner.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu besini silemezsin");
        }
        if (foodLogEntryRepository.countByFood_Id(id) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Bu besin günlük kayıtlarında kullanılıyor; önce kayıtları sil veya düzenle.");
        }
        foodRepository.delete(f);
    }

    private static FoodResponse toDto(Food f, boolean usedInLogs) {
        return FoodResponse.builder()
                .id(f.getId())
                .name(f.getName())
                .caloriesPer100g(f.getCaloriesPer100g())
                .proteinPer100g(f.getProteinPer100g())
                .carbsPer100g(f.getCarbsPer100g())
                .fatPer100g(f.getFatPer100g())
                .tablespoonGrams(f.getTablespoonGrams())
                .sliceGrams(f.getSliceGrams())
                .custom(f.getOwner() != null)
                .usedInLogs(usedInLogs)
                .build();
    }
}
