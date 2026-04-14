package com.akillidiyet.service;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.domain.WeightLog;
import com.akillidiyet.repo.WeightLogRepository;
import com.akillidiyet.web.dto.AddWeightLogRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WeightLogCommandService {

    private final WeightLogRepository weightLogRepository;

    @Transactional
    public double upsert(AppUser user, AddWeightLogRequest req) {
        WeightLog log =
                weightLogRepository
                        .findByUserAndLogDate(user, req.date())
                        .orElseGet(
                                () ->
                                        WeightLog.builder()
                                                .user(user)
                                                .logDate(req.date())
                                                .weightKg(req.weightKg())
                                                .build());
        log.setWeightKg(req.weightKg());
        weightLogRepository.save(log);
        return log.getWeightKg();
    }
}
