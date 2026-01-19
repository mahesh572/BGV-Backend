package com.org.bgv.notifications.service;

import java.time.Instant;

import com.org.bgv.notifications.NotificationChannel;
import com.org.bgv.notifications.NotificationLog;
import com.org.bgv.notifications.dto.NotificationContext;
import com.org.bgv.notifications.entity.NotificationPolicyChannel;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public final class NotificationLogFactory {

    private NotificationLogFactory() {
        // prevent instantiation
    }

    public static NotificationLog email(
            NotificationContext context,
            NotificationPolicyChannel channel,
            boolean success,
            String failureReason
    ) {
        return baseLog(context, channel, NotificationChannel.EMAIL, success, failureReason);
    }

    public static NotificationLog sms(
            NotificationContext context,
            NotificationPolicyChannel channel,
            boolean success,
            String failureReason
    ) {
        return baseLog(context, channel, NotificationChannel.SMS, success, failureReason);
    }

    public static NotificationLog inApp(
            NotificationContext context,
            NotificationPolicyChannel channel,
            boolean success,
            String failureReason
    ) {
        return baseLog(context, channel, NotificationChannel.IN_APP, success, failureReason);
    }
    
    private static NotificationLog baseLog(
            NotificationContext context,
            NotificationPolicyChannel policyChannel,
            NotificationChannel channel,
            boolean success,
            String failureReason
    ) {
        NotificationLog log = new NotificationLog();

        log.setCompanyId(context.getCompanyId());
        log.setEvent(context.getEvent());
        log.setChannel(channel);

        log.setRecipient(
                resolveRecipient(policyChannel, context)
        );

        log.setTemplateCode(
                policyChannel.getTemplateCode()
        );

        log.setSuccess(success);
        log.setFailureReason(failureReason);
        log.setSentAt(Instant.now());

        return log;
    }
    
    private static String resolveRecipient(
            NotificationPolicyChannel channel,
            NotificationContext context
    ) {
        return switch (channel.getRecipient()) {
            case CANDIDATE -> context.getCandidateEmail();
            case EMPLOYER_ADMIN -> context.getEmployerAdminEmail();
            case RECRUITER -> context.getRecruiterEmail();
            case VENDOR -> context.getVendorEmail();
        };
    }


}

