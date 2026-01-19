package com.org.bgv.notifications.service;

import org.springframework.stereotype.Service;

import com.org.bgv.notifications.dto.NotificationContext;
import com.org.bgv.notifications.entity.NotificationPolicyChannel;
import com.org.bgv.notifications.entity.SmsTemplate;
import com.org.bgv.notifications.repository.NotificationLogRepository;
import com.org.bgv.notifications.repository.SmsTemplateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SmsNotificationService
         {

    private final SmsTemplateRepository templateRepository;
    private final NotificationLogRepository logRepository;

    
    public void send(NotificationPolicyChannel channel,
                     NotificationContext context) {

        SmsTemplate template =
                templateRepository.findResolvedTemplate(
                        channel.getTemplateCode(),
                        context.getCompanyId()
                );

        if (template == null || !template.isActive()) {
            return;
        }

        String message = TemplateEngine.render(
                template.getMessage(), context.getVariables());

        boolean success = true;

        logRepository.save(
                NotificationLogFactory.sms(
                        context, channel, success, null
                )
        );
    }
}

