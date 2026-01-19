package com.org.bgv.notifications.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.notifications.entity.NotificationPolicyChannel;

@Repository
public interface NotificationPolicyChannelRepository
        extends JpaRepository<NotificationPolicyChannel, Long> {

    List<NotificationPolicyChannel> findByPolicyId(Long policyId);
}
