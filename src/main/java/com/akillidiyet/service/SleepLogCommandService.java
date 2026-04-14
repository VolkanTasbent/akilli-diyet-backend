package com.akillidiyet.service;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.domain.SleepLog;
import com.akillidiyet.repo.SleepLogRepository;
import com.akillidiyet.web.dto.AddSleepLogRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SleepLogCommandService {

    private final SleepLogRepository sleepLogRepository;

    @Transactional
    public double upsert(AppUser user, AddSleepLogRequest req) {
        SleepLog log =
                sleepLogRepository
                        .findByUserAndLogDate(user, req.date())
                        .orElseGet(
                                () ->
                                        SleepLog.builder()
                                                .user(user)
                                                .logDate(req.date())
                                                .hoursSlept(req.hoursSlept())
                                                .build());
        log.setHoursSlept(req.hoursSlept());
        sleepLogRepository.save(log);
        return log.getHoursSlept();
    }
}
