package com.officemind.application.user;

import java.time.Duration;

/**
 * OfficeMind AI uses stateless JWTs, so "logging out" means blocklisting the
 * token's jti until its natural expiry rather than server-side session
 * destruction. Backed by Redis (TTL = remaining token lifetime).
 */
public interface SessionRevocationPort {

    void revoke(String tokenId, Duration remainingValidity);

    boolean isRevoked(String tokenId);
}
