package com.officemind.application.department;

import com.officemind.common.paging.PageResult;
import com.officemind.domain.department.Department;
import org.springframework.stereotype.Service;

@Service
public class ListDepartmentsUseCase {

    private static final int MAX_PAGE_SIZE = 100;

    private final DepartmentRepositoryPort departmentRepository;

    public ListDepartmentsUseCase(DepartmentRepositoryPort departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public PageResult<Department> execute(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        return departmentRepository.findAll(safePage, safeSize);
    }
}
