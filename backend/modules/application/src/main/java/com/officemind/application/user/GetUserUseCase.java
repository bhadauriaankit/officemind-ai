package com.officemind.application.user;

import com.officemind.common.exception.ResourceNotFoundException;
import com.officemind.domain.shared.EntityId;
import com.officemind.domain.user.User;
import org.springframework.stereotype.Service;

@Service
public class GetUserUseCase {

    private final UserRepositoryPort userRepository;

    public GetUserUseCase(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(EntityId id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}
