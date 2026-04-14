package com.akillidiyet.service;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.domain.Food;
import com.akillidiyet.domain.FoodLogEntry;
import com.akillidiyet.repo.FoodLogEntryRepository;
import com.akillidiyet.repo.FoodRepository;
import com.akillidiyet.web.dto.AddFoodLogRequest;
import com.akillidiyet.web.dto.FoodLogResponseDto;
import com.akillidiyet.web.dto.UpdateFoodLogRequest;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class FoodLogCommandService {

    private final FoodRepository foodRepository;
    private final FoodLogEntryRepository foodLogRepository;

    @Transactional(readOnly = true)
    public List<FoodLogResponseDto> listForDay(AppUser user, LocalDate date) {
        return foodLogRepository.findByUserAndLogDateOrderByIdAsc(user, date).stream()
                .map(FoodLogCommandService::toDto)
                .toList();
    }

    @Transactional
    public void add(AppUser user, AddFoodLogRequest req) {
        Food food = requireFoodForUser(user, req.foodId());
        FoodLogEntry e =
                FoodLogEntry.builder()
                        .user(user)
                        .logDate(req.date())
                        .mealType(req.mealType())
                        .food(food)
                        .grams(req.grams())
                        .note(req.note())
                        .build();
        foodLogRepository.save(e);
    }

    @Transactional
    public void update(AppUser user, Long entryId, UpdateFoodLogRequest req) {
        FoodLogEntry e =
                foodLogRepository
                        .findById(entryId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kayıt bulunamadı"));
        if (!e.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu kaydı düzenleyemezsin");
        }
        Food food = requireFoodForUser(user, req.foodId());
        e.setLogDate(req.date());
        e.setMealType(req.mealType());
        e.setFood(food);
        e.setGrams(req.grams());
        e.setNote(req.note());
    }

    @Transactional
    public void delete(AppUser user, Long entryId) {
        FoodLogEntry e =
                foodLogRepository
                        .findById(entryId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kayıt bulunamadı"));
        if (!e.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu kaydı silemezsin");
        }
        foodLogRepository.delete(e);
    }

    private Food requireFoodForUser(AppUser user, Long foodId) {
        Food food =
                foodRepository
                        .findById(foodId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Besin bulunamadı"));
        if (food.getOwner() != null && !food.getOwner().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu besine erişemezsin");
        }
        return food;
    }

    private static FoodLogResponseDto toDto(FoodLogEntry e) {
        return FoodLogResponseDto.builder()
                .id(e.getId())
                .date(e.getLogDate())
                .mealType(e.getMealType())
                .foodId(e.getFood().getId())
                .foodName(e.getFood().getName())
                .grams(e.getGrams())
                .note(e.getNote())
                .caloriesEstimate(NutritionService.caloriesForGrams(e.getFood(), e.getGrams()))
                .build();
    }
}
