package com.officemind.application.department;

import com.officemind.common.paging.PageResult;
import com.officemind.domain.department.Department;
import com.officemind.domain.shared.EntityId;

import java.util.Optional;

public interface DepartmentRepositoryPort {

    Department save(Department department);

    Optional<Department> findById(EntityId id);

    boolean existsByName(String name);

    PageResult<Department> findAll(int page, int size);

    void deleteById(EntityId id);
}
