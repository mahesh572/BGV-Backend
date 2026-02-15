package com.org.bgv.auth.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.auth.dto.TokenValidationResult;
import com.org.bgv.auth.entity.PasswordResetToken;
import com.org.bgv.auth.repository.PasswordResetTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResetTokenService {

    private final PasswordResetTokenRepository repository;

    @Value("${app.reset-password.base-url}")
    private String resetBaseUrl;

    @Value("${app.reset-password.expiry-hours:24}")
    private int expiryHours;

    /**
     * Generates reset link and persists token
     */
    @Transactional
    public String generateResetLink(Long userId) {

        // Invalidate previous tokens
        repository.deleteByUserId(userId);

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .userId(userId)
                .token(token)
                .expiresAt(LocalDateTime.now().plusHours(expiryHours))
                .used(false)
                .build();

        repository.save(resetToken);

        return resetBaseUrl + "?token=" + token;
    }

    /**
     * Validates token before password reset
     */
    public TokenValidationResult validateToken(String token) {

        Optional<PasswordResetToken> optionalToken = repository.findByToken(token);

        if (optionalToken.isEmpty()) {
            return new TokenValidationResult(false, "Invalid reset token");
        }

        PasswordResetToken resetToken = optionalToken.get();

        if (resetToken.isUsed()) {
            return new TokenValidationResult(false, "Reset token already used");
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return new TokenValidationResult(false, "Reset token expired");
        }

        return new TokenValidationResult(true, "Reset token is valid");
    }
    
    public PasswordResetToken getValidTokenOrThrow(String token) {

        PasswordResetToken resetToken = repository.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("Invalid reset token"));

        if (resetToken.isUsed()) {
            throw new IllegalStateException("Reset token already used");
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Reset token expired");
        }

        return resetToken;
    }



    /**
     * Marks token as used after successful reset
     */
    @Transactional
    public void markUsed(PasswordResetToken token) {
        token.setUsed(true);
        repository.save(token);
    }
}
