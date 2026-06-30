package com.org.bgv.user.requests;

import java.util.Set;

import com.org.bgv.entity.User;
import com.org.bgv.onboarding.entity.Company;
import com.org.bgv.user.enums.UserRegistrationSource;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RegistrationContext {

    private UserRegistrationSource source;
    

    private Company company;

    private User createdBy;

    private Set<String> roles;

    private boolean createCandidate;

    private boolean createCompanyUser;

    private boolean sendInvitation;
}
