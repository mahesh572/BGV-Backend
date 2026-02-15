package com.org.bgv.notifications.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.notifications.InAppNotification;
import com.org.bgv.notifications.InAppTemplate;
import com.org.bgv.notifications.dto.NotificationContext;
import com.org.bgv.notifications.entity.NotificationPolicyChannel;
import com.org.bgv.notifications.entity.NotificationPolicyRecipient;
import com.org.bgv.notifications.repository.InAppNotificationRepository;
import com.org.bgv.notifications.repository.InAppTemplateRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InAppNotificationService {

    private final InAppTemplateRepository templateRepository;
    private final InAppNotificationRepository notificationRepository;

    @Transactional
    public void create(
            NotificationPolicyRecipient recipient,
            NotificationPolicyChannel channel,
            NotificationContext context
    ) {

        // 1️⃣ Channel disabled → do nothing
        if (!channel.isEnabled()) {
            return;
        }

        // 2️⃣ Template code missing
        if (channel.getTemplateCode() == null) {
            return;
        }

        // 3️⃣ Resolve template (company → platform fallback)
        InAppTemplate template =
                templateRepository
                        .findResolvedTemplate(
                                channel.getTemplateCode(),
                                context.getCompanyId()
                        )
                        .orElse(null);

        if (template == null || !template.isActive()) {
            return;
        }

        // 4️⃣ Resolve recipient userId
        Long recipientUserId =
                resolveRecipient(recipient, context);

        if (recipientUserId == null) {
            return;
        }

        // 5️⃣ Create notification
        InAppNotification notification = new InAppNotification();
        notification.setRecipientUserId(recipientUserId);
        notification.setEvent(context.getEvent());
        notification.setTitle(
                TemplateEngine.render(
                        template.getTitle(),
                        context.getVariables()
                )
        );
        notification.setMessage(
                TemplateEngine.render(
                        template.getMessage(),
                        context.getVariables()
                )
        );
        notification.setDeepLink(
                template.getDeepLink() != null
                        ? TemplateEngine.render(
                                template.getDeepLink(),
                                context.getVariables()
                        )
                        : null
        );
        notification.setPriority(channel.getPriority());
        notification.setRead(false);
        notification.setCreatedAt(Instant.now());

        notificationRepository.save(notification);
    }

    private Long resolveRecipient(
            NotificationPolicyRecipient recipient,
            NotificationContext context
    ) {
        return switch (recipient.getRecipient()) {
            case CANDIDATE -> context.getCandidateUserId();
            case EMPLOYER -> context.getEmployerAdminUserId();
            case RECRUITER -> context.getRecruiterUserId();
            case VENDOR -> context.getVendorUserId();
		    default -> throw new IllegalArgumentException("Unexpected value: " + recipient.getRecipient());
        };
    }
}
