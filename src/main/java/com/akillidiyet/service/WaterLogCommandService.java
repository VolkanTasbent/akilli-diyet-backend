package com.akillidiyet.service;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.domain.WaterLog;
import com.akillidiyet.repo.WaterLogRepository;
import com.akillidiyet.web.dto.AddWaterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WaterLogCommandService {

    private final WaterLogRepository waterLogRepository;

    @Transactional
    public int addWater(AppUser user, AddWaterRequest req) {
        WaterLog log =
                waterLogRepository
                        .findByUserAndLogDate(user, req.date())
                        .orElseGet(
                                () ->
                                        WaterLog.builder()
                                                .user(user)
                                                .logDate(req.date())
                                                .totalMl(0)
                                                .build());
        log.setTotalMl(log.getTotalMl() + req.addMl());
        waterLogRepository.save(log);
        return log.getTotalMl();
    }
}
