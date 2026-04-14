package com.akillidiyet.repo;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.domain.WeightLog;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeightLogRepository extends JpaRepository<WeightLog, Long> {

    Optional<WeightLog> findByUserAndLogDate(AppUser user, LocalDate date);

    List<WeightLog> findByUserAndLogDateBetweenOrderByLogDateAscIdAsc(AppUser user, LocalDate from, LocalDate to);

    List<WeightLog> findByUserOrderByLogDateDesc(AppUser user);

    boolean existsByUserAndLogDate(AppUser user, LocalDate date);
}
