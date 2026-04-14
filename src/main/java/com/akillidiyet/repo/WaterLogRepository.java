package com.akillidiyet.repo;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.domain.WaterLog;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaterLogRepository extends JpaRepository<WaterLog, Long> {

    Optional<WaterLog> findByUserAndLogDate(AppUser user, LocalDate date);

    List<WaterLog> findByUserAndLogDateBetweenOrderByLogDateAsc(AppUser user, LocalDate from, LocalDate to);
}
