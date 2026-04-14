package com.akillidiyet.repo;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.domain.PasswordResetToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUser(AppUser user);

    Optional<PasswordResetToken> findFirstByUser_EmailIgnoreCaseOrderByIdDesc(String email);
}
