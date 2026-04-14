package com.akillidiyet.repo;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.domain.FoodLogEntry;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodLogEntryRepository extends JpaRepository<FoodLogEntry, Long> {

    List<FoodLogEntry> findByUserAndLogDateOrderByIdAsc(AppUser user, LocalDate date);

    List<FoodLogEntry> findByUserAndLogDateBetweenOrderByLogDateAscIdAsc(
            AppUser user, LocalDate from, LocalDate to);

    boolean existsByUserAndLogDate(AppUser user, LocalDate date);

    long countByFood_Id(Long foodId);
}
