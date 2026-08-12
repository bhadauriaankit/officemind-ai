package com.officemind.application.user;

import com.officemind.common.paging.PageResult;
import com.officemind.domain.user.User;
import org.springframework.stereotype.Service;

@Service
public class ListUsersUseCase {

    private static final int MAX_PAGE_SIZE = 100;

    private final UserRepositoryPort userRepository;

    public ListUsersUseCase(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    public PageResult<User> execute(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        return userRepository.findAll(safePage, safeSize);
    }
}
