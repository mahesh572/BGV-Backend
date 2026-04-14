package com.org.bgv.notifications.service;

import org.springframework.stereotype.Service;

import com.org.bgv.notifications.NotificationEvent;
import com.org.bgv.notifications.dto.NotificationContext;
import com.org.bgv.notifications.entity.NotificationPolicy;
import com.org.bgv.notifications.entity.NotificationPolicyChannel;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDispatcherService {

    private final NotificationPolicyResolver policyResolver;
    private final EmailNotificationService emailService;
    private final SmsNotificationService smsService;
    private final InAppNotificationService inAppService;

    @Transactional
    public void dispatch(
            NotificationEvent event,
            NotificationContext context
    ) {
try {
        log.info(
                "📣 Notification dispatch started | event={} | companyId={} | recipient={}",
                event,
                context.getCompanyId(),
                context.getUserEmailAddress()
        );

        NotificationPolicy policy =
                policyResolver.resolve(event, context.getCompanyId());

        if (policy == null) {
            log.warn(
                    "⚠️ No notification policy found | event={} | companyId={}",
                    event,
                    context.getCompanyId()
            );
            return;
        }

        if (!policy.isActive()) {
            log.info(
                    "⛔ Notification policy inactive | policyId={} | event={}",
                    policy.getId(),
                    event
            );
            return;
        }

        log.debug(
                "📜 Notification policy resolved | policyId={} | recipients={}",
                policy.getId(),
                policy.getRecipients().size()
        );

        policy.getRecipients().forEach(recipient -> {

            log.info(
                    "👤 Processing recipient | recipientId={}",
                    recipient.getId()
            );

            recipient.getChannels().forEach(channel -> {

                if (!channel.isEnabled()) {
                    log.debug(
                            "⏭ Channel disabled | recipientId={} | channel={}",
                            recipient.getId(),
                            channel.getChannel()
                    );
                    return;
                }

                try {
                    log.info(
                            "🚀 Dispatching notification | event={} | channel={} | recipientId={}",
                            event,
                            channel.getChannel(),
                            recipient.getId()
                    );

                    switch (channel.getChannel()) {

                        case EMAIL ->
                                emailService.send(recipient, channel, context);

                        case SMS ->
                                smsService.send(recipient, channel, context);

                        case IN_APP ->
                                inAppService.create(recipient, channel, context);
                    }

                    log.info(
                            "✅ Notification sent | event={} | channel={} | recipientId={}",
                            event,
                            channel.getChannel(),
                            recipient.getId()
                    );

                } catch (Exception ex) {
                    log.error(
                            "❌ Failed to send notification | event={} | channel={} | recipientId={}",
                            event,
                            channel.getChannel(),
                            recipient.getId(),
                            ex
                    );
                    // intentionally continue to next channel / recipient
                }
            });
        });

        log.info(
                "🏁 Notification dispatch completed | event={} | companyId={}",
                event,
                context.getCompanyId()
        );
}catch (Exception e) {
	e.printStackTrace();
}
    }
    
}
