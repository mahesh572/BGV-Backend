package com.org.bgv.user.service;

import org.springframework.stereotype.Service;

import com.org.bgv.auth.entity.PasswordResetToken;
import com.org.bgv.auth.service.ResetTokenService;
import com.org.bgv.entity.User;
import com.org.bgv.repository.UserRepository;
import com.org.bgv.user.enums.UserStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountActivationService {

    private final ResetTokenService activationTokenService;
    private final UserRepository userRepository;

    @Transactional
    public void activateAccount(String token) {

        log.info("Activating account using token");

        PasswordResetToken activationToken =
                activationTokenService.getValidTokenOrThrow(token);

        User user = userRepository.findById(activationToken.getUserId())
                .orElseThrow(() ->
                        new IllegalStateException("User not found"));

        if (Boolean.TRUE.equals(user.getIsVerified())) {
            throw new IllegalStateException("Account is already activated.");
        }

        user.setIsVerified(true);
        user.setIsActive(true);
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);

        activationTokenService.markUsed(activationToken);

        log.info("User account activated successfully. userId={}", user.getUserId());
    }
}
