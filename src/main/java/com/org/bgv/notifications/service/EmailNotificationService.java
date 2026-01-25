package com.org.bgv.notifications.service;

import org.springframework.stereotype.Service;

import com.org.bgv.notifications.dto.NotificationContext;
import com.org.bgv.notifications.entity.EmailTemplate;
import com.org.bgv.notifications.entity.NotificationPolicyChannel;
import com.org.bgv.notifications.entity.NotificationPolicyRecipient;
import com.org.bgv.notifications.repository.NotificationLogRepository;
import com.org.bgv.repository.EmailTemplateRepository;
import com.org.bgv.service.EmailService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EmailNotificationService {

    private final EmailTemplateRepository templateRepository;
    private final NotificationLogRepository logRepository;
    private final EmailService emailService;
    private final RecipientResolver recipientResolver;
    private final EmailSenderResolver senderResolver;

    public void send(
            NotificationPolicyRecipient recipient,
            NotificationPolicyChannel channel,
            NotificationContext context
    ) {

        String to = recipientResolver.resolve(recipient, context);
        if (to == null) {
            logRepository.save(
                NotificationLogFactory.email(
                    context, recipient, channel, false,
                    "Recipient email not resolved"
                )
            );
            return;
        }

        String from = senderResolver.resolveFrom(context);

        EmailTemplate template =
                templateRepository
                        .findResolvedTemplate(
                                channel.getTemplateCode(),
                                context.getCompanyId()
                        )
                        .orElse(null);

        if (template == null || !Boolean.TRUE.equals(template.getIsActive())) {
            logRepository.save(
                NotificationLogFactory.email(
                    context, recipient, channel, false,
                    "Template missing or inactive"
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

            emailService.sendEmail(from, to, subject, body);

            logRepository.save(
                NotificationLogFactory.email(
                    context, recipient, channel, true, null
                )
            );

        } catch (Exception ex) {
            logRepository.save(
                NotificationLogFactory.email(
                    context, recipient, channel, false, ex.getMessage()
                )
            );
        }
    }
}

