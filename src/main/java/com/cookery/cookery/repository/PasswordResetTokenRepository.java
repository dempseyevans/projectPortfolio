package com.cookery.cookery.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cookery.cookery.entity.PasswordResetToken;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    PasswordResetToken findByToken(String token);

    void deleteByExpiryDateBefore(LocalDateTime now);

}
