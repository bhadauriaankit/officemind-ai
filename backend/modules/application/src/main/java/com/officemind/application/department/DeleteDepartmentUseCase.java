package com.officemind.application.department;

import com.officemind.common.exception.ResourceNotFoundException;
import com.officemind.domain.shared.EntityId;
import org.springframework.stereotype.Service;

@Service
public class DeleteDepartmentUseCase {

    private final DepartmentRepositoryPort departmentRepository;

    public DeleteDepartmentUseCase(DepartmentRepositoryPort departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public void execute(EntityId id) {
        departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", id));
        departmentRepository.deleteById(id);
    }
}
