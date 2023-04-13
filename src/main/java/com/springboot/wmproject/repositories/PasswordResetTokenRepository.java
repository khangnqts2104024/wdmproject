package com.springboot.wmproject.repositories;

import com.springboot.wmproject.entities.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long> {
    Optional<PasswordResetToken> findByToken(String token);

    List<PasswordResetToken> findByExpiryDateBefore(String currentTime);
    void deleteByExpiryDateBefore(String currentTime);
}
