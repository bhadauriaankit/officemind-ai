package com.officemind.infrastructure.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Keycloak puts realm roles under {@code realm_access.roles}, not in the
 * standard {@code scope}/{@code scp} claim Spring Security expects by
 * default. This converter bridges the two and applies the {@code ROLE_}
 * prefix so {@code @PreAuthorize("hasRole('ADMIN')")} works unmodified.
 */
@Component
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractRealmRoles(jwt).stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());

        return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
    }

    @SuppressWarnings("unchecked")
    public static Set<String> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null) {
            return Set.of();
        }
        List<String> roles = (List<String>) realmAccess.getOrDefault("roles", List.of());
        return Set.copyOf(roles);
    }
}
