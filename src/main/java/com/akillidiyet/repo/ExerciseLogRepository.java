package com.akillidiyet.repo;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.domain.ExerciseLog;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseLogRepository extends JpaRepository<ExerciseLog, Long> {

    List<ExerciseLog> findByUserAndLogDateOrderByIdAsc(AppUser user, LocalDate date);

    List<ExerciseLog> findByUserAndLogDateBetweenOrderByLogDateAscIdAsc(
            AppUser user, LocalDate from, LocalDate to);

    boolean existsByUserAndLogDate(AppUser user, LocalDate date);

    Optional<ExerciseLog> findByIdAndUser(Long id, AppUser user);
}
