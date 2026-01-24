package com.org.bgv.notifications.service;

import org.springframework.stereotype.Service;

import com.org.bgv.notifications.dto.NotificationContext;
import com.org.bgv.notifications.entity.NotificationPolicyChannel;
import com.org.bgv.notifications.entity.NotificationPolicyRecipient;
import com.org.bgv.notifications.entity.SmsTemplate;
import com.org.bgv.notifications.repository.NotificationLogRepository;
import com.org.bgv.notifications.repository.SmsTemplateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SmsNotificationService {

    private final SmsTemplateRepository templateRepository;
    private final NotificationLogRepository logRepository;

    public void send(
            NotificationPolicyRecipient recipient,
            NotificationPolicyChannel channel,
            NotificationContext context
    ) {

        // 🔐 Channel sanity checks
        if (!channel.isEnabled()) {
            return;
        }

        if (channel.getTemplateCode() == null) {
            logRepository.save(
                NotificationLogFactory.sms(
                    context,
                    recipient,
                    channel,
                    false,
                    "Template code not configured"
                )
            );
            return;
        }

        SmsTemplate template =
                templateRepository.findResolvedTemplate(
                        channel.getTemplateCode(),
                        context.getCompanyId()
                );

        if (template == null) {
            logRepository.save(
                NotificationLogFactory.sms(
                    context,
                    recipient,
                    channel,
                    false,
                    "Template not found"
                )
            );
            return;
        }

        if (!template.isActive()) {
            logRepository.save(
                NotificationLogFactory.sms(
                    context,
                    recipient,
                    channel,
                    false,
                    "Template is inactive"
                )
            );
            return;
        }

        try {
            String message = TemplateEngine.render(
                    template.getMessage(),
                    context.getVariables()
            );

            // 🔁 Actual SMS sending (Twilio / AWS SNS / Karix / Gupshup)
            boolean success = true;

            logRepository.save(
                NotificationLogFactory.sms(
                    context,
                    recipient,
                    channel,
                    success,
                    null
                )
            );

        } catch (Exception ex) {
            logRepository.save(
                NotificationLogFactory.sms(
                    context,
                    recipient,
                    channel,
                    false,
                    ex.getMessage()
                )
            );
        }
    }
}
