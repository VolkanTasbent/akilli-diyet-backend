package com.akillidiyet.web;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.security.AppUserDetails;
import com.akillidiyet.service.CurrentUserService;
import com.akillidiyet.service.ExerciseLogCommandService;
import com.akillidiyet.service.FoodLogCommandService;
import com.akillidiyet.service.SleepLogCommandService;
import com.akillidiyet.service.WaterLogCommandService;
import com.akillidiyet.service.WeightLogCommandService;
import com.akillidiyet.web.dto.AddExerciseLogRequest;
import com.akillidiyet.web.dto.AddFoodLogRequest;
import com.akillidiyet.web.dto.AddSleepLogRequest;
import com.akillidiyet.web.dto.AddWaterRequest;
import com.akillidiyet.web.dto.AddWeightLogRequest;
import com.akillidiyet.web.dto.ExerciseLogResponseDto;
import com.akillidiyet.web.dto.FoodLogResponseDto;
import com.akillidiyet.web.dto.UpdateFoodLogRequest;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {

    private final CurrentUserService currentUserService;
    private final FoodLogCommandService foodLogCommandService;
    private final WaterLogCommandService waterLogCommandService;
    private final WeightLogCommandService weightLogCommandService;
    private final ExerciseLogCommandService exerciseLogCommandService;
    private final SleepLogCommandService sleepLogCommandService;

    @GetMapping("/food")
    public List<FoodLogResponseDto> listFoodLogs(
            @AuthenticationPrincipal AppUserDetails details,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        AppUser u = currentUserService.require(details);
        return foodLogCommandService.listForDay(u, date);
    }

    @PostMapping("/food")
    @ResponseStatus(HttpStatus.CREATED)
    public void logFood(@AuthenticationPrincipal AppUserDetails details, @Valid @RequestBody AddFoodLogRequest body) {
        AppUser u = currentUserService.require(details);
        foodLogCommandService.add(u, body);
    }

    @PatchMapping("/food/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateFoodLog(
            @AuthenticationPrincipal AppUserDetails details,
            @PathVariable Long id,
            @Valid @RequestBody UpdateFoodLogRequest body) {
        AppUser u = currentUserService.require(details);
        foodLogCommandService.update(u, id, body);
    }

    @DeleteMapping("/food/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFoodLog(@AuthenticationPrincipal AppUserDetails details, @PathVariable Long id) {
        AppUser u = currentUserService.require(details);
        foodLogCommandService.delete(u, id);
    }

    @PostMapping("/water")
    public Map<String, Integer> addWater(
            @AuthenticationPrincipal AppUserDetails details, @Valid @RequestBody AddWaterRequest body) {
        AppUser u = currentUserService.require(details);
        int total = waterLogCommandService.addWater(u, body);
        return Map.of("totalMl", total);
    }

    @PostMapping("/weight")
    public Map<String, Double> logWeight(
            @AuthenticationPrincipal AppUserDetails details, @Valid @RequestBody AddWeightLogRequest body) {
        AppUser u = currentUserService.require(details);
        double kg = weightLogCommandService.upsert(u, body);
        return Map.of("weightKg", kg);
    }

    @PostMapping("/exercise")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Integer> logExercise(
            @AuthenticationPrincipal AppUserDetails details, @Valid @RequestBody AddExerciseLogRequest body) {
        AppUser u = currentUserService.require(details);
        int dayTotal = exerciseLogCommandService.totalBurnedForDay(u, body);
        return Map.of("totalBurned", dayTotal);
    }

    @GetMapping("/exercise")
    public List<ExerciseLogResponseDto> listExercise(
            @AuthenticationPrincipal AppUserDetails details,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        AppUser u = currentUserService.require(details);
        return exerciseLogCommandService.listForDay(u, date);
    }

    @DeleteMapping("/exercise/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExercise(@AuthenticationPrincipal AppUserDetails details, @PathVariable Long id) {
        AppUser u = currentUserService.require(details);
        exerciseLogCommandService.delete(u, id);
    }

    @PostMapping("/sleep")
    public Map<String, Double> logSleep(
            @AuthenticationPrincipal AppUserDetails details, @Valid @RequestBody AddSleepLogRequest body) {
        AppUser u = currentUserService.require(details);
        double h = sleepLogCommandService.upsert(u, body);
        return Map.of("hoursSlept", h);
    }
}
