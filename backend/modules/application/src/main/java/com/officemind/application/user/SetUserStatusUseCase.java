package com.officemind.application.user;

import com.officemind.common.exception.ResourceNotFoundException;
import com.officemind.domain.shared.EntityId;
import com.officemind.domain.user.User;
import org.springframework.stereotype.Service;

@Service
public class SetUserStatusUseCase {

    private final UserRepositoryPort userRepository;

    public SetUserStatusUseCase(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    public User disable(EntityId userId) {
        User user = load(userId);
        user.disable();
        return userRepository.save(user);
    }

    public User reactivate(EntityId userId) {
        User user = load(userId);
        user.reactivate();
        return userRepository.save(user);
    }

    private User load(EntityId userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }
}
