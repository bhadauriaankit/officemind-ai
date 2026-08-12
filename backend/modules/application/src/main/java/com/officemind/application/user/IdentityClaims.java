package com.officemind.application.user;

import com.officemind.domain.user.RoleName;

import java.util.Set;

public record IdentityClaims(
        String subject,
        String email,
        String displayName,
        Set<RoleName> roles
) {
}
