package com.officemind.api.user;

import com.officemind.application.user.GetUserUseCase;
import com.officemind.application.user.IdentityClaims;
import com.officemind.application.user.ListUsersUseCase;
import com.officemind.application.user.ProvisionUserOnLoginUseCase;
import com.officemind.domain.shared.EntityId;
import com.officemind.domain.user.RoleName;
import com.officemind.domain.user.User;
import com.officemind.infrastructure.security.KeycloakJwtAuthenticationConverter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final ProvisionUserOnLoginUseCase provisionUserOnLoginUseCase;
    private final GetUserUseCase getUserUseCase;
    private final ListUsersUseCase listUsersUseCase;

    public UserController(ProvisionUserOnLoginUseCase provisionUserOnLoginUseCase,
                           GetUserUseCase getUserUseCase,
                           ListUsersUseCase listUsersUseCase) {
        this.provisionUserOnLoginUseCase = provisionUserOnLoginUseCase;
        this.getUserUseCase = getUserUseCase;
        this.listUsersUseCase = listUsersUseCase;
    }

    @GetMapping("/me")
    public UserResponse me(JwtAuthenticationToken authentication) {
        Jwt jwt = authentication.getToken();
        IdentityClaims claims = toIdentityClaims(jwt);
        User user = provisionUserOnLoginUseCase.execute(claims);
        return UserResponse.from(user);
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable UUID id) {
        User user = getUserUseCase.execute(EntityId.of(id));
        return UserResponse.from(user);
    }

     @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public PageResponse<UserResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return PageResponse.from(listUsersUseCase.execute(page, size), UserResponse::from);
    }
    private IdentityClaims toIdentityClaims(Jwt jwt) {
        Set<RoleName> roles = KeycloakJwtAuthenticationConverter.extractRealmRoles(jwt).stream()
                .map(this::toRoleNameSafely)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());

        return new IdentityClaims(
                jwt.getSubject(),
                jwt.getClaimAsString("email"),
                jwt.getClaimAsString("name"),
                roles
        );
    }

    private RoleName toRoleNameSafely(String role) {
        try {
            return RoleName.valueOf(role);
        } catch (IllegalArgumentException e) {
            return null; // ignore Keycloak's built-in default roles we don't model (e.g. offline_access)
        }
    }
}
