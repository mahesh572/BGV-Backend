package com.org.bgv.notifications.service;

import org.springframework.stereotype.Service;

import com.org.bgv.notifications.dto.NotificationContext;
import com.org.bgv.notifications.entity.EmailTemplate;
import com.org.bgv.notifications.entity.NotificationPolicyChannel;
import com.org.bgv.notifications.entity.NotificationPolicyRecipient;
import com.org.bgv.notifications.repository.NotificationLogRepository;
import com.org.bgv.repository.EmailTemplateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private final EmailTemplateRepository templateRepository;
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
                NotificationLogFactory.email(
                    context,
                    recipient,
                    channel,
                    false,
                    "Template code not configured"
                )
            );
            return;
        }

        EmailTemplate template =
                templateRepository
                        .findResolvedTemplate(
                                channel.getTemplateCode(),
                                context.getCompanyId()
                        )
                        .orElse(null);

        if (template == null) {
            logRepository.save(
                NotificationLogFactory.email(
                    context,
                    recipient,
                    channel,
                    false,
                    "Template not found"
                )
            );
            return;
        }

        if (!Boolean.TRUE.equals(template.getIsActive())) {
            logRepository.save(
                NotificationLogFactory.email(
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
            String subject = TemplateEngine.render(
                    template.getSubject(),
                    context.getVariables()
            );

            String body = TemplateEngine.render(
                    template.getBodyHtml(),
                    context.getVariables()
            );

            // 🔁 Actual email sending (SMTP / SES / SendGrid)
            boolean success = true;

            logRepository.save(
                NotificationLogFactory.email(
                        context,
                        recipient,
                        channel,
                        success,
                        null
                )
            );

        } catch (Exception ex) {

            logRepository.save(
                NotificationLogFactory.email(
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

