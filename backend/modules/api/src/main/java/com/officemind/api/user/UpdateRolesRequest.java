package com.officemind.api.user;

import com.officemind.domain.user.RoleName;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record UpdateRolesRequest(
        @NotEmpty(message = "at least one role is required") Set<RoleName> roles
) {
}
