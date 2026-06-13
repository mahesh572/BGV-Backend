package com.org.bgv.vendor.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.org.bgv.common.RoleConstants;
import com.org.bgv.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActionPolicyFactory {

    private final VendorAgentActionPolicy vendorAgentActionPolicy;
    private final FieldAgentActionPolicy fieldAgentActionPolicy;

    public ActionPolicy getPolicy(User user) {

        log.info("Fetching action policy for userId={}", user.getUserId());

        Set<String> roleNames = user.getRoles()
                .stream()
                .map(r -> r.getRole().getName())
                .collect(Collectors.toSet());

        log.info("User roles : {}", roleNames);

        if (roleNames.contains(RoleConstants.ROLE_FIELD_AGENT)) {

            log.info(
                    "User {} has role '{}'. Returning FieldAgentActionPolicy",
                    user.getUserId(),
                    RoleConstants.ROLE_FIELD_AGENT);

            return fieldAgentActionPolicy;
        }

        if (roleNames.contains(RoleConstants.ROLE_VENDOR_AGENT)) {

            log.info(
                    "User {} has role '{}'. Returning VendorAgentActionPolicy",
                    user.getUserId(),
                    RoleConstants.ROLE_VENDOR_AGENT);

            return vendorAgentActionPolicy;
        }

        log.error(
                "No action policy found for userId={} with roles={}",
                user.getUserId(),
                roleNames);

        throw new IllegalArgumentException(
                "No action policy found for roles " + roleNames);
    }
}