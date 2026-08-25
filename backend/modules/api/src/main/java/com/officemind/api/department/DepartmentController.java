package com.officemind.api.department;

import com.officemind.application.department.*;
import com.officemind.domain.department.Department;
import com.officemind.domain.shared.EntityId;
import com.officemind.api.user.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    private final CreateDepartmentUseCase createDepartmentUseCase;
    private final ListDepartmentsUseCase listDepartmentsUseCase;
    private final UpdateDepartmentUseCase updateDepartmentUseCase;
    private final DeleteDepartmentUseCase deleteDepartmentUseCase;

    public DepartmentController(CreateDepartmentUseCase createDepartmentUseCase,
                                 ListDepartmentsUseCase listDepartmentsUseCase,
                                 UpdateDepartmentUseCase updateDepartmentUseCase,
                                 DeleteDepartmentUseCase deleteDepartmentUseCase) {
        this.createDepartmentUseCase = createDepartmentUseCase;
        this.listDepartmentsUseCase = listDepartmentsUseCase;
        this.updateDepartmentUseCase = updateDepartmentUseCase;
        this.deleteDepartmentUseCase = deleteDepartmentUseCase;
    }

    @GetMapping
    public PageResponse<DepartmentResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return PageResponse.from(listDepartmentsUseCase.execute(page, size), DepartmentResponse::from);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public DepartmentResponse create(@Valid @RequestBody DepartmentRequest request) {
        Department department = createDepartmentUseCase.execute(request.name(), request.description());
        return DepartmentResponse.from(department);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public DepartmentResponse update(@PathVariable UUID id, @Valid @RequestBody DepartmentRequest request) {
        Department department = updateDepartmentUseCase.execute(EntityId.of(id), request.name(), request.description());
        return DepartmentResponse.from(department);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteDepartmentUseCase.execute(EntityId.of(id));
        return ResponseEntity.noContent().build();
    }
}
