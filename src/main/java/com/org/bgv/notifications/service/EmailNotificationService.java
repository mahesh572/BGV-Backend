package com.org.bgv.notifications.service;

import org.springframework.stereotype.Service;

import com.org.bgv.entity.EmailTemplate;
import com.org.bgv.notifications.dto.NotificationContext;
import com.org.bgv.notifications.entity.NotificationPolicyChannel;
import com.org.bgv.notifications.repository.NotificationLogRepository;
import com.org.bgv.repository.EmailTemplateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private final EmailTemplateRepository templateRepository;
    private final NotificationLogRepository logRepository;

    
    public void send(NotificationPolicyChannel channel,
                     NotificationContext context) {

        EmailTemplate template =
                templateRepository.findResolvedTemplate(
                        channel.getTemplateCode(),
                        context.getCompanyId()
                ).orElseThrow(null);

        if (template == null || !template.getIsActive()) {
            return;
        }

        String subject = TemplateEngine.render(
                template.getSubject(), context.getVariables());

        String body = TemplateEngine.render(
                template.getBodyHtml(), context.getVariables());

        // 🔁 Actual email sending (SMTP / SES)
        boolean success = true;

        logRepository.save(
                NotificationLogFactory.email(
                        context, channel, success, null
                )
        );
    }
}
