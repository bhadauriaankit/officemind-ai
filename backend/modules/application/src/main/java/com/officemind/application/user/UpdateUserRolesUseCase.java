package com.officemind.application.user;

import com.officemind.common.exception.ResourceNotFoundException;
import com.officemind.domain.shared.EntityId;
import com.officemind.domain.user.RoleName;
import com.officemind.domain.user.User;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UpdateUserRolesUseCase {

    private final UserRepositoryPort userRepository;

    public UpdateUserRolesUseCase(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(EntityId userId, Set<RoleName> newRoles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        user.changeRoles(newRoles);
        return userRepository.save(user);
    }
}
