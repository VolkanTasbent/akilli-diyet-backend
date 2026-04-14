package com.akillidiyet.repo;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.domain.SleepLog;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SleepLogRepository extends JpaRepository<SleepLog, Long> {

    Optional<SleepLog> findByUserAndLogDate(AppUser user, LocalDate date);

    boolean existsByUserAndLogDate(AppUser user, LocalDate date);

    List<SleepLog> findByUserAndLogDateBetweenOrderByLogDateAsc(AppUser user, LocalDate from, LocalDate to);
}
