package com.org.bgv.notifications.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.org.bgv.notifications.NotificationEvent;
import com.org.bgv.notifications.entity.NotificationPolicy;
import com.org.bgv.notifications.repository.NotificationPolicyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationPolicyResolver {

    private final NotificationPolicyRepository policyRepository;

    
    public NotificationPolicy resolve(NotificationEvent event, Long companyId) {

        // 1️⃣ Employer override
        Optional<NotificationPolicy> employerPolicy =
                policyRepository.findByEventAndCompanyId(event, companyId);

        if (employerPolicy.isPresent()) {
            return employerPolicy.get();
        }

        // 2️⃣ Platform default
        return policyRepository
                .findByEventAndCompanyId(event, null)
                .orElse(null);
    }
}

