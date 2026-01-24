package com.org.bgv.notifications.service;

import java.time.Instant;

import com.org.bgv.notifications.NotificationChannel;
import com.org.bgv.notifications.NotificationLog;
import com.org.bgv.notifications.dto.NotificationContext;
import com.org.bgv.notifications.entity.NotificationPolicyChannel;
import com.org.bgv.notifications.entity.NotificationPolicyRecipient;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public final class NotificationLogFactory {

    private NotificationLogFactory() {
        // prevent instantiation
    }

    public static NotificationLog email(
            NotificationContext context,
            NotificationPolicyRecipient recipient,
            NotificationPolicyChannel channel,
            boolean success,
            String failureReason
    ) {
        return baseLog(context, recipient, channel, NotificationChannel.EMAIL, success, failureReason);
    }


    public static NotificationLog sms(
            NotificationContext context,
            NotificationPolicyRecipient recipient,
            NotificationPolicyChannel channel,
            boolean success,
            String failureReason
    ) {
    	 return baseLog(context, recipient, channel, NotificationChannel.SMS, success, failureReason);
       
    }

    public static NotificationLog inApp(
            NotificationContext context,
            NotificationPolicyRecipient recipient,
            NotificationPolicyChannel channel,
            boolean success,
            String failureReason
    ) {
        
        return baseLog(context, recipient, channel, NotificationChannel.IN_APP, success, failureReason);
    }
    
    private static NotificationLog baseLog(
            NotificationContext context,
            NotificationPolicyRecipient recipient,
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
                resolveRecipient(recipient, context)
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
            NotificationPolicyRecipient recipient,
            NotificationContext context
    ) {
        return switch (recipient.getRecipient()) {
            case CANDIDATE -> context.getCandidateEmail();
            case EMPLOYER -> context.getEmployerAdminEmail();
            case RECRUITER -> context.getRecruiterEmail();
            case VENDOR -> context.getVendorEmail();
		    default -> throw new IllegalArgumentException("Unexpected value: " + recipient.getRecipient());
        };
    }



}

