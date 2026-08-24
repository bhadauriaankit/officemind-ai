package com.officemind.infrastructure.config;

import com.officemind.infrastructure.security.MultiIssuerValidator;
import com.officemind.infrastructure.security.RevokedTokenValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.util.List;
import java.util.Set;

@Configuration
public class JwtDecoderConfig {

    @Bean
    public JwtDecoder jwtDecoder(
            @Value("${officemind.keycloak.jwk-set-uri}") String jwkSetUri,
            @Value("${officemind.keycloak.internal-issuer}") String internalIssuer,
            @Value("${officemind.keycloak.public-issuer}") String publicIssuer,
            RevokedTokenValidator revokedTokenValidator) {

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        OAuth2TokenValidator<Jwt> multiIssuer = new MultiIssuerValidator(Set.of(internalIssuer, publicIssuer));
        OAuth2TokenValidator<Jwt> timestamp = new JwtTimestampValidator();
        OAuth2TokenValidator<Jwt> combined =
                new DelegatingOAuth2TokenValidator<>(List.of(multiIssuer, timestamp, revokedTokenValidator));

        decoder.setJwtValidator(combined);
        return decoder;
    }
}
