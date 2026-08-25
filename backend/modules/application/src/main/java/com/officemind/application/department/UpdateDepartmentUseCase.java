package com.officemind.application.department;

import com.officemind.common.exception.ResourceNotFoundException;
import com.officemind.domain.department.Department;
import com.officemind.domain.shared.EntityId;
import org.springframework.stereotype.Service;

@Service
public class UpdateDepartmentUseCase {

    private final DepartmentRepositoryPort departmentRepository;

    public UpdateDepartmentUseCase(DepartmentRepositoryPort departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Department execute(EntityId id, String name, String description) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", id));
        department.rename(name, description);
        return departmentRepository.save(department);
    }
}
