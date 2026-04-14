package com.akillidiyet.service;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.domain.ExerciseLog;
import com.akillidiyet.repo.ExerciseLogRepository;
import com.akillidiyet.web.dto.AddExerciseLogRequest;
import com.akillidiyet.web.dto.ExerciseLogResponseDto;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ExerciseLogCommandService {

    private final ExerciseLogRepository exerciseLogRepository;

    @Transactional(readOnly = true)
    public List<ExerciseLogResponseDto> listForDay(AppUser user, LocalDate date) {
        return exerciseLogRepository.findByUserAndLogDateOrderByIdAsc(user, date).stream()
                .map(
                        e ->
                                ExerciseLogResponseDto.builder()
                                        .id(e.getId())
                                        .date(e.getLogDate())
                                        .caloriesBurned(e.getCaloriesBurned())
                                        .label(e.getLabel())
                                        .build())
                .toList();
    }

    @Transactional
    public void delete(AppUser user, Long id) {
        ExerciseLog log =
                exerciseLogRepository
                        .findByIdAndUser(id, user)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kayıt bulunamadı"));
        exerciseLogRepository.delete(log);
    }

    @Transactional
    public int totalBurnedForDay(AppUser user, AddExerciseLogRequest req) {
        ExerciseLog log =
                ExerciseLog.builder()
                        .user(user)
                        .logDate(req.date())
                        .caloriesBurned(req.caloriesBurned())
                        .label(req.label() != null && !req.label().isBlank() ? req.label().trim() : null)
                        .build();
        exerciseLogRepository.save(log);
        return exerciseLogRepository.findByUserAndLogDateOrderByIdAsc(user, req.date()).stream()
                .mapToInt(ExerciseLog::getCaloriesBurned)
                .sum();
    }
}
