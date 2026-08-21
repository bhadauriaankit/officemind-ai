package com.officemind.api.auth;

import com.officemind.application.user.LogoutUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final LogoutUseCase logoutUseCase;

    public AuthController(LogoutUseCase logoutUseCase) {
        this.logoutUseCase = logoutUseCase;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(JwtAuthenticationToken authentication) {
        Jwt jwt = authentication.getToken();
        String tokenId = jwt.getId();
        Duration remainingValidity = Duration.between(Instant.now(), jwt.getExpiresAt());
        logoutUseCase.execute(tokenId, remainingValidity);
        return ResponseEntity.noContent().build();
    }
}
