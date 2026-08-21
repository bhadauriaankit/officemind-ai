package com.officemind.application.user;

import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class LogoutUseCase {

    private final SessionRevocationPort sessionRevocationPort;

    public LogoutUseCase(SessionRevocationPort sessionRevocationPort) {
        this.sessionRevocationPort = sessionRevocationPort;
    }

    public void execute(String tokenId, Duration remainingValidity) {
        if (remainingValidity == null || remainingValidity.isNegative() || remainingValidity.isZero()) {
            return; // already expired, nothing to blocklist
        }
        sessionRevocationPort.revoke(tokenId, remainingValidity);
    }
}
