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
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
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

        log.info(
                "📧 Email notification started | event={} | companyId={} | templateCode={}",
                context.getEvent(),
                context.getCompanyId(),
                channel.getTemplateCode()
        );

        String to = recipientResolver.resolve(recipient, context);
        if (to == null) {
            log.warn(
                    "❌ Email recipient unresolved | recipientId={} | event={}",
                    recipient.getId(),
                    context.getEvent()
            );

            logRepository.save(
                    NotificationLogFactory.email(
                            context, recipient, channel, false,
                            "Recipient email not resolved"
                    )
            );
            return;
        }

        log.debug("📨 Resolved recipient email: {}", to);

        String from = senderResolver.resolveFrom(context);
        log.debug("✉️ Resolved sender email: {}", from);

        EmailTemplate template =
                templateRepository
                        .findResolvedTemplate(
                                channel.getTemplateCode(),
                                context.getCompanyId()
                        )
                        .orElse(null);

        if (template == null) {
            log.warn(
                    "❌ Email template not found | templateCode={} | companyId={}",
                    channel.getTemplateCode(),
                    context.getCompanyId()
            );

            logRepository.save(
                    NotificationLogFactory.email(
                            context, recipient, channel, false,
                            "Template not found"
                    )
            );
            return;
        }

        if (!Boolean.TRUE.equals(template.getIsActive())) {
            log.warn(
                    "⛔ Email template inactive | templateCode={}",
                    channel.getTemplateCode()
            );

            logRepository.save(
                    NotificationLogFactory.email(
                            context, recipient, channel, false,
                            "Template inactive"
                    )
            );
            return;
        }

        try {
            log.debug("🧩 Rendering email subject and body");

            String subject = TemplateEngine.render(
                    template.getSubject(),
                    context.getVariables()
            );

            String body = TemplateEngine.render(
                    template.getBodyHtml(),
                    context.getVariables()
            );

            log.info(
                    "🚀 Sending email | from={} | to={} | body={} | subject='{}'",
                    from,
                    to,
                    body,
                    subject
            );

            emailService.sendEmail(from, to, subject, body);

            log.info(
                    "✅ Email sent successfully | to={} | templateCode={}",
                    to,
                    channel.getTemplateCode()
            );

            logRepository.save(
                    NotificationLogFactory.email(
                            context, recipient, channel, true, null
                    )
            );

        } catch (Exception ex) {
        	ex.printStackTrace();

            log.error(
                    "❌ Email send failed | to={} | templateCode={} | error={}",
                    to,
                    channel.getTemplateCode(),
                    ex.getMessage(),
                    ex
            );

            logRepository.save(
                    NotificationLogFactory.email(
                            context, recipient, channel, false, ex.getMessage()
                    )
            );
        }
    }
}
