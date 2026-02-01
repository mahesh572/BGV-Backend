package com.org.bgv.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.org.bgv.common.navigation.PortalType;

public class PortalAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = 1L;
    private final PortalType portal;

    // BEFORE authentication
    public PortalAuthenticationToken(
            String email,
            String password,
            PortalType portal
    ) {
        super(email, password);
        this.portal = portal;
    }

    // AFTER authentication
    public PortalAuthenticationToken(
            CustomUserDetails principal,
            PortalType portal
    ) {
        super(principal, null, principal.getAuthorities());
        this.portal = portal;
    }

    public PortalType getPortal() {
        return portal;
    }
}
