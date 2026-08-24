package com.officemind.infrastructure.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Set;

/**
 * Accepts a JWT whose "iss" claim matches any of several trusted issuer
 * strings. Needed because this Keycloak instance is reachable two ways in
 * local dev: internally as http://keycloak:8080 (used by backend-to-backend
 * test scripts) and externally as http://localhost:8081 (used by the
 * browser). Both are the same Keycloak; only the hostname used to reach it
 * differs, so both issuer strings must be trusted.
 */
public class MultiIssuerValidator implements OAuth2TokenValidator<Jwt> {

    private final Set<String> trustedIssuers;

    public MultiIssuerValidator(Set<String> trustedIssuers) {
        this.trustedIssuers = trustedIssuers;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        String issuer = token.getIssuer() != null ? token.getIssuer().toString() : null;
        if (issuer != null && trustedIssuers.contains(issuer)) {
            return OAuth2TokenValidatorResult.success();
        }
        OAuth2Error error = new OAuth2Error(
                "invalid_issuer", "The iss claim '" + issuer + "' is not trusted", null);
        return OAuth2TokenValidatorResult.failure(error);
    }
}
