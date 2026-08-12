package com.officemind.application.user;

import com.officemind.domain.user.User;
import org.springframework.stereotype.Service;

@Service
public class ProvisionUserOnLoginUseCase {

    private final UserRepositoryPort userRepository;

    public ProvisionUserOnLoginUseCase(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(IdentityClaims claims) {
        return userRepository.findByKeycloakSubjectId(claims.subject())
                .orElseGet(() -> {
                    User user = User.provisionFromIdentityProvider(
                            claims.subject(), claims.email(), claims.displayName(), claims.roles());
                    return userRepository.save(user);
                });
    }
}
