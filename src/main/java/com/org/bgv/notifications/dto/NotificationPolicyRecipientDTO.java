package com.org.bgv.notifications.dto;

import java.util.List;

import com.org.bgv.notifications.RecipientType;

import lombok.Data;

@Data
public class NotificationPolicyRecipientDTO {

    private Long id;
    private RecipientType recipient;
    private List<NotificationPolicyChannelDTO> channels;
}

