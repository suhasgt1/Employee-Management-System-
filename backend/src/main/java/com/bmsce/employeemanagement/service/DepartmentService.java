package com.bmsce.employeemanagement.service;

import com.bmsce.employeemanagement.dto.DepartmentRequest;
import com.bmsce.employeemanagement.dto.DepartmentResponse;
import com.bmsce.employeemanagement.entity.Department;
import com.bmsce.employeemanagement.exception.ConflictException;
import com.bmsce.employeemanagement.repository.DepartmentRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        String name = request.name().trim();
        if (departmentRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException("A department with this name already exists");
        }
        return toResponse(departmentRepository.save(new Department(name)));
    }

    private DepartmentResponse toResponse(Department department) {
        return new DepartmentResponse(department.getId(), department.getName());
    }
}
