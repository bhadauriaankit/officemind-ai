package com.officemind.infrastructure.security;

import com.officemind.application.user.SessionRevocationPort;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Rejects any JWT whose jti has been blocklisted via logout, even though its
 * signature and expiry are otherwise still valid. Plugged into the JwtDecoder
 * as an additional validator alongside Spring's default issuer/timestamp checks.
 */
@Component
public class RevokedTokenValidator implements OAuth2TokenValidator<Jwt> {

    private static final OAuth2Error REVOKED_ERROR = new OAuth2Error(
            "token_revoked", "This token has been revoked (logged out)", null);

    private final SessionRevocationPort sessionRevocationPort;

    public RevokedTokenValidator(SessionRevocationPort sessionRevocationPort) {
        this.sessionRevocationPort = sessionRevocationPort;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        if (sessionRevocationPort.isRevoked(token.getId())) {
            return OAuth2TokenValidatorResult.failure(REVOKED_ERROR);
        }
        return OAuth2TokenValidatorResult.success();
    }
}
