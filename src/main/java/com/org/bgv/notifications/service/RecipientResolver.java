package com.org.bgv.notifications.service;

import org.springframework.stereotype.Service;

import com.org.bgv.notifications.dto.NotificationContext;
import com.org.bgv.notifications.entity.NotificationPolicyRecipient;

@Service
public class RecipientResolver {

    public String resolve(
            NotificationPolicyRecipient recipient,
            NotificationContext context
    ) {

        return switch (recipient.getRecipient()) {

            case USER, RECRUITER ->
                    context.getUserEmailAddress();

            case CANDIDATE ->
                    context.getCandidateEmail();

            case EMPLOYER ->
                    context.getCompanySupportEmail();

            case ADMIN ->
                    "platform-admin@bgv.com"; // fallback or platform setting

            case VENDOR ->
                    context.getVendorEmail();
        };
    }
}
