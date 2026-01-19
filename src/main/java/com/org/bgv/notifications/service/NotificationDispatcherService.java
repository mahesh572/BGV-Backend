package com.org.bgv.notifications.service;

import org.springframework.stereotype.Service;

import com.org.bgv.notifications.NotificationEvent;
import com.org.bgv.notifications.dto.NotificationContext;
import com.org.bgv.notifications.entity.NotificationPolicy;
import com.org.bgv.notifications.entity.NotificationPolicyChannel;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationDispatcherService {

    private final NotificationPolicyResolver policyResolver;
    private final EmailNotificationService emailService;
    private final SmsNotificationService smsService;
    private final InAppNotificationService inAppService;

   
    @Transactional
    public void dispatch(NotificationEvent event, NotificationContext context) {

        NotificationPolicy policy =
                policyResolver.resolve(event, context.getCompanyId());

        if (policy == null || !policy.isActive()) {
            return;
        }

        for (NotificationPolicyChannel channel : policy.getChannels()) {

            if (!channel.isEnabled()) continue;

            switch (channel.getChannel()) {

                case EMAIL -> emailService.send(channel, context);
                case SMS -> smsService.send(channel, context);
                case IN_APP -> inAppService.create(channel, context);
            }
        }
    }
}

